package net.msrandom.featuresandcreatures.util.handlers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherWartBlock;
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
        if (event.getState().getBlock().getRegistryName().toString().contains("log")){
            List<BlackForestSpirit> list = player.level.getEntitiesOfClass(BlackForestSpirit.class, new AABB(player.blockPosition()).inflate(30));
            if (list.isEmpty()) return;
            for (BlackForestSpirit spirit : list){
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
        if (event.getWorld().isClientSide() || !event.getState().is(Blocks.NETHER_WART)) return;
        int age = event.getState().getValue(NetherWartBlock.AGE);
        if (age < NetherWartBlock.MAX_AGE && !event.getWorld().getEntitiesOfClass(BrimstoneGolem.class, new AABB(event.getPos()).inflate(16)).isEmpty()) {
            event.getWorld().setBlock(event.getPos(), event.getState().setValue(NetherWartBlock.AGE, age + 1), Block.UPDATE_CLIENTS);
        }
    }
}
