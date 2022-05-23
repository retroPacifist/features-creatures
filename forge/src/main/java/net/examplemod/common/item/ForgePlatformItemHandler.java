package net.examplemod.common.item;

import com.google.auto.service.AutoService;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.msrandom.featuresandcreatures.common.item.PlatformItemHandler;
import retropacifist.featuresandcreatures.reg.RegistryObject;
import software.bernie.geckolib3.core.IAnimatable;

@AutoService(PlatformItemHandler.class)
public class ForgePlatformItemHandler implements PlatformItemHandler {

    @Override
    public <ITEM extends ArmorItem & IAnimatable> ITEM getAntlerHeaddressItem(ArmorMaterial material, EquipmentSlot slot, Item.Properties builder) {
        return (ITEM) new AntlerHeaddressItemForgeImpl(material, slot, builder);
    }

    @Override
    public <ITEM extends ArmorItem & IAnimatable> ITEM getLunarHeaddressItem(ArmorMaterial material, EquipmentSlot slot, Item.Properties builder) {
        return (ITEM) new LunarHeadDressItemForgeImpl(material, slot, builder);
    }

    @Override
    public <ITEM extends SpawnEggItem, T extends Mob> ITEM getSpawnEggItem(RegistryObject<EntityType<T>> object, int base, int spots, Item.Properties builder) {
        return (ITEM) new ForgeSpawnEggItem(object, base, spots, builder);
    }
}
