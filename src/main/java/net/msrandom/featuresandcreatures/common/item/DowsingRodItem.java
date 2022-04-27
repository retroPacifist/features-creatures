package net.msrandom.featuresandcreatures.common.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.msrandom.featuresandcreatures.core.FnCSounds;
import net.msrandom.featuresandcreatures.common.entity.spawner.FnCSpawnerLevelContext;
import net.msrandom.featuresandcreatures.common.entity.spawner.JockeySpawner;
import net.msrandom.featuresandcreatures.mixin.access.FirstPersonRendererAccess;

public class DowsingRodItem extends Item {
    public DowsingRodItem(Properties settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (level.isClientSide && selected && entity instanceof Player && entity.tickCount % 20 == 0) {
            Player player = (Player) entity;
            if (!player.getOffhandItem().isEmpty()) return;
            JockeySpawner.Context context = ((FnCSpawnerLevelContext) level.getLevelData()).jockeyContext();
            if (context != null) {
                BlockPos pos = context.getPos();
                if (pos != null) {
                    float pitch = (float) Math.toRadians(entity.getXRot());
                    float yaw = (float) Math.toRadians(-entity.getYRot());
                    float horizontalFactor = Mth.cos(pitch);

                    Vec3 playerForward = new Vec3(
                            Mth.sin(yaw) * horizontalFactor,
                            -Mth.sin(pitch),
                            Mth.cos(yaw) * horizontalFactor
                    );

                    Vec3 playerToJockey = new Vec3(
                            pos.getX() + 0.5 - entity.getX(),
                            playerForward.y,
                            pos.getZ() + 0.5 - entity.getZ()
                    );

                    if (playerForward.dot(playerToJockey.normalize()) > 0.99) {
                        // Pointing at jockey
                        player.playNotifySound(FnCSounds.DOWSING_ROD_LOCATES, SoundSource.PLAYERS, 1f, 1f);
                    }
                }
            }
        }
    }

    public static void renderInHand(AbstractClientPlayer player, ItemStack stack, PoseStack poseStack, InteractionHand hand, MultiBufferSource bufferProvider, int packedLight, float pitch, float handHeight, float attackAnimation, ItemInHandRenderer firstPersonRenderer) {
        float f = Mth.sqrt(attackAnimation);
        float f1 = Mth.sin(attackAnimation * (float) Math.PI) * -0.2F;
        float f2 = Mth.sin(f * (float) Math.PI) * -0.4F;
        poseStack.translate(0.0D, -f1 / 2.0F, f2);
        float f3 = ((FirstPersonRendererAccess) firstPersonRenderer).invokeCalculateMapTilt(pitch);
        poseStack.translate(0.0D, 0.04 + handHeight * -1.2 + f3 * -0.5, -0.72);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(f3 * -85.0F));
        if (!stack.isEmpty()) {
            poseStack.pushPose();
            boolean isMainHand = hand == InteractionHand.MAIN_HAND;
            boolean isRightHand = player.getMainArm() == HumanoidArm.RIGHT;
            ItemTransforms.TransformType transformType;
            if (isMainHand == isRightHand) {
                transformType = ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND;
            } else {
                transformType = ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND;
            }
            Minecraft.getInstance().getItemRenderer().renderStatic(player, stack, transformType, isRightHand, poseStack, bufferProvider, player.level, packedLight, (int)pitch, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
        if (!player.isInvisible()) {
            poseStack.pushPose();
            poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
            ((FirstPersonRendererAccess) firstPersonRenderer).invokeRenderMapHand(poseStack, bufferProvider, packedLight, HumanoidArm.RIGHT);
            ((FirstPersonRendererAccess) firstPersonRenderer).invokeRenderMapHand(poseStack, bufferProvider, packedLight, HumanoidArm.LEFT);
            poseStack.popPose();
        }
        float f4 = Mth.sin(f * (float) Math.PI);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(f4 * 20.0F));
        poseStack.scale(2.0F, 2.0F, 2.0F);
    }
}
