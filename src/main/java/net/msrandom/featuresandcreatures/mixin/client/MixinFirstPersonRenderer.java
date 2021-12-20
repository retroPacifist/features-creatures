package net.msrandom.featuresandcreatures.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.msrandom.featuresandcreatures.core.FnCItems;
import net.msrandom.featuresandcreatures.item.DowsingRodItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FirstPersonRenderer.class)
public abstract class MixinFirstPersonRenderer {

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void renderDowsingRod(AbstractClientPlayerEntity player, float partialTicks, float pitch, Hand hand, float attackAnimation, ItemStack itemStack, float handHeight, MatrixStack poseStack, IRenderTypeBuffer bufferProvider, int packedLight, CallbackInfo ci) {
        if (player.getMainHandItem().getItem() == FnCItems.DOWSING_ROD || player.getOffhandItem().getItem() == FnCItems.DOWSING_ROD) {
            if (itemStack.getItem() == FnCItems.DOWSING_ROD) {
                DowsingRodItem.renderInHand(player, itemStack, poseStack, hand, bufferProvider, packedLight, pitch, handHeight, attackAnimation, (FirstPersonRenderer) (Object) this);
            }
            ci.cancel();
        }
    }
}
