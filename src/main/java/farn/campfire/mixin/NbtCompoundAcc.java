package farn.campfire.mixin;

import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.io.DataInput;
import java.io.DataOutput;

@Mixin(NbtCompound.class)
public interface NbtCompoundAcc {

    @Invoker("read")
    void campfire_read(DataInput input);

    @Invoker("write")
    void campfire_write(DataOutput output);

}
