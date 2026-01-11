package farn.campfire.packet;

import farn.campfire.block_entity.CampFireBlockEntity;
import farn.campfire.mixin.NbtListAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
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
    public NbtList list;
    private int listSize;

    public PacketUpdateCampfireItem() {
    }

    @Environment(EnvType.SERVER)
    public PacketUpdateCampfireItem(int x, int y, int z, NbtList list) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.list = list;
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            x = stream.readInt();
            y = stream.readInt();
            z = stream.readInt();
            ((NbtListAccessor) list).campfire_read(stream);
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
            writeCampfireItemList(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeCampfireItemList(DataOutputStream stream) {
        ((NbtListAccessor) list).campfire_write(stream);
        try {
            stream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        listSize = stream.size();
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
            if(list != null) {
                for(int index = 0; index < campfire.item.length; ++index) {
                    if(list.get(index) instanceof NbtCompound compound) {
                        campfire.item[index] = new ItemStack(compound);
                    }
                }
            }
        }
    }

    @Environment(EnvType.SERVER)
    public void handleServer(NetworkHandler handler) {
    }

    @Override
    public int size() {
        return 12 + listSize;
    }

    @Override
    public @NotNull PacketType<PacketUpdateCampfireItem> getType() {
        return TYPE;
    }
}
