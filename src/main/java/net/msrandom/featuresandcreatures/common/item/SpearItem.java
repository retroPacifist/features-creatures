package net.msrandom.featuresandcreatures.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.msrandom.featuresandcreatures.core.FnCSounds;
import net.msrandom.featuresandcreatures.common.entity.Spear;

import java.util.Map;

public class SpearItem extends Item implements Vanishable {
    private final Multimap<Attribute, AttributeModifier> spearAttributes = ImmutableMultimap.of();

    public SpearItem(Properties properties) {
        super(properties);
    }

    public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
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

    public void releaseUsing(ItemStack stack, Level world, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player) {
            Player playerentity = (Player) entity;
            int i = this.getUseDuration(stack) - timeLeft;
            if (i >= 10) {
                if (!world.isClientSide) {
                    stack.hurtAndBreak(1, playerentity, (p_220047_1_) -> {
                        p_220047_1_.broadcastBreakEvent(entity.getUsedItemHand());
                    });
                    Spear spear = new Spear(world, playerentity, stack);
                    spear.shootFromRotation(playerentity, playerentity.getXRot(), playerentity.getYRot(), 0.0F, 2.5F + (float) 2 * 0.5F, 1.0F);
                    if (playerentity.getAbilities().instabuild) {
                        spear.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    }
                    world.addFreshEntity(spear);
                    world.playSound(null, spear, FnCSounds.SPEAR_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
                    if (!playerentity.getAbilities().instabuild) {
                        playerentity.getInventory().removeItem(stack);
                    }
                }
            }
            playerentity.awardStat(Stats.ITEM_USED.get(this));
        }
    }

    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.getDamageValue() >= itemstack.getMaxDamage() - 1) {
            return InteractionResultHolder.fail(itemstack);
        } else {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(itemstack);
        }
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity attacker, LivingEntity entity) {
        stack.hurtAndBreak(1, entity, (player) -> {
            player.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        return true;
    }

    public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entity) {
        if ((double) state.getDestroySpeed(world, pos) != 0.0D) {
            stack.hurtAndBreak(2, entity, (p_220046_0_) -> {
                p_220046_0_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }

        return true;
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.spearAttributes : super.getDefaultAttributeModifiers(slot);
    }

    public int getEnchantmentValue() {
        return 40;
    }
}