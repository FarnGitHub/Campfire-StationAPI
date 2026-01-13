package farn.campfire.mixin;

import farn.campfire.block_entity.CampFireBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @Shadow public abstract void sendToAround(double x, double y, double z, double range, int dimensionId, Packet packet);

    @Inject(method="updateBlockEntity", at = @At("HEAD"))
    public void updateBlockEntity(int x, int y, int z, BlockEntity be, CallbackInfo ci) {
        if(be instanceof CampFireBlockEntity campfire) {
            sendToAround(x,y,z, 64, campfire.world.dimension.id,campfire.createUpdatePacket());
        }

    }
}
