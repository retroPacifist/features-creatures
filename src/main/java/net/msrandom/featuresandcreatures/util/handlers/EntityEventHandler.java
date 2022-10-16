package net.msrandom.featuresandcreatures.util.handlers;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.msrandom.featuresandcreatures.common.entity.Gup;
import net.msrandom.featuresandcreatures.common.entity.Jockey;

@Mod.EventBusSubscriber
public class EntityEventHandler {
    @SubscribeEvent
    public static void onLivingAttacked(LivingAttackEvent event) {
        LivingEntity target = event.getEntityLiving();

        if (target instanceof Jockey && target.equals(event.getSource().getEntity())) {
            event.setCanceled(true);
            return;
        }

        if (target.hasPassenger((passenger) -> passenger instanceof Jockey))
		{

            target.getPassengers().stream().filter(entity -> entity instanceof Jockey).forEach(jockey ->
            {
                if (jockey.equals(event.getSource().getEntity())) {
                    event.setCanceled(true);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onStruckByLightning(EntityStruckByLightningEvent event) {
        if (event.getEntity() instanceof Slime) {
            Gup.spawnFromStruckSlime((Slime)event.getEntity());
        }
    }
}
