package farn.campfire.packet;

import farn.campfire.CampFireStationAPI;
import farn.campfire.block_entity.CampFireBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.SideUtil;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class CampfireUpdatePacket extends Packet implements ManagedPacket<CampfireUpdatePacket> {

    public NbtCompound data;
    public int dataSize = 0;
    public static final PacketType<CampfireUpdatePacket> TYPE = PacketType.builder(true, false, CampfireUpdatePacket::new).build();

    public CampfireUpdatePacket() {
        worldPacket = true;
    }

    @Environment(EnvType.SERVER)
    public CampfireUpdatePacket(CampFireBlockEntity te) {
        this();
        data = new NbtCompound();
        te.writeNbtLite(data);
    }

    @Override
    public void read(DataInputStream stream) {
        data = readNbt(stream);
    }

    public NbtCompound readNbt(DataInputStream dis) {
        try {
            int length = Short.toUnsignedInt(dis.readShort());
            if (length == 0) {
                return null;
            } else {
                byte[] data = new byte[length];
                dis.readFully(data);
                return NbtIo.readCompressed(new ByteArrayInputStream(data));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeNbt(NbtCompound tag, DataOutputStream dos) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            NbtIo.writeCompressed(tag, baos);
            byte[] buffer = baos.toByteArray();
            dos.writeShort((short)buffer.length);
            dos.write(buffer);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        int before = stream.size();
        writeNbt(data, stream);
        dataSize = stream.size() - before;
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        SideUtil.run(() -> handleClient(networkHandler),()->{});
    }

    @Environment(EnvType.CLIENT)
    public void handleClient(NetworkHandler networkHandler) {
        try {
            PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
            int x = data.getInt("x");
            int y = data.getInt("y");
            int z = data.getInt("z");
            if(player.world.getBlockId(x,y,z) == CampFireStationAPI.campfire_block.id &&
                    player.world.getBlockEntity(x,y,z) instanceof CampFireBlockEntity armorStandBlock) {
                armorStandBlock.readNbtLite(data);
                player.world.setBlockDirty(x,y,z);
            }
        } catch (Exception e){
            CampFireStationAPI.LOGGER.info(e);
        }

    }

    @Override
    public int size() {
        return dataSize;
    }

    @Override
    public @NotNull PacketType<CampfireUpdatePacket> getType() {
        return TYPE;
    }
}
