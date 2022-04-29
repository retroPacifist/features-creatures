package net.msrandom.featuresandcreatures.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.msrandom.featuresandcreatures.common.block.entity.FeaturesCreaturesBlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class FeaturesCreaturesOre extends Block implements EntityBlock {
    private static final BooleanProperty REVEALED = BooleanProperty.create("revealed");

    private final Duration duration;

    public FeaturesCreaturesOre(Duration duration, Properties properties) {
        super(properties);
        this.duration = duration;

        registerDefaultState(stateDefinition.any().setValue(REVEALED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(REVEALED));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return FeaturesCreaturesBlockEntities.ORE.get().create(pos, state);
    }

    @Override
    public <T extends BlockEntity> @NotNull BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return FeaturesCreaturesOre::tick;
    }

    private static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T entity) {
        if (!level.isClientSide) {
            Duration duration = ((FeaturesCreaturesOre) state.getBlock()).duration;

            boolean revealed = state.getValue(REVEALED);

            long time = level.getDayTime() % 24000L;

            if (time >= duration.start && time <= duration.end) {
                if (!revealed)
                    setBlock(level, pos, state, true);

            } else if (revealed)
                setBlock(level, pos, state, false);
        }
    }

    private static void setBlock(Level level, BlockPos pos, BlockState state, boolean revealed) {
        level.setBlock(pos, state.setValue(REVEALED, revealed), UPDATE_ALL);
    }

    public record Duration(long start, long end) {
        public static final Duration DAWN = new Duration(0L, 1000L);
        public static final Duration SUNSET = new Duration(11000L, 12000L);
        public static final Duration MIDNIGHT = new Duration(17750L, 19250L);
    }
}
