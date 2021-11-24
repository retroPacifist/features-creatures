package net.msrandom.featuresandcreatures.common.items.antler_headdress;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.msrandom.featuresandcreatures.core.FnCItems;
import net.msrandom.featuresandcreatures.core.FnCKeybinds;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.GeoArmorItem;

import java.util.List;

public class AntlerHeadDressItem extends GeoArmorItem implements IAnimatable {
    public boolean isCharging = false;
    public int charge = 0;
    public boolean isDamaging = false;
    public int damageTimer = 40;
    private final AnimationFactory factory = new AnimationFactory(this);

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
        isCharging = FnCKeybinds.CHARGE_ANTLER.isDown();
        float f7 = player.yRot;
        float f = player.xRot;
        float f1 = -MathHelper.sin(f7 * ((float) Math.PI / 180F)) * MathHelper.cos(f * ((float) Math.PI / 180F));
        float f2 = -MathHelper.sin(f * ((float) Math.PI / 180F));
        float f3 = MathHelper.cos(f7 * ((float) Math.PI / 180F)) * MathHelper.cos(f * ((float) Math.PI / 180F));
        float f4 = MathHelper.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
        if (world.isClientSide) {
            if (isCharging && player.getItemBySlot(EquipmentSlotType.HEAD).getItem() == FnCItems.ANTLER_HEADDRESS.get() && charge <= 100) {
                charge++;
            }
            if (charge == 2 || charge == 25 || charge == 50) {
                world.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.LEVER_CLICK, SoundCategory.AMBIENT, 1, charge / 25F, false);
            } else if (charge == 75) {
                world.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_CHIME, SoundCategory.AMBIENT, 2, 1.5F, false);
            }
            if (charge == 100) {
                world.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_CHIME, SoundCategory.AMBIENT, 30, charge / 15F, false);
            }
            if (!isCharging) {
                int j = charge / 37;
                float i = f1 * (j / f4);
                float k = f3 * (j / f4);
                if (charge > 37) {
                    player.push(i, 0.1, k);
                    charge = 0;
                    world.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_RIPTIDE_1, SoundCategory.AMBIENT, 30, 1, false);
                    isDamaging = true;
                } else if (charge >= 1) {
                    player.push(f1 * (0.5 / f4), 0.1, f3 * (0.5 / f4));
                    charge = 0;
                    world.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_RIPTIDE_1, SoundCategory.AMBIENT, 30, 1, false);
                    isDamaging = true;
                }else{
                    charge = 0;
                }
            }
        }
        if (isDamaging) {
            AxisAlignedBB aabb = new AxisAlignedBB(player.blockPosition());
            List<LivingEntity> list = world.getEntitiesOfClass(LivingEntity.class, aabb);
            for (LivingEntity entity2 : list) {
                if (entity2 != entity) {
                    entity2.hurt(DamageSource.playerAttack(player), getDamageAmount());
                    if(charge > 37) {
                        entity2.knockback(0.5F, -f1 * (2 / f4), -f3 * (2 / f4));
                    }
                }
            }
            damageTimer--;
        }
        if (damageTimer <= 0) {
            isDamaging = false;
            damageTimer = 40;
        }
        super.inventoryTick(stack, world, entity, p_77663_4_, p_77663_5_);
    }
    public float getDamageAmount(){
        if (charge <=25){
            return 0.5F;
        } else if (charge <= 50){
            return 1F;
        } else if (charge == 100){
            return  2.5F;
        }else {
            return 1.5F;
        }
    }
}
