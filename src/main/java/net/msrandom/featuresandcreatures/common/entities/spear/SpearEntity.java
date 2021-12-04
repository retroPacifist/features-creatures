package net.msrandom.featuresandcreatures.common.entities.spear;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.msrandom.featuresandcreatures.core.FnCEntities;
import net.msrandom.featuresandcreatures.core.FnCItems;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpearEntity extends AbstractArrowEntity {
    private boolean dealtDamage;
    private final ItemStack thrownStack = new ItemStack(FnCItems.SPEAR.get());

    public SpearEntity(EntityType<? extends AbstractArrowEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public SpearEntity(World worldIn, LivingEntity thrower, ItemStack thrownStackIn) {
        super(FnCEntities.SPEAR.get(), thrower, worldIn);
    }

    protected ItemStack getPickupItem() {
        return this.thrownStack.copy();
    }

    @Nullable
    protected EntityRayTraceResult findHitEntity(Vector3d startVec, Vector3d endVec) {
        return this.dealtDamage ? null : super.findHitEntity(startVec, endVec);
    }

    @Override
    @Nonnull
    public IPacket getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult result) {
        Entity entity = result.getEntity();
        float f = 8.0F;
        if (entity instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity) entity;
            f += EnchantmentHelper.getDamageBonus(this.thrownStack, livingentity.getMobType());
        }

        Entity entity1 = this.getOwner();
        DamageSource damagesource = DamageSource.trident(this, (Entity) (entity1 == null ? this : entity1));
        this.dealtDamage = true;
        SoundEvent soundevent = SoundEvents.TRIDENT_HIT;
        if (entity.hurt(damagesource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (entity instanceof LivingEntity) {
                LivingEntity livingentity1 = (LivingEntity) entity;
                if (entity1 instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(livingentity1, entity1);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity) entity1, livingentity1);
                }

                this.doPostHurtEffects(livingentity1);
            }
        }
        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
        this.playSound(soundevent, 1.0F, 1.0F);
    }

    public void playerTouch(PlayerEntity player) {
        Entity entity = this.getOwner();
        if (entity == null || entity.getUUID() == player.getUUID()) {
            super.playerTouch(player);
        }
    }
}

