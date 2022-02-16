package net.msrandom.featuresandcreatures.client.model;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.entity.Spear;

public class SpearModel<T extends Spear> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(FeaturesAndCreatures.MOD_ID, "spear"), "main");
	private final ModelPart pole;
	public SpearModel(ModelPart root) {
		this.pole = root.getChild("pole");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition pole = partdefinition.addOrReplaceChild("pole", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition pole_r1 = pole.addOrReplaceChild("pole_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -13.0F, 1.0F, 1.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(0, 13).addBox(-1.0F, 0.5F, -17.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(10, 13).addBox(-1.0F, -0.5F, -15.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		pole.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void setupAnim(T p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_) {
	}
}
