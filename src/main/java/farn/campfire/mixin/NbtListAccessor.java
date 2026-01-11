package farn.campfire.mixin;

import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.io.DataInput;
import java.io.DataOutput;

//for packet staff
@Mixin(NbtList.class)
public interface NbtListAccessor {

    @Invoker("read")
    void campfire_read(DataInput input);

    @Invoker("write")
    void campfire_write(DataOutput output);
}
