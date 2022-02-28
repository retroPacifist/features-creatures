package net.msrandom.featuresandcreatures.client.model;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class SpearModel extends Model {
	private final ModelRenderer pole = new ModelRenderer(32, 32, 0, 6);

	public SpearModel() {
		super(RenderType::entityCutout);
		texWidth = 32;
		texHeight = 32;

		pole.setPos(0.0F, 0.0F, 0.0F);
		pole.texOffs(0, 0).addBox(-0.5F, 1.0F, -0.5F, 1.0F, 26.0F, 1.0F, 0.01F, false);
		pole.texOffs(4, 9).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);
		pole.texOffs(4, 0).addBox(-2.5F, -9.0F, 0.0F, 5.0F, 9.0F, 0.0F, 0.0F, false);
	}

	@Override
	public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		pole.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
