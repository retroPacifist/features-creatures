package retropacifist.featuresandcreatures.common.item;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.IItemRenderProperties;
import retropacifist.featuresandcreatures.core.FnCKeybinds;
import retropacifist.featuresandcreatures.core.FnCSounds;
import retropacifist.featuresandcreatures.network.AntlerHeaddressChargePacket;
import retropacifist.featuresandcreatures.platform.ModPlatform;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class AntlerHeaddressItemForgeImpl extends AntlerHeaddressItem {

    public AntlerHeaddressItemForgeImpl(ArmorMaterial material, EquipmentSlot slot, Properties builder) {
        super(material, slot, builder);
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IItemRenderProperties() {
            @Override
            public HumanoidModel<?> getArmorModel(LivingEntity entityLiving, ItemStack itemStack,
                                                  EquipmentSlot armorSlot, HumanoidModel<?> _default) {
                return (HumanoidModel<?>) GeoArmorRenderer.getRenderer(AntlerHeaddressItemForgeImpl.this.getClass()).applyEntityStats(_default)
                        .applySlot(armorSlot).setCurrentItem(entityLiving, itemStack, armorSlot);
            }
        });
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Nullable
    @Override
    public final String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        Class<? extends ArmorItem> clazz = this.getClass();
        GeoArmorRenderer renderer = GeoArmorRenderer.getRenderer(clazz);
        return renderer.getTextureLocation((ArmorItem) stack.getItem()).toString();
    }


    @Override
    public void onArmorTick(ItemStack stack, Level world, Player player) {
        CompoundTag data = player.getPersistentData();
        if (world.isClientSide) {
            int charge = data.getInt(CURRENT_CHARGE);
            boolean isCharging = FnCKeybinds.CHARGE_ANTLER.isDown();
            if (!player.getCooldowns().isOnCooldown(this)) {
                if (isCharging && charge <= getMaxCharge()) {
                    charge++;
                }
                if (charge == Math.round(getMaxCharge() * 0.01f) || charge == getMaxCharge() / 4 || charge == getMaxCharge() / 2) {
                    world.playLocalSound(player.getX(), player.getY(), player.getZ(), FnCSounds.ANTLER_HEADDRESS_CHARGE.get(), SoundSource.AMBIENT, 1, charge / 50F, false);
                } else if (charge == Math.round(getMaxCharge() * 0.75f)) {
                    world.playLocalSound(player.getX(), player.getY(), player.getZ(), FnCSounds.ANTLER_HEADDRESS_CHARGE.get(), SoundSource.AMBIENT, 2, 1.5F, false);
                } else if (charge == getMaxCharge()) {
                    world.playLocalSound(player.getX(), player.getY(), player.getZ(), FnCSounds.ANTLER_HEADDRESS_FINISHED_CHARGING.get(), SoundSource.AMBIENT, 30, charge / 15F, false);
                }

                if (!isCharging && charge > 0) {
                    ModPlatform.INSTANCE.sendToServer(new AntlerHeaddressChargePacket(charge));
                    handleCharge(player, charge);
                    world.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_RIPTIDE_1, SoundSource.AMBIENT, 30, 1, false);
                    charge = 0;
                }

                data.putInt(CURRENT_CHARGE, charge);
            }
        } else if (data.getBoolean(IS_DAMAGING)) {
            // I changed this to use the motion, rather than the player's look direction, since it makes more sense to push them in the player's charge direction
            Vec3 pushDirection = player.getDeltaMovement().normalize();
            int damageTimer = data.contains(DAMAGE_TIMER) ? data.getInt(DAMAGE_TIMER) : 30;
            int charge = data.getInt(LAST_CHARGE);
            ServerPlayer serverplayerentity = (ServerPlayer) player;
            usedHeaddress(serverplayerentity, world, stack);
            AABB aabb = new AABB(player.blockPosition());
            for (LivingEntity victim : world.getEntitiesOfClass(LivingEntity.class, aabb)) {
                if (victim != player) {
                    if (getDamageAmount(charge) < 1) {
                        victim.hurt(DamageSource.playerAttack(player), 1);
                    } else {
                        victim.hurt(DamageSource.playerAttack(player), getDamageAmount(charge));
                    }
                    if (charge > Math.round(getMaxCharge() * 0.37f)) {
                        victim.knockback(knockbackValue(), -pushDirection.x * 1.5, -pushDirection.z * 1.5);
                    }
                    world.playSound(null, victim.blockPosition(), FnCSounds.ANTLER_HEADDRESS_ATTACK_STRONG.get(), player.getSoundSource(), (float) (player.getDeltaMovement().length() * 3), 1f);
                }
            }
            if (--damageTimer <= 0) {
                data.remove(IS_DAMAGING);
                damageTimer = 30;
            }
            data.putInt(DAMAGE_TIMER, damageTimer);
        }
        super.onArmorTick(stack, world, player);
    }

}
