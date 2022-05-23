package net.msrandom.featuresandcreatures.mixin.server;


import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.msrandom.featuresandcreatures.common.entity.spawner.FnCSpawnerLevelContext;
import net.msrandom.featuresandcreatures.common.entity.spawner.JockeySpawner;
import net.msrandom.featuresandcreatures.network.JockeyPosPacket;
import net.msrandom.featuresandcreatures.platform.ModPlatform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class MixinPlayerList {

    @Inject(method = "sendLevelInfo", at = @At(value = "HEAD"))
    private void sendContext(ServerPlayer playerIn, ServerLevel worldIn, CallbackInfo ci) {
        JockeySpawner.Context jockeyContext = ((FnCSpawnerLevelContext) worldIn.getLevelData()).jockeyContext();
        if (jockeyContext != null && jockeyContext.getPos() != null) {
            ModPlatform.INSTANCE.sendToClient(playerIn, new JockeyPosPacket(jockeyContext.getPos()));
        }
    }
}