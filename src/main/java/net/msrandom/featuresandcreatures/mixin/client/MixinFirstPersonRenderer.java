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

    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", ordinal = 0, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void renderDowsingRod(AbstractClientPlayerEntity p_228405_1_, float arg1, float arg2, Hand p_228405_4_, float arg3, ItemStack itemStack, float arg4, MatrixStack stack, IRenderTypeBuffer renderTypeBuffer, int i, CallbackInfo ci, boolean flag, HandSide handside) {
        if (itemStack.getItem() == FnCItems.DOWSING_ROD) {
            DowsingRodItem.renderInHand(stack, renderTypeBuffer, i, arg2, arg4, arg3, (FirstPersonRenderer) (Object) this);
            ci.cancel();
            stack.popPose();
        }
    }
}
