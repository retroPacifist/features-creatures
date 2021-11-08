package net.msrandom.featuresandcreatures.common.entities.jackalope;

import net.minecraft.util.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class JackalopeModel<T extends IAnimatable> extends AnimatedGeoModel<T> {

	@Override
	public ResourceLocation getModelLocation(T object) {
		return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "geo/jackalope.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(T object) {
		return JackalopeRenderer.TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(T animatable) {
		return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "animations/jackalope.animation.json");

	}
}