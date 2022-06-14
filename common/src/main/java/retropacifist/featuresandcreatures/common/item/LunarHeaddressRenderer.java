package retropacifist.featuresandcreatures.common.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import retropacifist.featuresandcreatures.client.model.LunarHeaddressModel;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class LunarHeaddressRenderer extends GeoArmorRenderer<LunarHeaddressItem> {
    public LunarHeaddressRenderer() {
        super(new LunarHeaddressModel());
        this.headBone = "head";
        this.bodyBone = "body";
        this.rightArmBone = "rarm";
        this.leftArmBone = "larm";
        this.rightLegBone = "rleg";
        this.leftLegBone = "lleg";
        this.rightBootBone = "rboot";
        this.leftBootBone = "lboot";
    }

    @Override
    public RenderType getRenderType(LunarHeaddressItem animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(LunarHeaddressModel.TEXTURE);
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(stack, bufferIn, packedLightIn);
    }
}