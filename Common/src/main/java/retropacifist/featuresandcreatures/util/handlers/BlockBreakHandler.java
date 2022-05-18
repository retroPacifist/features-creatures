package retropacifist.featuresandcreatures.util.handlers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import retropacifist.featuresandcreatures.common.entity.BlackForestSpirit;

import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlockBreakHandler {

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
}
