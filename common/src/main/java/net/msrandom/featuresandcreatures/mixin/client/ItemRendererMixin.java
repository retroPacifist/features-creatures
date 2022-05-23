package net.msrandom.featuresandcreatures.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.RenderProperties;
import net.msrandom.featuresandcreatures.client.BuiltInGuiTextureRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow
    @Final
    private ItemModelShaper itemModelShaper;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(ItemStack stack, ItemTransforms.TransformType transformType, boolean leftHand, PoseStack matrixStack, MultiBufferSource vertexBuilder, int combinedLight, int combinedOverlay, BakedModel model, CallbackInfo ci) {
        if (!stack.isEmpty()) {
            ModelResourceLocation itemModel = BuiltInGuiTextureRenderer.getItemModel(stack);
            if (itemModel != null) {
                matrixStack.pushPose();
                boolean gui = transformType == ItemTransforms.TransformType.GUI || transformType == ItemTransforms.TransformType.GROUND || transformType == ItemTransforms.TransformType.FIXED;
                if (gui) {
                    model = itemModelShaper.getModelManager().getModel(itemModel);
                }
                model = ForgeHooksClient.handleCameraTransforms(matrixStack, model, transformType, leftHand);
                matrixStack.translate(-0.5D, -0.5D, -0.5D);
                if (!model.isCustomRenderer() && gui) {
                    if (model.isLayered()) {
                        ForgeHooksClient.drawItemLayered((ItemRenderer) (Object) this, model, stack, matrixStack, vertexBuilder, combinedLight, combinedOverlay, true);
                    } else {
                        RenderType rendertype = ItemBlockRenderTypes.getRenderType(stack, true);
                        VertexConsumer ivertexbuilder;
                        ivertexbuilder = getFoilBufferDirect(vertexBuilder, rendertype, true, stack.hasFoil());

                        renderModelLists(model, stack, combinedLight, combinedOverlay, matrixStack, ivertexbuilder);
                    }
                } else {
                    RenderProperties.get(stack).getItemStackRenderer().renderByItem(stack, transformType, matrixStack, vertexBuilder, combinedLight, combinedOverlay);
                }

                matrixStack.popPose();
                ci.cancel();
            }
        }
    }

    @Shadow
    public static VertexConsumer getFoilBufferDirect(MultiBufferSource p_239391_0_, RenderType p_239391_1_, boolean p_239391_2_, boolean p_239391_3_) {
        return null;
    }

    @Shadow
    public abstract void renderModelLists(BakedModel p_229114_1_, ItemStack p_229114_2_, int p_229114_3_, int p_229114_4_, PoseStack p_229114_5_, VertexConsumer p_229114_6_);
}
