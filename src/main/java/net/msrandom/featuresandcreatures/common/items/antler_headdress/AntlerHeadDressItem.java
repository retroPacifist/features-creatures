package net.msrandom.featuresandcreatures.common.items.antler_headdress;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.msrandom.featuresandcreatures.core.FnCItems;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.GeoArmorItem;

public class AntlerHeadDressItem extends GeoArmorItem implements IAnimatable {
    private AnimationFactory factory = new AnimationFactory(this);

    public int charge = 0;

    public AntlerHeadDressItem(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder) {
        super(materialIn, slot, builder);
    }

    @Override
    public void registerControllers(AnimationData data) {
    }


    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int p_77663_4_, boolean p_77663_5_) {
        PlayerEntity player = (PlayerEntity) entity;
        if (player.isCrouching() && player.getItemBySlot(EquipmentSlotType.HEAD).getItem() == FnCItems.ANTLER_HEADDRESS.get() && charge < 100) {
            charge++;
            int tick = charge % 25;
            if (tick == 0) {
                world.playSound(null, player.blockPosition(), SoundEvents.LEVER_CLICK, SoundCategory.AMBIENT, 1, 0.2F);
            }else if (tick % 8 ==0){
                world.playSound(null, player.blockPosition(), SoundEvents.LEVER_CLICK, SoundCategory.AMBIENT, 2, 3);
            }
            if (charge == 100) {
                world.playSound(null, player.blockPosition(), SoundEvents.NOTE_BLOCK_CHIME, SoundCategory.AMBIENT, 30, charge / 15F);
            }
        }
        if (!player.isCrouching() && player.getItemBySlot(EquipmentSlotType.HEAD).getItem() == FnCItems.ANTLER_HEADDRESS.get()) {
            int j = charge / 37;
            if (j > 0) {
                float f7 = player.yRot;
                float f = player.xRot;
                float f1 = -MathHelper.sin(f7 * ((float) Math.PI / 180F)) * MathHelper.cos(f * ((float) Math.PI / 180F));
                float f2 = -MathHelper.sin(f * ((float) Math.PI / 180F));
                float f3 = MathHelper.cos(f7 * ((float) Math.PI / 180F)) * MathHelper.cos(f * ((float) Math.PI / 180F));
                float f4 = MathHelper.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
                f1 = f1 * (j / f4);
                f3 = f3 * (j / f4);
                player.push(f1, 0.2, f3);
                player.startAutoSpinAttack(40);
                charge = 0;
            }
        }
        super.inventoryTick(stack, world, entity, p_77663_4_, p_77663_5_);
    }
}
