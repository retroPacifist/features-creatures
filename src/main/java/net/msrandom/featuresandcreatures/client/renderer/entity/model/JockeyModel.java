package net.msrandom.featuresandcreatures.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.msrandom.featuresandcreatures.entity.JockeyEntity;

public class JockeyModel extends SegmentedModel<JockeyEntity> {
	public ModelRenderer head;
	public ModelRenderer torso;
	public ModelRenderer backpack;
	public ModelRenderer rightArm;
	public ModelRenderer leftArm;
	public ModelRenderer rightLeg;
	public ModelRenderer leftLeg;
	private final Iterable<ModelRenderer> parts;

	public JockeyModel() {
		texWidth = 64;
		texHeight = 32;

		head = new ModelRenderer(this);
		head.setPos(0.0F, 13.0F, 0.0F);
		head.texOffs(0, 0).addBox(-3.5F, -4.0F, -1.5F, 7.0F, 4.0F, 3.0F, 0.0F, false);

		torso = new ModelRenderer(this);
		torso.setPos(0.0F, 19.0F, 0.0F);
		torso.texOffs(0, 7).addBox(-2.0F, -6.0F, -1.0F, 4.0F, 6.0F, 2.0F, 0.0F, false);

		backpack = new ModelRenderer(this);
		backpack.setPos(0.0F, -4.0F, 2.0F);
		torso.addChild(backpack);
		backpack.texOffs(28, 12).addBox(-2.0F, -2.0F, -1.0F, 4.0F, 4.0F, 2.0F, 0.25F, false);
		backpack.texOffs(16, 12).addBox(-2.0F, -2.0F, -1.0F, 4.0F, 4.0F, 2.0F, 0.0F, false);
		backpack.texOffs(40, 13).addBox(-0.5F, -1.0F, 0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);

		rightArm = new ModelRenderer(this);
		rightArm.setPos(-2.0F, 14.0F, 0.0F);
		rightArm.texOffs(0, 15).addBox(-1.0F, -0.5F, -0.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);

		leftArm = new ModelRenderer(this);
		leftArm.setPos(2.0F, 14.0F, 0.0F);
		leftArm.texOffs(12, 12).addBox(0.0F, -0.5F, -0.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);

		rightLeg = new ModelRenderer(this);
		rightLeg.setPos(-1.5F, 19.0F, 0.0F);
		rightLeg.texOffs(8, 15).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);

		leftLeg = new ModelRenderer(this);
		leftLeg.setPos(1.5F, 19.0F, 0.0F);
		leftLeg.texOffs(4, 15).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);

		parts = ImmutableList.of(head, torso, rightArm, leftArm, rightLeg, leftLeg);
	}

	@Override
	public void setupAnim(JockeyEntity p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
	}

	@Override
	public Iterable<ModelRenderer> parts() {
		return parts;
	}
}
