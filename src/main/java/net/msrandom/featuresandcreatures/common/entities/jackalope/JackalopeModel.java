package net.msrandom.featuresandcreatures.common.entities.jackalope;

import net.minecraft.util.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class JackalopeModel extends AnimatedGeoModel<Jackalope> {

	@Override
	public ResourceLocation getModelLocation(Jackalope object) {
		return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "geo/jackalope.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(Jackalope object) {
		return JackalopeRenderer.TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(Jackalope animatable) {
		return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "animations/jackalope.animation.json");

	}
}