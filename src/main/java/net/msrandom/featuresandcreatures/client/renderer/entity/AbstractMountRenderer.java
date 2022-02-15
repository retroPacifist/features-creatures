package net.msrandom.featuresandcreatures.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.LightType;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.msrandom.featuresandcreatures.entity.mount.AbstractMountEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.InvocationTargetException;

@ParametersAreNonnullByDefault
public abstract class AbstractMountRenderer<T extends AbstractMountEntity> extends GeoEntityRenderer<T> {
    private final float defaultShadowRadius;
    protected static EntityRendererManager entityRenderDispatcher;

    protected AbstractMountRenderer(EntityRendererManager renderManager, AnimatedGeoModel<T> modelProvider, float defaultShadowRadius) {
        super(renderManager, modelProvider);
        this.defaultShadowRadius = defaultShadowRadius;
        entityRenderDispatcher = renderManager;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn) {
        if (entity.isBaby()) {
            float scale = 0.5F;
            stack.scale(scale, scale, scale);
            shadowRadius = defaultShadowRadius * 0.75F;
        } else {
            shadowRadius = defaultShadowRadius;
        }

        if(entity.getLeashHolder() != null)
            renderLeash(entity, partialTicks, stack, bufferIn, entity.getLeashHolder());

        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }

    //TODO might be a good idea to migrate this to its own util class if more static functions show up
    public static <E extends Entity> void renderLeash(LivingEntity entity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, E leashHolder) {
        matrixStack.pushPose();
        Vector3d vector3d = leashHolder.getRopeHoldPosition(partialTicks);
        double d0 = (double)(MathHelper.lerp(partialTicks, entity.yBodyRot, entity.yBodyRotO) * ((float)Math.PI / 180F)) + (Math.PI / 2D);
        Vector3d vector3d1 = entity.getLeashOffset();
        double d1 = Math.cos(d0) * vector3d1.z + Math.sin(d0) * vector3d1.x;
        double d2 = Math.sin(d0) * vector3d1.z - Math.cos(d0) * vector3d1.x;
        double d3 = MathHelper.lerp(partialTicks, entity.xo, entity.getX()) + d1;
        double d4 = MathHelper.lerp(partialTicks, entity.yo, entity.getY()) + vector3d1.y;
        double d5 = MathHelper.lerp(partialTicks, entity.zo, entity.getZ()) + d2;
        matrixStack.translate(d1, vector3d1.y, d2);
        float f = (float)(vector3d.x - d3);
        float f1 = (float)(vector3d.y - d4);
        float f2 = (float)(vector3d.z - d5);
        IVertexBuilder ivertexbuilder = buffer.getBuffer(RenderType.leash());
        Matrix4f matrix4f = matrixStack.last().pose();
        float f4 = MathHelper.fastInvSqrt(f * f + f2 * f2) * 0.025F / 2.0F;
        float f5 = f2 * f4;
        float f6 = f * f4;
        BlockPos blockpos = new BlockPos(entity.getEyePosition(partialTicks));
        BlockPos blockpos1 = new BlockPos(leashHolder.getEyePosition(partialTicks));

        int i;
        try
        {
            i = (int)ObfuscationReflectionHelper.findMethod(EntityRenderer.class, "getBlockLightLevel", entity.getClass(), BlockPos.class).invoke(entityRenderDispatcher.getRenderer(entity), entity, blockpos); //there's a chance that these might not work upon building, yell at @Cibernet on discord if that's the case
        } catch (Throwable e) { i = (entity.isOnFire() ? 15 : entity.level.getBrightness(LightType.BLOCK, blockpos));}
        int j;
        try
        {
            j = (int) ObfuscationReflectionHelper.findMethod(EntityRenderer.class, "getBlockLightLevel", leashHolder.getClass(), BlockPos.class).invoke(entityRenderDispatcher.getRenderer(leashHolder), leashHolder, blockpos1);
        } catch (Throwable e) { j = (leashHolder.isOnFire() ? 15 : leashHolder.level.getBrightness(LightType.BLOCK, blockpos1));}

        int k = entity.level.getBrightness(LightType.SKY, blockpos);
        int l = entity.level.getBrightness(LightType.SKY, blockpos1);
        renderSide(ivertexbuilder, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.025F, f5, f6);
        renderSide(ivertexbuilder, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.0F, f5, f6);
        matrixStack.popPose();
    }

    public static void renderSide(IVertexBuilder p_229119_0_, Matrix4f p_229119_1_, float p_229119_2_, float p_229119_3_, float p_229119_4_, int p_229119_5_, int p_229119_6_, int p_229119_7_, int p_229119_8_, float p_229119_9_, float p_229119_10_, float p_229119_11_, float p_229119_12_) {
        int i = 24;

        for(int j = 0; j < 24; ++j) {
            float f = (float)j / 23.0F;
            int k = (int)MathHelper.lerp(f, (float)p_229119_5_, (float)p_229119_6_);
            int l = (int)MathHelper.lerp(f, (float)p_229119_7_, (float)p_229119_8_);
            int i1 = LightTexture.pack(k, l);
            addVertexPair(p_229119_0_, p_229119_1_, i1, p_229119_2_, p_229119_3_, p_229119_4_, p_229119_9_, p_229119_10_, 24, j, false, p_229119_11_, p_229119_12_);
            addVertexPair(p_229119_0_, p_229119_1_, i1, p_229119_2_, p_229119_3_, p_229119_4_, p_229119_9_, p_229119_10_, 24, j + 1, true, p_229119_11_, p_229119_12_);
        }

    }

    public static void addVertexPair(IVertexBuilder p_229120_0_, Matrix4f p_229120_1_, int p_229120_2_, float p_229120_3_, float p_229120_4_, float p_229120_5_, float p_229120_6_, float p_229120_7_, int p_229120_8_, int p_229120_9_, boolean p_229120_10_, float p_229120_11_, float p_229120_12_) {
        float f = 0.5F;
        float f1 = 0.4F;
        float f2 = 0.3F;
        if (p_229120_9_ % 2 == 0) {
            f *= 0.7F;
            f1 *= 0.7F;
            f2 *= 0.7F;
        }

        float f3 = (float)p_229120_9_ / (float)p_229120_8_;
        float f4 = p_229120_3_ * f3;
        float f5 = p_229120_4_ > 0.0F ? p_229120_4_ * f3 * f3 : p_229120_4_ - p_229120_4_ * (1.0F - f3) * (1.0F - f3);
        float f6 = p_229120_5_ * f3;
        if (!p_229120_10_) {
            p_229120_0_.vertex(p_229120_1_, f4 + p_229120_11_, f5 + p_229120_6_ - p_229120_7_, f6 - p_229120_12_).color(f, f1, f2, 1.0F).uv2(p_229120_2_).endVertex();
        }

        p_229120_0_.vertex(p_229120_1_, f4 - p_229120_11_, f5 + p_229120_7_, f6 + p_229120_12_).color(f, f1, f2, 1.0F).uv2(p_229120_2_).endVertex();
        if (p_229120_10_) {
            p_229120_0_.vertex(p_229120_1_, f4 + p_229120_11_, f5 + p_229120_6_ - p_229120_7_, f6 - p_229120_12_).color(f, f1, f2, 1.0F).uv2(p_229120_2_).endVertex();
        }

    }
}
