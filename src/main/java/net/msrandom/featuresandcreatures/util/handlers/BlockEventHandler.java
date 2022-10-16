package net.msrandom.featuresandcreatures.util.handlers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.msrandom.featuresandcreatures.common.entity.BlackForestSpirit;
import net.msrandom.featuresandcreatures.common.entity.BrimstoneGolem;

import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlockEventHandler {

    @SubscribeEvent
    public static void addTargetOnBlockBreak(BlockEvent.BreakEvent event){
        Player player = event.getPlayer();
        if (event.getState().getMaterial() == Material.WOOD && event.getState().getBlock().getRegistryName().toString().contains("log")){
            for (BlackForestSpirit spirit : player.level.getEntitiesOfClass(BlackForestSpirit.class, new AABB(player.blockPosition()).inflate(30))){
                spirit.setPersistentAngerTarget(player.getUUID());
                spirit.setRemainingPersistentAngerTime(500);
            }
        }
    }

    @SubscribeEvent
    public static void checkSpawnBrimstoneGolem(BlockEvent.EntityPlaceEvent event){
        BrimstoneGolem.trySpawn(event);
    }

    @SubscribeEvent
    public static void onCropGrowEvent(BlockEvent.CropGrowEvent.Post event) {
        BlockState state = event.getState();
        LevelAccessor level = event.getWorld();
        if (!level.isClientSide() && state.is(Blocks.NETHER_WART)) {
            BlockPos pos = event.getPos();
            int age = state.getValue(NetherWartBlock.AGE);
            if (age < NetherWartBlock.MAX_AGE && !level.getEntitiesOfClass(BrimstoneGolem.class, new AABB(pos).inflate(16)).isEmpty()) {
                level.setBlock(pos, state.setValue(NetherWartBlock.AGE, age + 1), Block.UPDATE_CLIENTS);
                ((ServerLevel)level).sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 0.25, 0.25, 0.25, 0.01);
            }
        }
    }
}
