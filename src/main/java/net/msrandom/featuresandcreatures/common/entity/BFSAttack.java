package net.msrandom.featuresandcreatures.common.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.msrandom.featuresandcreatures.common.item.FeaturesCreaturesItems;
import net.msrandom.featuresandcreatures.core.FnCEntities;

public class BFSAttack extends ThrowableItemProjectile {
    public BFSAttack(Level p_37392_) {
        super(FnCEntities.BFS_ATTACK.get(), p_37392_);
    }

    public BFSAttack(Level p_37399_, LivingEntity p_37400_) {
        super(FnCEntities.BFS_ATTACK.get(), p_37400_, p_37399_);
    }

    @Override
    protected Item getDefaultItem() {
        return FeaturesCreaturesItems.BFS_ATTACK_ITEM.get();
    }

    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        Entity entity = hitResult.getEntity();
        entity.hurt(DamageSource.thrown(this, this.getOwner()), 3.5F);
        if (entity instanceof LivingEntity le){
            le.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 100));
        }
    }

    protected void onHit(HitResult p_37406_) {
        super.onHit(p_37406_);
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte)3);
            this.discard();
        }

    }
}
