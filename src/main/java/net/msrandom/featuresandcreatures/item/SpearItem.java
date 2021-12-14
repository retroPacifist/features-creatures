package net.msrandom.featuresandcreatures.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.featuresandcreatures.core.FnCSounds;
import net.msrandom.featuresandcreatures.entity.Spear;

import java.util.Map;

public class SpearItem extends Item implements IVanishable {
    private final Multimap<Attribute, AttributeModifier> spearAttributes;

    public SpearItem(Properties properties) {
        super(properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        this.spearAttributes = builder.build();
    }

    public boolean canAttackBlock(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
    }

    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.SPEAR;
    }

    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(book);
        return enchantments.containsKey(Enchantments.FIRE_ASPECT) || enchantments.containsKey(Enchantments.MENDING)
                || enchantments.containsKey(Enchantments.UNBREAKING);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.UNBREAKING || enchantment == Enchantments.FIRE_ASPECT;
    }

    public void releaseUsing(ItemStack stack, World world, LivingEntity entity, int timeLeft) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity playerentity = (PlayerEntity) entity;
            int i = this.getUseDuration(stack) - timeLeft;
            if (i >= 10) {
                if (!world.isClientSide) {
                    stack.hurtAndBreak(1, playerentity, (p_220047_1_) -> {
                        p_220047_1_.broadcastBreakEvent(entity.getUsedItemHand());
                    });
                    Spear spear = new Spear(world, playerentity, stack);
                    spear.shootFromRotation(playerentity, playerentity.xRot, playerentity.yRot, 0.0F, 2.5F + (float) 2 * 0.5F, 1.0F);
                    if (playerentity.abilities.instabuild) {
                        spear.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                    }
                    world.addFreshEntity(spear);
                    world.playSound(null, spear, FnCSounds.SPEAR_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    if (!playerentity.abilities.instabuild) {
                        playerentity.inventory.removeItem(stack);
                    }
                }
            }
            playerentity.awardStat(Stats.ITEM_USED.get(this));
        }
    }

    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.getDamageValue() >= itemstack.getMaxDamage() - 1) {
            return ActionResult.fail(itemstack);
        } else {
            player.startUsingItem(hand);
            return ActionResult.consume(itemstack);
        }
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity attacker, LivingEntity entity) {
        stack.hurtAndBreak(1, entity, (player) -> {
            player.broadcastBreakEvent(EquipmentSlotType.MAINHAND);
        });
        return true;
    }

    public boolean mineBlock(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity entity) {
        if ((double) state.getDestroySpeed(world, pos) != 0.0D) {
            stack.hurtAndBreak(2, entity, (p_220046_0_) -> {
                p_220046_0_.broadcastBreakEvent(EquipmentSlotType.MAINHAND);
            });
        }

        return true;
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlotType slot) {
        return slot == EquipmentSlotType.MAINHAND ? this.spearAttributes : super.getDefaultAttributeModifiers(slot);
    }

    public int getEnchantmentValue() {
        return 40;
    }
}