package retropacifist.featuresandcreatures.mixin.server;


import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import retropacifist.featuresandcreatures.common.entity.spawner.FnCSpawnerLevelContext;
import retropacifist.featuresandcreatures.common.entity.spawner.JockeySpawner;
import retropacifist.featuresandcreatures.common.network.JockeyPosPacket;

@Mixin(PlayerList.class)
public abstract class MixinPlayerList {

    @Inject(method = "sendLevelInfo", at = @At(value = "HEAD"))
    private void sendContext(ServerPlayer playerIn, ServerLevel worldIn, CallbackInfo ci) {
        JockeySpawner.Context jockeyContext = ((FnCSpawnerLevelContext) worldIn.getLevelData()).jockeyContext();
        if (jockeyContext != null && jockeyContext.getPos() != null) {
            NetworkHandler.sendToClient(playerIn, new JockeyPosPacket(jockeyContext.getPos()));
        }
    }
}