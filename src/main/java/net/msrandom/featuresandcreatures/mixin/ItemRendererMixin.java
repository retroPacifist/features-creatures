package net.msrandom.featuresandcreatures.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.msrandom.featuresandcreatures.util.BuiltInGuiTextureRenderer;
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
    private ItemModelMesher itemModelShaper;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(ItemStack stack, ItemCameraTransforms.TransformType transformType, boolean leftHand, MatrixStack matrixStack, IRenderTypeBuffer vertexBuilder, int combinedLight, int combinedOverlay, IBakedModel model, CallbackInfo ci) {
        if (!stack.isEmpty()) {
            ModelResourceLocation itemModel = BuiltInGuiTextureRenderer.getItemModel(stack);
            if (itemModel != null) {
                matrixStack.pushPose();
                boolean flag = transformType == ItemCameraTransforms.TransformType.GUI || transformType == ItemCameraTransforms.TransformType.GROUND || transformType == ItemCameraTransforms.TransformType.FIXED;
                if (flag) {
                    model = itemModelShaper.getModelManager().getModel(itemModel);
                }

                model = ForgeHooksClient.handleCameraTransforms(matrixStack, model, transformType, leftHand);
                matrixStack.translate(-0.5D, -0.5D, -0.5D);
                if (!model.isCustomRenderer() && flag) {
                    if (model.isLayered()) {
                        ForgeHooksClient.drawItemLayered((ItemRenderer) (Object) this, model, stack, matrixStack, vertexBuilder, combinedLight, combinedOverlay, true);
                    } else {
                        RenderType rendertype = RenderTypeLookup.getRenderType(stack, true);
                        IVertexBuilder ivertexbuilder;
                        ivertexbuilder = getFoilBufferDirect(vertexBuilder, rendertype, true, stack.hasFoil());

                        renderModelLists(model, stack, combinedLight, combinedOverlay, matrixStack, ivertexbuilder);
                    }
                } else {
                    stack.getItem().getItemStackTileEntityRenderer().renderByItem(stack, transformType, matrixStack, vertexBuilder, combinedLight, combinedOverlay);
                }

                matrixStack.popPose();
                ci.cancel();
            }
        }
    }

    @Shadow
    public static IVertexBuilder getFoilBufferDirect(IRenderTypeBuffer p_239391_0_, RenderType p_239391_1_, boolean p_239391_2_, boolean p_239391_3_) {
        return null;
    }

    @Shadow
    public abstract void renderModelLists(IBakedModel p_229114_1_, ItemStack p_229114_2_, int p_229114_3_, int p_229114_4_, MatrixStack p_229114_5_, IVertexBuilder p_229114_6_);
}
