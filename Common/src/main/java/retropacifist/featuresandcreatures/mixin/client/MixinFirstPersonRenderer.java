package retropacifist.featuresandcreatures.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import retropacifist.featuresandcreatures.common.item.DowsingRodItem;
import retropacifist.featuresandcreatures.common.item.FeaturesCreaturesItems;

@Mixin(ItemInHandRenderer.class)
public abstract class MixinFirstPersonRenderer {

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void renderDowsingRod(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float attackAnimation, ItemStack itemStack, float handHeight, PoseStack poseStack, MultiBufferSource bufferProvider, int packedLight, CallbackInfo ci) {
        if (player.getMainHandItem().getItem() == FeaturesCreaturesItems.DOWSING_ROD.get() || player.getOffhandItem().getItem() == FeaturesCreaturesItems.DOWSING_ROD.get()) {
            if (itemStack.getItem() == FeaturesCreaturesItems.DOWSING_ROD.get()) {
                DowsingRodItem.renderInHand(player, itemStack, poseStack, hand, bufferProvider, packedLight, pitch, handHeight, attackAnimation, (ItemInHandRenderer) (Object) this);
            }
            ci.cancel();
        }
    }
}
