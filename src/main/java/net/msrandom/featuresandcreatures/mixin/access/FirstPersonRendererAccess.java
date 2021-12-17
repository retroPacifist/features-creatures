package net.msrandom.featuresandcreatures.mixin.access;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.HandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FirstPersonRenderer.class)
public interface FirstPersonRendererAccess {

    @Accessor
    Minecraft getMinecraft();

    @Invoker
    void invokeRenderMapHand(MatrixStack p_228403_1_, IRenderTypeBuffer p_228403_2_, int p_228403_3_, HandSide p_228403_4_);

    @Invoker
    float invokeCalculateMapTilt(float p_178100_1_);
}
