package com.dikiytechies.rotp_bb.action.non_stand;

import com.dikiytechies.rotp_bb.BBAddonConfig;
import com.dikiytechies.rotp_bb.item.ModItems;
import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.non_stand.NonStandAction;
import com.github.standobyte.jojo.action.non_stand.VampirismAction;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.world.World;

import java.util.UUID;

import static com.github.standobyte.jojo.client.sound.ClientTickingSoundsHelper.playEntitySound;


public class BloodRefill extends VampirismAction {
    public BloodRefill(NonStandAction.Builder builder) { super(builder); }

    @Override
    protected ActionConditionResult checkSpecificConditions(LivingEntity user, INonStandPower power, ActionTarget target) {
        ItemStack offhandItem = user.getOffhandItem();
        if (!offhandItem.getItem().equals(Items.GLASS_BOTTLE)) {
            return conditionMessage("no_volume");
        }
        if (power.getEnergy() < (power.getMaxEnergy() / 10) / 100 * BBAddonConfig.fillBottleMultiplier.get()) {
            return conditionMessage("no_energy_vampirism");
        }
        return ActionConditionResult.POSITIVE;
    }

    @Override
    protected void perform(World world, LivingEntity user, INonStandPower power, ActionTarget target) {
        if (!world.isClientSide()) {
            ItemStack offhandItem = user.getOffhandItem();
            PlayerEntity player = (PlayerEntity) user;
            ItemStack bottle = new ItemStack(ModItems.BOTTLE.get(), 1);
            boolean avoid = false;
            if (player.getMainHandItem().isEmpty() && !player.inventory.contains(bottle)) {
                player.inventory.setItem(player.inventory.getSuitableHotbarSlot(), new ItemStack(ModItems.BOTTLE.get()));
                avoid = true;
            }
            if (user instanceof PlayerEntity && !player.isCreative()) {
                offhandItem.shrink(1);
                if (!avoid && !player.inventory.add(new ItemStack(ModItems.BOTTLE.get()))) {
                    player.drop(bottle, false);
                }
            }
            power.consumeEnergy((power.getMaxEnergy() / 10) / 100 * BBAddonConfig.fillBottleMultiplier.get());
            world.playSound(null, user.blockPosition(), SoundEvents.BOTTLE_FILL, SoundCategory.PLAYERS, 0.75f, 1.0F);
        }
    }

    @Override
    protected int maxCuringStage() { return 3; }
}
