package farn.campfire.packet;

import farn.campfire.block_entity.CampFireBlockEntity;
import farn.campfire.mixin.NbtCompoundAcc;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.SideUtil;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//update client food item
public class PacketUpdateCampfireItem extends Packet implements ManagedPacket<PacketUpdateCampfireItem> {
    public static final PacketType<PacketUpdateCampfireItem> TYPE = PacketType.builder(true, false, PacketUpdateCampfireItem::new).build();

    public int x;
    public int y;
    public int z;
    public NbtCompound compound;
    private int length;

    public PacketUpdateCampfireItem() {
    }

    public PacketUpdateCampfireItem(int x, int y, int z, NbtCompound list) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.compound = list;
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            x = stream.readInt();
            y = stream.readInt();
            z = stream.readInt();
            compound = new NbtCompound();
            ((NbtCompoundAcc) compound).campfire_read(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    // This will be called when the packet is being sent
    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeInt(x);
            stream.writeInt(y);
            stream.writeInt(z);
            ((NbtCompoundAcc) compound).campfire_write(stream);
            length = stream.size();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler handler) {
        SideUtil.run(
                () -> handleClient(handler),
                () -> handleServer(handler)
        );
    }

    @Environment(EnvType.CLIENT)
    public void handleClient(NetworkHandler handler) {
        if(PlayerHelper.getPlayerFromGame().world.getBlockEntity(x,y,z) instanceof CampFireBlockEntity campfire) {
            campfire.readNbt(compound);
        }
    }

    @Environment(EnvType.SERVER)
    public void handleServer(NetworkHandler handler) {
    }

    @Override
    public int size() {
        return 12 + length;
    }

    @Override
    public @NotNull PacketType<PacketUpdateCampfireItem> getType() {
        return TYPE;
    }
}
