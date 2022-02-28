package net.msrandom.featuresandcreatures.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.msrandom.featuresandcreatures.entity.mount.AbstractMountEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class AbstractMountRenderer<T extends AbstractMountEntity> extends GeoEntityRenderer<T> {
    private final float defaultShadowRadius;
    protected static EntityRenderDispatcher entityRenderDispatcher;

    protected AbstractMountRenderer(EntityRendererProvider.Context context, AnimatedGeoModel<T> modelProvider, float defaultShadowRadius) {
        super(context, modelProvider);
        this.defaultShadowRadius = defaultShadowRadius;
        entityRenderDispatcher = context.getEntityRenderDispatcher();
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource consumer, int packedLightIn) {
        if (entity.isBaby()) {
            float scale = 0.5F;
            stack.scale(scale, scale, scale);
            shadowRadius = defaultShadowRadius * 0.75F;
        } else {
            shadowRadius = defaultShadowRadius;
        }

        if(entity.getLeashHolder() != null)
            renderLeash(entity, partialTicks, stack, consumer, entity.getLeashHolder());

        super.render(entity, entityYaw, partialTicks, stack, consumer, packedLightIn);
    }

    //TODO might be a good idea to migrate this to its own util class if more static functions show up
    public static <E extends Entity> void renderLeash(LivingEntity entity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, E leashHolder) {
        matrixStack.pushPose();
        Vec3 vector3d = leashHolder.getRopeHoldPosition(partialTicks);
        double d0 = (double)(Mth.lerp(partialTicks, entity.yBodyRot, entity.yBodyRotO) * ((float)Math.PI / 180F)) + (Math.PI / 2D);
        Vec3 vector3d1 = entity.getLeashOffset();
        double d1 = Math.cos(d0) * vector3d1.z + Math.sin(d0) * vector3d1.x;
        double d2 = Math.sin(d0) * vector3d1.z - Math.cos(d0) * vector3d1.x;
        double d3 = Mth.lerp(partialTicks, entity.xo, entity.getX()) + d1;
        double d4 = Mth.lerp(partialTicks, entity.yo, entity.getY()) + vector3d1.y;
        double d5 = Mth.lerp(partialTicks, entity.zo, entity.getZ()) + d2;
        matrixStack.translate(d1, vector3d1.y, d2);
        float f = (float)(vector3d.x - d3);
        float f1 = (float)(vector3d.y - d4);
        float f2 = (float)(vector3d.z - d5);
        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.leash());
        Matrix4f matrix4f = matrixStack.last().pose();
        float f4 = Mth.fastInvSqrt(f * f + f2 * f2) * 0.025F / 2.0F;
        float f5 = f2 * f4;
        float f6 = f * f4;
        BlockPos blockpos = new BlockPos(entity.getEyePosition(partialTicks));
        BlockPos blockpos1 = new BlockPos(leashHolder.getEyePosition(partialTicks));

        int i;
        try
        {
            i = (int) ObfuscationReflectionHelper.findMethod(EntityRenderer.class, "getBlockLightLevel", entity.getClass(), BlockPos.class).invoke(entityRenderDispatcher.getRenderer(entity), entity, blockpos); //there's a chance that these might not work upon building, yell at @Cibernet on discord if that's the case
        } catch (Throwable e) { i = (entity.isOnFire() ? 15 : entity.level.getBrightness(LightLayer.BLOCK, blockpos));}
        int j;
        try
        {
            j = (int) ObfuscationReflectionHelper.findMethod(EntityRenderer.class, "getBlockLightLevel", leashHolder.getClass(), BlockPos.class).invoke(entityRenderDispatcher.getRenderer(leashHolder), leashHolder, blockpos1);
        } catch (Throwable e) { j = (leashHolder.isOnFire() ? 15 : leashHolder.level.getBrightness(LightLayer.BLOCK, blockpos1));}

        int k = entity.level.getBrightness(LightLayer.SKY, blockpos);
        int l = entity.level.getBrightness(LightLayer.SKY, blockpos1);for(int i1 = 0; i1 <= 24; ++i1) {
            addVertexPair(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.025F, f5, f6, i1, false);
        }

        for(int j1 = 24; j1 >= 0; --j1) {
            addVertexPair(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.0F, f5, f6, j1, true);
        }
        matrixStack.popPose();
    }

    private static void addVertexPair(VertexConsumer p_174308_, Matrix4f p_174309_, float p_174310_, float p_174311_, float p_174312_, int p_174313_, int p_174314_, int p_174315_, int p_174316_, float p_174317_, float p_174318_, float p_174319_, float p_174320_, int p_174321_, boolean p_174322_) {
        float f = (float)p_174321_ / 24.0F;
        int i = (int)Mth.lerp(f, (float)p_174313_, (float)p_174314_);
        int j = (int)Mth.lerp(f, (float)p_174315_, (float)p_174316_);
        int k = LightTexture.pack(i, j);
        float f1 = p_174321_ % 2 == (p_174322_ ? 1 : 0) ? 0.7F : 1.0F;
        float f2 = 0.5F * f1;
        float f3 = 0.4F * f1;
        float f4 = 0.3F * f1;
        float f5 = p_174310_ * f;
        float f6 = p_174311_ > 0.0F ? p_174311_ * f * f : p_174311_ - p_174311_ * (1.0F - f) * (1.0F - f);
        float f7 = p_174312_ * f;
        p_174308_.vertex(p_174309_, f5 - p_174319_, f6 + p_174318_, f7 + p_174320_).color(f2, f3, f4, 1.0F).uv2(k).endVertex();
        p_174308_.vertex(p_174309_, f5 + p_174319_, f6 + p_174317_ - p_174318_, f7 - p_174320_).color(f2, f3, f4, 1.0F).uv2(k).endVertex();
    }
}
