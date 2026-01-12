package farn.campfire.block_entity;

import farn.campfire.recipe.CampFireRecipeManager;
import farn.campfire.packet.PacketUpdateCampfireItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.World;

import java.util.Random;

public class CampFireBlockEntity extends BlockEntity implements Inventory
{
    public ItemStack[] item = new ItemStack[4];
    protected int[] cookingDuration = new int[item.length];
    protected static final Random rand = new Random();


    //called when item is finished cooking
    public void finishCookedItem(int slotIndex) {
        dropUnbuggedItem(CampFireRecipeManager.getResultFor(item[slotIndex]), world, x,y,z);
        removeStack(slotIndex, 1);
        cookingDuration[slotIndex] = 0;
        this.markDirty();
    }

    //sometime the item bugged out and drop the glitched item that keep duplicate
    public static void dropUnbuggedItem(ItemStack stack, World world, int x, int y, int z)
    {
        ItemStack newStack = new ItemStack(stack.itemId, 1, stack.getDamage());
        dropItem(newStack, world, x, y, z);
    }

    //normal item drooped
    public static void dropItem(ItemStack stack, World world, int x, int y, int z)
    {
        if (stack != null && stack.count > 0)
        {
            ItemEntity entityitem = new ItemEntity(world, x + rand.nextDouble() * 0.75 + 0.125, y + rand.nextDouble() * 0.375 + 0.5, z, stack);

            entityitem.velocityX = rand.nextGaussian() * 0.05;
            entityitem.velocityY = rand.nextGaussian() * 0.05 + 0.2;
            entityitem.velocityZ = rand.nextGaussian() * 0.05;

            world.spawnEntity(entityitem);
        }
    }

    //insert the item inside campfire
    public boolean insertFood(ItemStack stack) {
        if(stack == null || CampFireRecipeManager.getResultFor(stack) == null || item[item.length - 1] != null) return false;
        for(int slotIndex = 0; slotIndex < item.length; ++slotIndex) {
            if(item[slotIndex] == null) {
                setStack(slotIndex, stack);
                cookingDuration[slotIndex] = 0;
                this.markDirty();
                return true;
            }
        }
        return false;
    }

    //vanilla staff

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        cookingDuration = nbt.getIntArray("cookedTime");
        NbtList var2 = nbt.getList("Items");
        this.item = new ItemStack[this.size()];

        for(int var3 = 0; var3 < var2.size(); ++var3) {
            NbtCompound var4 = (NbtCompound)var2.get(var3);
            byte var5 = var4.getByte("Slot");
            if (var5 >= 0 && var5 < this.item.length) {
                this.item[var5] = new ItemStack(var4);
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("cookedTime", cookingDuration);
        NbtList var2 = new NbtList();

        for(int var3 = 0; var3 < this.item.length; ++var3) {
            if (this.item[var3] != null) {
                NbtCompound var4 = new NbtCompound();
                var4.putByte("Slot", (byte)var3);
                this.item[var3].writeNbt(var4);
                var2.add(var4);
            }
        }

        nbt.put("Items", var2);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (this.item[slot] != null) {
            if (this.item[slot].count <= amount) {
                ItemStack var4 = this.item[slot];
                this.item[slot] = null;
                return var4;
            } else {
                ItemStack var3 = this.item[slot].split(amount);
                if (this.item[slot].count == 0) {
                    this.item[slot] = null;
                }

                return var3;
            }
        } else {
            return null;
        }
    }

    @Environment(EnvType.SERVER)
    public Packet createUpdatePacket() {
        NbtList list = new NbtList();
        for(ItemStack stack : item) {
            if(stack != null) list.add(stack.writeNbt(new NbtCompound()));
        }
        return new PacketUpdateCampfireItem(x,y,z, list);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.item[slot] = stack;
        if (stack != null && stack.count > this.getMaxCountPerStack()) {
            stack.count = this.getMaxCountPerStack();
        }
    }

    @Override
    public String getName() {
        return "Campfire";
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.x, this.y, this.z) != this) {
            return false;
        } else {
            return !(player.getSquaredDistance((double)this.x + 0.5, (double)this.y + 0.5, (double)this.z + 0.5) > 64.0);
        }
    }

    @Override
    public void tick() {
        if (!this.world.isRemote) {
            for(int slotIndex = 0; slotIndex < item.length; ++slotIndex) {
                if(item[slotIndex] != null) {
                    if(cookingDuration[slotIndex] >= 600)
                        finishCookedItem(slotIndex);
                    else
                        ++cookingDuration[slotIndex];

                }
            }
        }
    }

    @Override
    public int size() {
        return this.item.length;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.item[slot];
    }
}
