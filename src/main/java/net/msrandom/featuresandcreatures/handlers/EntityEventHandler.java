package net.msrandom.featuresandcreatures.handlers;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.msrandom.featuresandcreatures.entity.Jockey;

@Mod.EventBusSubscriber
public class EntityEventHandler
{
	@SubscribeEvent
	public static void onLivingAttacked(LivingAttackEvent event)
	{
		LivingEntity target = event.getEntityLiving();

		if(target instanceof Jockey && target.equals(event.getSource().getEntity()))
		{
			event.setCanceled(true);
			return;
		}

		if(target.hasPassenger(Jockey.class))
			target.getPassengers().stream().filter(entity -> entity instanceof Jockey).forEach(jockey ->
			{
				if(jockey.equals(event.getSource().getEntity()))
				{
					event.setCanceled(true);
					return;
				}
			});
	}
}
