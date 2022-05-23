package net.msrandom.featuresandcreatures.common.item;

import net.minecraft.Util;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import retropacifist.featuresandcreatures.reg.RegistryObject;
import software.bernie.geckolib3.core.IAnimatable;

import java.util.ServiceLoader;

public interface PlatformItemHandler {

    PlatformItemHandler INSTANCE = Util.make(() -> {
        final var loader = ServiceLoader.load(PlatformItemHandler.class);
        final var it = loader.iterator();
        if (!it.hasNext()) {
            throw new RuntimeException("No PlatformItemHandler was found on the classpath!");
        } else {
            final PlatformItemHandler factory = it.next();
            if (it.hasNext()) {
                throw new RuntimeException("More than one PlatformItemHandler was found on the classpath!");
            }
            return factory;
        }
    });

    <ITEM extends ArmorItem & IAnimatable> ITEM getAntlerHeaddressItem(ArmorMaterial material, EquipmentSlot slot, Item.Properties builder);

    <ITEM extends ArmorItem & IAnimatable> ITEM getLunarHeaddressItem(ArmorMaterial material, EquipmentSlot slot, Item.Properties builder);

    <ITEM extends SpawnEggItem, T extends Mob> ITEM getSpawnEggItem(RegistryObject<EntityType<T>> object, int base, int spots, Item.Properties builder);
}
