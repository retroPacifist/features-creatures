package net.msrandom.featuresandcreatures.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.msrandom.featuresandcreatures.core.FnCSounds;
import net.msrandom.featuresandcreatures.entity.spawner.FnCSpawnerLevelContext;
import net.msrandom.featuresandcreatures.entity.spawner.JockeySpawner;

public class DowsingRodItem extends Item {
    public DowsingRodItem(Properties settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (level.isClientSide && selected && entity instanceof PlayerEntity && entity.tickCount % 20 == 0) {
            PlayerEntity player = (PlayerEntity) entity;
            if (!player.getOffhandItem().isEmpty()) return;
            JockeySpawner.Context context = ((FnCSpawnerLevelContext) level.getLevelData()).jockeyContext();
            if (context != null) {
                BlockPos pos = context.getPos();
                if (pos != null) {
                    float pitch = (float) Math.toRadians(entity.xRot);
                    float yaw = (float) Math.toRadians(-entity.yRot);
                    float horizontalFactor = MathHelper.cos(pitch);

                    Vector3d playerForward = new Vector3d(
                            MathHelper.sin(yaw) * horizontalFactor,
                            -MathHelper.sin(pitch),
                            MathHelper.cos(yaw) * horizontalFactor
                    );

                    Vector3d playerToJockey = new Vector3d(
                            pos.getX() + 0.5 - entity.getX(),
                            playerForward.y,
                            pos.getZ() + 0.5 - entity.getZ()
                    );

                    if (playerForward.dot(playerToJockey.normalize()) > 0.99) {
                        // Pointing at jockey
                        player.playNotifySound(FnCSounds.DOWSING_ROD_LOCATES, SoundCategory.PLAYERS, 1f, 1f);
                    }
                }
            }
        }
    }
}
