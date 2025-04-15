package net.itsred_v2.plaier.hacks;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.AttackBlockListener;
import net.itsred_v2.plaier.utils.Toggleable;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

import java.util.ArrayList;
import java.util.List;

public class AutoToolHack extends Toggleable implements AttackBlockListener {

    private static double tmpEfficiency = 0;

    @Override
    protected void onEnable() {
        PlaierClient.getEventManager().add(AttackBlockListener.class, this);
    }

    @Override
    protected void onDisable() {
        PlaierClient.getEventManager().remove(AttackBlockListener.class, this);
    }

    @Override
    public void onAttackBlock(AttackBlockEvent event) {
        if (PlaierClient.MC.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        ClientPlayerEntity player = PlaierClient.getPlayer();
        PlayerInventory inventory = player.getInventory();
        BlockState blockState = PlaierClient.MC.getWorld().getBlockState(event.pos);

        List<ItemStack> bestTools = new ArrayList<>();
        float bestMiningSpeed = 1;

        for (int i = 0; i < inventory.main.size(); i++) {
            if (inventory.main.get(i).isEmpty()) continue;

            ItemStack item = inventory.main.get(i);
            float miningSpeed = computeMiningSpeed(item, blockState);

            if (bestTools.isEmpty() || miningSpeed > bestMiningSpeed) {
                bestTools.clear();
                bestTools.add(item);
                bestMiningSpeed = miningSpeed;
            } else if (miningSpeed == bestMiningSpeed) {
                bestTools.add(item);
            }
        }

        // If the best mining speed is 1, that means no tool in the inventory
        // is able to mine faster than bare hands: return early.
        if (bestMiningSpeed == 1) {
            return;
        }

        // If one of the best tools is already selected, return early
        if (bestTools.contains(inventory.getMainHandStack())) {
            return;
        }

        // Check if one of the best tools is in the hot bar,
        // if it is, select it and return early.
        for (int i = 0; i < 9; i++) {
            if (bestTools.contains(inventory.getStack(i))) {
                inventory.selectedSlot = i;
                return;
            }
        }

        // Pick one of the best tools from the inventory
        int bestToolIndex = inventory.getSlotWithStack(bestTools.get(0));
        PlaierClient.MC.getInteractionManager().pickFromInventory(bestToolIndex);
    }

    private static float computeMiningSpeed(ItemStack item, BlockState state) {
        float miningSpeed = item.getMiningSpeedMultiplier(state);
        // efficiency must be applied only if the tool is suitable for this block,
        // i.e. if mining speed multiplier > 1
        if (miningSpeed > 1.0f) {
            miningSpeed += (float) getEfficiency(item);
        }
        return miningSpeed;
    }

    private static double getEfficiency(ItemStack item) {
        // We are forced to use a static field as a variable here, otherwise
        // the callback is unable to modify the variable's value.
        tmpEfficiency = 0;
        item.applyAttributeModifier(AttributeModifierSlot.MAINHAND, (attribute, modifier) -> {
            if (modifier.idMatches(Identifier.ofVanilla("enchantment.efficiency/mainhand"))) {
                tmpEfficiency = modifier.value();
            }
        });
        return tmpEfficiency;
    }

}
