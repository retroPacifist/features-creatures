package net.msrandom.featuresandcreatures.mixin.access;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemInHandRenderer.class)
public interface FirstPersonRendererAccess {

    @Accessor
    Minecraft getMinecraft();

    @Invoker
    void invokeRenderMapHand(PoseStack p_228403_1_, MultiBufferSource p_228403_2_, int p_228403_3_, HumanoidArm p_228403_4_);

    @Invoker
    float invokeCalculateMapTilt(float p_178100_1_);
}
