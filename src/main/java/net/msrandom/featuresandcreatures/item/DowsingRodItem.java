package net.msrandom.featuresandcreatures.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLCommonLaunchHandler;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.core.FnCSounds;
import net.msrandom.featuresandcreatures.entity.spawner.FnCSpawnerLevelContext;
import net.msrandom.featuresandcreatures.entity.spawner.JockeySpawner;
import net.msrandom.featuresandcreatures.mixin.access.FirstPersonRendererAccess;
import org.lwjgl.opengl.GL11;

public class DowsingRodItem extends Item {
    public DowsingRodItem(Properties settings)
    {
        super(settings);

        if(FMLEnvironment.dist == Dist.CLIENT)
            ItemModelsProperties.register(this, new ResourceLocation(FeaturesAndCreatures.MOD_ID, "dowsing"), ((stack, level, entity) ->
            {
                if(level == null || level.getLevelData() == null)
                    return 0;

                JockeySpawner.Context context = ((FnCSpawnerLevelContext) level.getLevelData()).jockeyContext();
                if(context != null)
                {
                    BlockPos pos = context.getPos();
                    if (pos != null)
                    {
                        float pitch = (float) Math.toRadians(entity.xRot);
                        float yaw = (float) Math.toRadians(-entity.yRot);
                        float horizontalFactor = MathHelper.cos(pitch);

                        Vector3d playerForward = new Vector3d(
                                MathHelper.sin(yaw) * horizontalFactor,
                                /*-MathHelper.sin(pitch)*/0, //ignore y
                                MathHelper.cos(yaw) * horizontalFactor
                        );

                        Vector3d playerToJockey = new Vector3d(
                                pos.getX() + 0.5 - entity.getX(),
                                /*playerForward.y*/0, //ignore y
                                pos.getZ() + 0.5 - entity.getZ()
                        );

                        return 180f - (float) Math.toDegrees(Math.acos(playerForward.dot(playerToJockey) / (playerForward.length() * playerToJockey.length())));
                    }
                }
                return 0;
            }));
    }

    @Override
    public void inventoryTick(ItemStack stack, World level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (level.isClientSide && selected && entity instanceof PlayerEntity && entity.tickCount % 20 == 0) {
            PlayerEntity player = (PlayerEntity) entity;
            if (!player.getOffhandItem().isEmpty()) return;
            JockeySpawner.Context context = ((FnCSpawnerLevelContext) level.getLevelData()).jockeyContext();
            if (context != null) {
                BlockPos pos = context.getPos();
                if (pos != null) {
                    float pitch = (float) Math.toRadians(entity.xRot);
                    float yaw = (float) Math.toRadians(-entity.yRot);
                    float horizontalFactor = MathHelper.cos(pitch);

                    Vector3d playerForward = new Vector3d(
                            MathHelper.sin(yaw) * horizontalFactor,
                            -MathHelper.sin(pitch),
                            MathHelper.cos(yaw) * horizontalFactor
                    );

                    Vector3d playerToJockey = new Vector3d(
                            pos.getX() + 0.5 - entity.getX(),
                            playerForward.y,
                            pos.getZ() + 0.5 - entity.getZ()
                    );

                    if (playerForward.dot(playerToJockey.normalize()) > 0.99) {
                        // Pointing at jockey
                        player.playNotifySound(FnCSounds.DOWSING_ROD_LOCATES, SoundCategory.PLAYERS, 1f, 1f);
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderInHand(AbstractClientPlayerEntity player, ItemStack stack, MatrixStack poseStack, Hand hand, IRenderTypeBuffer bufferProvider, int packedLight, float pitch, float handHeight, float attackAnimation, FirstPersonRenderer firstPersonRenderer) {
        float f = MathHelper.sqrt(attackAnimation);
        float f1 = MathHelper.sin(attackAnimation * (float) Math.PI) * -0.2F;
        float f2 = MathHelper.sin(f * (float) Math.PI) * -0.4F;
        poseStack.translate(0.0D, -f1 / 2.0F, f2);
        float f3 = ((FirstPersonRendererAccess) firstPersonRenderer).invokeCalculateMapTilt(pitch);
        poseStack.translate(0.0D, 0.04 + handHeight * -1.2 + f3 * -0.5, -0.72);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(f3 * -85.0F));
        if (!stack.isEmpty()) {
            poseStack.pushPose();
            boolean isMainHand = hand == Hand.MAIN_HAND;
            boolean isRightHand = player.getMainArm() == HandSide.RIGHT;
            ItemCameraTransforms.TransformType transformType;
            if (isMainHand == isRightHand) {
                transformType = ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND;
            } else {
                transformType = ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND;
            }
            Minecraft.getInstance().getItemRenderer().renderStatic(player, stack, transformType, isRightHand, poseStack, bufferProvider, player.level, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
        if (!player.isInvisible()) {
            poseStack.pushPose();
            poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
            ((FirstPersonRendererAccess) firstPersonRenderer).invokeRenderMapHand(poseStack, bufferProvider, packedLight, HandSide.RIGHT);
            ((FirstPersonRendererAccess) firstPersonRenderer).invokeRenderMapHand(poseStack, bufferProvider, packedLight, HandSide.LEFT);
            poseStack.popPose();
        }
        float f4 = MathHelper.sin(f * (float) Math.PI);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(f4 * 20.0F));
        poseStack.scale(2.0F, 2.0F, 2.0F);
    }
}
