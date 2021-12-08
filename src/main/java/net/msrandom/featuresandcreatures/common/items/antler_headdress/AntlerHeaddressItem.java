package net.msrandom.featuresandcreatures.common.items.antler_headdress;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.core.FnCKeybinds;
import net.msrandom.featuresandcreatures.network.AntlerHeaddressChargePacket;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.GeoArmorItem;

public class AntlerHeaddressItem extends GeoArmorItem implements IAnimatable {
    public static final String PLAYER_CHARGE = "AntlerHeaddressCharge";
    private static final String STACK_CHARGE = "Charge";
    private static final String IS_DAMAGING = "IsDamaging";
    private static final String DAMAGE_TIMER = "AntlerHeaddressDamageTimer";
    private final AnimationFactory factory = new AnimationFactory(this);

    public AntlerHeaddressItem(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder) {
        super(materialIn, slot, builder);
    }

    @Override
    public void registerControllers(AnimationData data) {
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int p_77663_4_, boolean p_77663_5_) {
        PlayerEntity player = (PlayerEntity) entity;
        CompoundNBT data = player.getPersistentData();
        if (!stack.hasTag() || !stack.getTag().getBoolean(IS_DAMAGING)) {
            if (world.isClientSide) {
                int charge = data.getInt(PLAYER_CHARGE);
                boolean isCharging = FnCKeybinds.CHARGE_ANTLER.isDown();
                if (!player.getCooldowns().isOnCooldown(this)) {
                    if (isCharging && charge <= getMaxCharge()) {
                        charge++;
                    }

                    if (charge == Math.round(getMaxCharge() * 0.01f) || charge == getMaxCharge() / 4 || charge == getMaxCharge() / 2) {
                        world.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.LEVER_CLICK, SoundCategory.AMBIENT, 1, charge / 50F, false);
                    } else if (charge == Math.round(getMaxCharge() * 0.75f)) {
                        world.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.TRIPWIRE_CLICK_ON, SoundCategory.AMBIENT, 2, 1.5F, false);
                    } else if (charge == getMaxCharge()) {
                        world.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_CHIME, SoundCategory.AMBIENT, 30, charge / 15F, false);
                    }

                    if (!isCharging && charge > 0) {
                        FeaturesAndCreatures.NETWORK_CHANNEL.sendToServer(new AntlerHeaddressChargePacket(charge));
                        handleCharge(player, stack, charge);
                        world.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_RIPTIDE_1, SoundCategory.AMBIENT, 30, 1, false);
                        charge = 0;
                    }

                    data.putInt(PLAYER_CHARGE, charge);
                }
            }
        } else if (!world.isClientSide) {
            // I changed this to use the motion, rather than the player's look direction, since it makes more sense to push them in the player's charge direction
            Vector3d pushDirection = player.getDeltaMovement().normalize();
            int damageTimer = data.contains(DAMAGE_TIMER) ? data.getInt(DAMAGE_TIMER) : 30;
            int charge = stack.getOrCreateTag().getInt(STACK_CHARGE);
            AxisAlignedBB aabb = new AxisAlignedBB(player.blockPosition());
            for (LivingEntity victim : world.getEntitiesOfClass(LivingEntity.class, aabb)) {
                if (victim != entity) {
                    if (getDamageAmount(charge) < 1) {
                        victim.hurt(DamageSource.playerAttack(player), 1);
                    }
                    else {
                        victim.hurt(DamageSource.playerAttack(player), getDamageAmount(charge));
                    }
                    if (charge > Math.round(getMaxCharge() * 0.37f)) {
                        victim.knockback(0.3F, -pushDirection.x * 1.5, -pushDirection.z * 1.5);
                    }
                }
            }
            if (--damageTimer <= 0) {
                stack.getOrCreateTag().remove(IS_DAMAGING);
                player.getCooldowns().addCooldown(stack.getItem(), 40);
                damageTimer = 30;
            }
            data.putInt(DAMAGE_TIMER, damageTimer);
        }
        super.inventoryTick(stack, world, entity, p_77663_4_, p_77663_5_);
    }

    public float getDamageAmount(int oldCharge) {
        return oldCharge / (getMaxCharge() * 0.202f);
    }

    public int getMaxCharge() {
        return 200;
    }

    public void handleCharge(PlayerEntity player, ItemStack stack, int charge) {
        Vector3d look = player.getLookAngle();
        if (charge > Math.round(getMaxCharge() * 0.37f)) {
            double amount = charge / (getMaxCharge() * 0.37);
            player.push(look.x * amount, 0.1, look.z * amount);
            stack.getOrCreateTag().putBoolean(IS_DAMAGING, true);
            stack.getOrCreateTag().putInt(STACK_CHARGE, charge);
        } else if (charge >= 1) {
            player.push(look.x * (0.5), 0.1, look.z * (0.5));
            stack.getOrCreateTag().putInt(STACK_CHARGE, charge);
        }
    }
}
