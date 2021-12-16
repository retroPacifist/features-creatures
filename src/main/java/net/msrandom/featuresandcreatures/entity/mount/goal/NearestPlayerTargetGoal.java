package net.msrandom.featuresandcreatures.entity.mount.goal;

import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.msrandom.featuresandcreatures.entity.mount.AbstractAngryMountEntity;

public final class NearestPlayerTargetGoal extends NearestAttackableTargetGoal<PlayerEntity> {

    public NearestPlayerTargetGoal(AbstractAngryMountEntity mount) {
        super(mount, PlayerEntity.class, 20, true, true, mount::isAngryAt);
    }
}
