package net.msrandom.featuresandcreatures.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class FeaturesCreaturesOreBlockEntity extends BlockEntity {

    public FeaturesCreaturesOreBlockEntity(BlockPos pos, BlockState state) {
        super(FeaturesCreaturesBlockEntities.ORE.get(), pos, state);
    }
}
