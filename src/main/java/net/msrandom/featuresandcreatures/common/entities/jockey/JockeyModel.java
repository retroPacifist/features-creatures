package net.msrandom.featuresandcreatures.common.entities.jockey;

import net.minecraft.util.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.common.entities.jackalope.JackalopeRenderer;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class JockeyModel extends AnimatedGeoModel<Jockey>{

	@Override
	public ResourceLocation getModelLocation(Jockey object) {
		return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "geo/jockey.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(Jockey object) {
		return JackalopeRenderer.TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(Jockey animatable) {
		return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "animations/jockey.animation.json");
	}
}