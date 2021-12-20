package net.msrandom.featuresandcreatures.mixin.server;


import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.server.ServerWorld;
import net.msrandom.featuresandcreatures.entity.spawner.FnCSpawnerLevelContext;
import net.msrandom.featuresandcreatures.entity.spawner.JockeySpawner;
import net.msrandom.featuresandcreatures.network.JockeyPosPacket;
import net.msrandom.featuresandcreatures.network.NetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class MixinPlayerList {

    @Inject(method = "sendLevelInfo", at = @At(value = "HEAD"))
    private void sendContext(ServerPlayerEntity playerIn, ServerWorld worldIn, CallbackInfo ci) {
        JockeySpawner.Context jockeyContext = ((FnCSpawnerLevelContext) worldIn.getLevelData()).jockeyContext();
        if (jockeyContext != null && jockeyContext.getPos() != null) {
            NetworkHandler.sendToClient(playerIn, new JockeyPosPacket(jockeyContext.getPos()));
        }
    }
}