package net.msrandom.featuresandcreatures.mixin;

import net.minecraft.client.renderer.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {}
//    @Shadow
//    @Final
//    private ItemModelMesher itemModelShaper;
//
//    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
//    private void render(ItemStack stack, ItemCameraTransforms.TransformType transformType, boolean leftHand, MatrixStack matrixStack, IRenderTypeBuffer vertexBuilder, int combinedLight, int combinedOverlay, IBakedModel model, CallbackInfo ci) {
//        if (!stack.isEmpty()) {
//            ModelResourceLocation itemModel = BuiltInGuiTextureRenderer.getItemModel(stack);
//            if (itemModel != null) {
//                matrixStack.pushPose();
//                matrixStack.scale(0.325F, 0.325F, 0.325F);
//                boolean flag = transformType == ItemCameraTransforms.TransformType.GUI || transformType == ItemCameraTransforms.TransformType.GROUND || transformType == ItemCameraTransforms.TransformType.FIXED;
//                if (flag) {
//                    model = itemModelShaper.getModelManager().getModel(itemModel);
//                }
//
//                model = ForgeHooksClient.handleCameraTransforms(matrixStack, model, transformType, leftHand);
//                matrixStack.translate(-0.5D, -0.5D, -0.5D);
//                if (!model.isCustomRenderer() && flag) {
//                    if (model.isLayered()) {
//                        ForgeHooksClient.drawItemLayered((ItemRenderer) (Object) this, model, stack, matrixStack, vertexBuilder, combinedLight, combinedOverlay, true);
//                    } else {
//                        RenderType rendertype = RenderTypeLookup.getRenderType(stack, true);
//                        IVertexBuilder ivertexbuilder;
//                        ivertexbuilder = getFoilBufferDirect(vertexBuilder, rendertype, true, stack.hasFoil());
//
//                        renderModelLists(model, stack, combinedLight, combinedOverlay, matrixStack, ivertexbuilder);
//                    }
//                } else {
//                    stack.getItem().getItemStackTileEntityRenderer().renderByItem(stack, transformType, matrixStack, vertexBuilder, combinedLight, combinedOverlay);
//                }
//
//                matrixStack.popPose();
//                ci.cancel();
//            }
//        }
//    }
//
//    @Shadow
//    public static IVertexBuilder getFoilBufferDirect(IRenderTypeBuffer p_239391_0_, RenderType p_239391_1_, boolean p_239391_2_, boolean p_239391_3_) {
//        return null;
//    }
//
//    @Shadow
//    public abstract void renderModelLists(IBakedModel p_229114_1_, ItemStack p_229114_2_, int p_229114_3_, int p_229114_4_, MatrixStack p_229114_5_, IVertexBuilder p_229114_6_);
//}
