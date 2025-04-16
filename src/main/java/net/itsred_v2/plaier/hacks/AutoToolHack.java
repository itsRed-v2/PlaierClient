package net.itsred_v2.plaier.hacks;

import java.util.ArrayList;
import java.util.List;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.AttackBlockListener;
import net.itsred_v2.plaier.utils.Toggleable;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.GameMode;

public class AutoToolHack extends Toggleable implements AttackBlockListener {

    private static final List<Item> TOOLS = List.of(
            Items.WOODEN_AXE, Items.WOODEN_SHOVEL, Items.WOODEN_PICKAXE, Items.WOODEN_HOE,
            Items.STONE_AXE, Items.STONE_SHOVEL, Items.STONE_PICKAXE, Items.STONE_HOE,
            Items.GOLDEN_AXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_PICKAXE, Items.GOLDEN_HOE,
            Items.IRON_AXE, Items.IRON_SHOVEL, Items.IRON_PICKAXE, Items.IRON_HOE,
            Items.DIAMOND_AXE, Items.DIAMOND_SHOVEL, Items.DIAMOND_PICKAXE, Items.DIAMOND_HOE,
            Items.NETHERITE_AXE, Items.NETHERITE_SHOVEL, Items.NETHERITE_PICKAXE, Items.NETHERITE_HOE
    );

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
        BlockState blockState = PlaierClient.MC.getClientWorld().getBlockState(event.pos);

        List<ItemStack> bestTools = new ArrayList<>();
        float bestMiningSpeed = 1;

        // TODO: don't select item if durability is low
        // TODO: check if offhand item is selectable
        for (int i = 0; i < inventory.getMainStacks().size(); i++) {
            ItemStack itemStack = inventory.getMainStacks().get(i);
            if (itemStack.isEmpty()) continue;

            float miningSpeed = computeMiningSpeed(itemStack, blockState);

            if (bestTools.isEmpty() || miningSpeed > bestMiningSpeed) {
                bestTools.clear();
                bestTools.add(itemStack);
                bestMiningSpeed = miningSpeed;
            } else if (miningSpeed == bestMiningSpeed) {
                bestTools.add(itemStack);
            }
        }

        // If the best mining speed is 1, that means no tool in the inventory
        // is able to mine faster than bare hands: return early.
        if (bestMiningSpeed == 1) {
            return;
        }

        // If one of the best tools is already selected, return early
        if (bestTools.contains(inventory.getSelectedStack())) {
            return;
        }

        // Check if one of the best tools is in the hot bar,
        // if it is, select it and return early.
        for (int i = 0; i < 9; i++) {
            if (bestTools.contains(inventory.getStack(i))) {
                inventory.setSelectedSlot(i);
                return;
            }
        }

        // Pick one of the best tools from the inventory
        int bestToolIndex = inventory.getSlotWithStack(bestTools.getFirst());

        int hotbarSlot;
        if (inventory.getSelectedStack().isEmpty()) {
            hotbarSlot = inventory.getSelectedSlot();
        } else {
            hotbarSlot = getSwappableHotbarSlot(inventory);
            inventory.setSelectedSlot(hotbarSlot);
        }

        PlaierClient.MC.getInteractionManager().clickSlot(player.playerScreenHandler.syncId, bestToolIndex, hotbarSlot, SlotActionType.SWAP, player);
    }

    private static int getSwappableHotbarSlot(PlayerInventory inventory) {
        DefaultedList<ItemStack> main = inventory.getMainStacks();
        for (int i = 0; i < 9; i++) {
            if (main.get(i).isEmpty()) return i;
        }

        for (int i = 0; i < 9; i++) {
            if (TOOLS.contains(main.get(i).getItem())) return i;
        }

        return inventory.getSelectedSlot();
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
