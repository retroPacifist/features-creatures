package net.msrandom.featuresandcreatures.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.core.FnCItems;
import net.msrandom.featuresandcreatures.core.FnCKeybinds;
import net.msrandom.featuresandcreatures.core.FnCSounds;
import net.msrandom.featuresandcreatures.core.FnCTriggers;
import net.msrandom.featuresandcreatures.network.AntlerHeaddressChargePacket;
import net.msrandom.featuresandcreatures.network.NetworkHandler;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.GeoArmorItem;

public class AntlerHeaddressItem extends GeoArmorItem implements IAnimatable {
    private static final IArmorMaterial MATERIAL = new FnCArmorMaterial(
            new ResourceLocation(FeaturesAndCreatures.MOD_ID, "headdress"),
            new int[]{0, 111, 111, 37},
            new int[]{0, 0, 0, 0},
            420,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            0.0F,
            0.0F,
            () -> Ingredient.of(FnCItems.ANTLER)
    );

    private static final String DATA_PREFIX = "AntlerHeaddress";
    public static final String CURRENT_CHARGE = DATA_PREFIX + "Charge";
    private static final String LAST_CHARGE = DATA_PREFIX + "LastCharge";
    private static final String IS_DAMAGING = DATA_PREFIX + "IsDamaging";
    private static final String DAMAGE_TIMER = DATA_PREFIX + "DamageTimer";
    private final AnimationFactory factory = new AnimationFactory(this);

    public AntlerHeaddressItem(EquipmentSlotType slot, Properties builder) {
        super(MATERIAL, slot, builder);
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
        if (world.isClientSide) {
            int charge = data.getInt(CURRENT_CHARGE);
            boolean isCharging = FnCKeybinds.CHARGE_ANTLER.isDown();
            if (!player.getCooldowns().isOnCooldown(this)) {
                if (isCharging && charge <= getMaxCharge()) {
                    charge++;
                }
                if (charge == Math.round(getMaxCharge() * 0.01f) || charge == getMaxCharge() / 4 || charge == getMaxCharge() / 2) {
                    world.playLocalSound(player.getX(), player.getY(), player.getZ(), FnCSounds.ANTLER_HEADDRESS_CHARGE, SoundCategory.AMBIENT, 1, charge / 50F, false);
                } else if (charge == Math.round(getMaxCharge() * 0.75f)) {
                    world.playLocalSound(player.getX(), player.getY(), player.getZ(), FnCSounds.ANTLER_HEADDRESS_CHARGE, SoundCategory.AMBIENT, 2, 1.5F, false);
                } else if (charge == getMaxCharge()) {
                    world.playLocalSound(player.getX(), player.getY(), player.getZ(), FnCSounds.ANTLER_HEADDRESS_FINISHED_CHARGING, SoundCategory.AMBIENT, 30, charge / 15F, false);
                }

                if (!isCharging && charge > 0) {
                    NetworkHandler.SIMPLE_CHANNEL.sendToServer(new AntlerHeaddressChargePacket(charge));
                    handleCharge(player, charge);
                    world.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_RIPTIDE_1, SoundCategory.AMBIENT, 30, 1, false);
                    charge = 0;
                }

                data.putInt(CURRENT_CHARGE, charge);
            }
        } else if (data.getBoolean(IS_DAMAGING)) {
            // I changed this to use the motion, rather than the player's look direction, since it makes more sense to push them in the player's charge direction
            Vector3d pushDirection = player.getDeltaMovement().normalize();
            int damageTimer = data.contains(DAMAGE_TIMER) ? data.getInt(DAMAGE_TIMER) : 30;
            int charge = data.getInt(LAST_CHARGE);
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) entity;
            usedHeaddress(serverplayerentity, world, stack);
            AxisAlignedBB aabb = new AxisAlignedBB(player.blockPosition());
            for (LivingEntity victim : world.getEntitiesOfClass(LivingEntity.class, aabb)) {
                if (victim != entity) {
                    if (getDamageAmount(charge) < 1) {
                        victim.hurt(DamageSource.playerAttack(player), 1);
                    } else {
                        victim.hurt(DamageSource.playerAttack(player), getDamageAmount(charge));
                    }
                    if (charge > Math.round(getMaxCharge() * 0.37f)) {
                        victim.knockback(0.3F, -pushDirection.x * 1.5, -pushDirection.z * 1.5);
                    }
                    world.playSound(null, victim.blockPosition(), FnCSounds.ANTLER_HEADDRESS_ATTACK_STRONG, player.getSoundSource(), (float) (player.getDeltaMovement().length() * 3), 1f);
                }
            }
            if (--damageTimer <= 0) {
                data.remove(IS_DAMAGING);
                damageTimer = 30;
            }
            data.putInt(DAMAGE_TIMER, damageTimer);
        }
        super.inventoryTick(stack, world, entity, p_77663_4_, p_77663_5_);
    }

    public static void usedHeaddress(ServerPlayerEntity entity, World world, ItemStack stack) {
        if (!world.isClientSide) {
            FnCTriggers.USE_ANTLER.trigger(entity, stack);
        }
        entity.awardStat(Stats.ITEM_USED.get(stack.getItem()));
    }

    public float getDamageAmount(int oldCharge) {
        return oldCharge / (getMaxCharge() * 0.202f);
    }

    public int getMaxCharge() {
        return 200;
    }

    public void handleCharge(PlayerEntity player, int charge) {
        player.getCooldowns().addCooldown(this, 40);
        Vector3d look = player.getLookAngle();
        if (charge > Math.round(getMaxCharge() * 0.37f)) {
            double amount = charge / (getMaxCharge() * 0.37);
            player.push(look.x * amount, 0.1, look.z * amount);
            if (!player.level.isClientSide) {
                player.getPersistentData().putBoolean(IS_DAMAGING, true);
                player.getPersistentData().putInt(LAST_CHARGE, charge);
            }
        } else if (charge >= 1) {
            player.push(look.x * (0.5), 0.1, look.z * (0.5));
            if (!player.level.isClientSide)
                player.getPersistentData().putInt(LAST_CHARGE, charge);
        }
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlotType slot) {
        return ImmutableMultimap.of();
    }
}
