package net.msrandom.featuresandcreatures.common.entities.sabertooth;

import net.minecraft.util.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SabertoothModel<T extends IAnimatable> extends AnimatedGeoModel<T> {

	@Override
	public ResourceLocation getModelLocation(T object) {
		return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "geo/sabertooth.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(T object) {
		return SabertoothRenderer.TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(T animatable) {
		return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "animations/sabertooth.animation.json");

	}
}