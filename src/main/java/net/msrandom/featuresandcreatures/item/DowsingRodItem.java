package net.msrandom.featuresandcreatures.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.core.FnCSounds;
import net.msrandom.featuresandcreatures.entity.spawner.FnCSpawnerLevelContext;
import net.msrandom.featuresandcreatures.entity.spawner.JockeySpawner;
import net.msrandom.featuresandcreatures.mixin.access.FirstPersonRendererAccess;

public class DowsingRodItem extends Item {
    public DowsingRodItem(Properties settings) {
        super(settings);
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

    public static void renderInHand(MatrixStack stack, IRenderTypeBuffer renderTypeBuffer, int p_228400_3_, float p_228400_4_, float p_228400_5_, float p_228400_6_, FirstPersonRenderer firstPersonRenderer) {
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuilder();
        RenderSystem.enableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        stack.pushPose();
        Matrix4f matrix4f1 = stack.last().pose();
        float f12 = 30.0F;
        Minecraft.getInstance().textureManager.bind(new ResourceLocation(FeaturesAndCreatures.MOD_ID, "textures/item/dowsing_rod.png"));
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.vertex(matrix4f1, -f12, 100.0F, -f12).uv(0.0F, 0.0F).endVertex();
        bufferbuilder.vertex(matrix4f1, f12, 100.0F, -f12).uv(1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(matrix4f1, f12, 100.0F, f12).uv(1.0F, 1.0F).endVertex();
        bufferbuilder.vertex(matrix4f1, -f12, 100.0F, f12).uv(0.0F, 1.0F).endVertex();
        bufferbuilder.end();
        WorldVertexBufferUploader.end(bufferbuilder);
        RenderSystem.disableTexture();
        stack.popPose();

        float f = MathHelper.sqrt(p_228400_6_);
        float f1 = -0.2F * MathHelper.sin(p_228400_6_ * (float)Math.PI);
        float f2 = -0.4F * MathHelper.sin(f * (float)Math.PI);
        stack.translate(0.0D, (double)(-f1 / 2.0F), (double)f2);
        float f3 = ((FirstPersonRendererAccess) firstPersonRenderer).invokeCalculateMapTilt(p_228400_4_);
        stack.translate(0.0D, (double)(0.04F + p_228400_5_ * -1.2F + f3 * -0.5F), (double)-0.72F);
        stack.mulPose(Vector3f.XP.rotationDegrees(f3 * -85.0F));
        if (!((FirstPersonRendererAccess) firstPersonRenderer).getMinecraft().player.isInvisible()) {
            stack.pushPose();
            stack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
            ((FirstPersonRendererAccess) firstPersonRenderer).invokeRenderMapHand(stack, renderTypeBuffer, p_228400_3_, HandSide.RIGHT);
            ((FirstPersonRendererAccess) firstPersonRenderer).invokeRenderMapHand(stack, renderTypeBuffer, p_228400_3_, HandSide.LEFT);
            stack.popPose();
        }
        float f4 = MathHelper.sin(f * (float)Math.PI);
        stack.mulPose(Vector3f.XP.rotationDegrees(f4 * 20.0F));
        stack.scale(2.0F, 2.0F, 2.0F);
    }
}
