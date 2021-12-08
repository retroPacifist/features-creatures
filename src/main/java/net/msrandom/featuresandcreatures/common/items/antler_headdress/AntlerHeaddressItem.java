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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.msrandom.featuresandcreatures.core.FnCItems;
import net.msrandom.featuresandcreatures.core.FnCKeybinds;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.GeoArmorItem;

import java.util.List;

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
        Vector3d look = player.getLookAngle();
        boolean isDoingDamage = stack.hasTag() && stack.getTag().getBoolean(IS_DAMAGING);
        CompoundNBT data = player.getPersistentData();
        if (!isDoingDamage) {
            if (world.isClientSide) {
                int charge = data.getInt(PLAYER_CHARGE);
                boolean isCharging = FnCKeybinds.CHARGE_ANTLER.isDown();
                if (!player.getCooldowns().isOnCooldown(stack.getItem())) {
                    if (isCharging && charge < getMaxCharge()) {
                        charge++;
                    }

                    if (charge == Math.round(getMaxCharge() * 0.01f) || charge == getMaxCharge() / 4 || charge == getMaxCharge() / 2) {
                        world.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.LEVER_CLICK, SoundCategory.AMBIENT, 1, charge / 50F, false);
                    } else if (charge == Math.round(getMaxCharge() * 0.75f)) {
                        world.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.TRIPWIRE_CLICK_ON, SoundCategory.AMBIENT, 2, 1.5F, false);
                    }
                    if (charge == getMaxCharge()) {
                        world.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_CHIME, SoundCategory.AMBIENT, 30, charge / 15F, false);
                    }

                    if (!isCharging) {
                        if (charge > Math.round(getMaxCharge() * 0.37f)) {
                            double amount = charge / (getMaxCharge() * 0.37);
                            player.push(look.x * amount, 0.1, look.z * amount);
                            world.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_RIPTIDE_1, SoundCategory.AMBIENT, 30, 1, false);
                            stack.getOrCreateTag().putBoolean(IS_DAMAGING, true);
                            stack.getOrCreateTag().putInt(STACK_CHARGE, charge);
                            charge = 0;
                        } else if (charge >= 1) {
                            player.push(look.x * (0.5), 0.1, look.z * (0.5));
                            world.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_RIPTIDE_1, SoundCategory.AMBIENT, 30, 1, false);
                            stack.getOrCreateTag().putInt(STACK_CHARGE, charge);
                            charge = 0;
                        } else {
                            charge = 0;
                        }
                    }

                    data.putInt(PLAYER_CHARGE, charge);
                }
            }
        } else if (!world.isClientSide) {
            int damageTimer = data.contains(DAMAGE_TIMER) ? data.getInt(DAMAGE_TIMER) : 30;
            int charge = stack.getOrCreateTag().getInt(STACK_CHARGE);
            AxisAlignedBB aabb = new AxisAlignedBB(player.blockPosition());
            for (LivingEntity entity2 : world.getEntitiesOfClass(LivingEntity.class, aabb)) {
                if (entity2 != entity) {
                    if (getDamageAmount(charge) < 1) {
                        entity2.hurt(DamageSource.playerAttack(player), 1);
                    }
                    else {
                        entity2.hurt(DamageSource.playerAttack(player), getDamageAmount(charge));
                    }
                    if (charge > Math.round(getMaxCharge() * 0.37f)) {
                        entity2.knockback(0.3F, -look.x * 1.5, -look.z * 1.5);
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
}
