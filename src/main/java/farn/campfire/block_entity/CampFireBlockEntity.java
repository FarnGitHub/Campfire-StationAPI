package farn.campfire.block_entity;

import farn.campfire.CampFireStationAPI;
import farn.campfire.particle.CampfireSmokeEffect;
import farn.campfire.recipe.CampFireRecipeManager;
import farn.farn_util.FarnUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.FireSmokeParticle;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.network.packet.MessagePacket;
import net.modificationstation.stationapi.api.util.SideUtil;

import java.util.Random;

public class CampFireBlockEntity extends BlockEntity implements Inventory
{
    private ItemStack[] cooking_food = new ItemStack[4];
    private int[] cookingDuration = new int[cooking_food.length];
    private int cookingTimeLimit = CampFireStationAPI.getCookingFinishedTime();

    private final boolean isServer = FabricLoader.getInstance().getEnvironmentType().equals(EnvType.SERVER);

    //mark every item add or removed so the server sent item display to client
    private boolean dirty = true;

    //called when item is finished cooking
    public void finishCookedFood(int slotIndex) {
        if(cooking_food[slotIndex] != null) {
            ItemStack stack = CampFireRecipeManager.getResultFor(cooking_food[slotIndex]);
            if(stack == null) {
                stack = cooking_food[slotIndex];
            }
            dropUnbuggedItem(stack, world, x,y,z);
            removeStack(slotIndex, 1);
            cookingDuration[slotIndex] = 0;
        }
    }

    @Environment(EnvType.CLIENT)
    private void renderParticle()
    {
        if (world.random.nextFloat() < 0.11F)
        {
            for (int i = 0; i < world.random.nextInt(2) + 2; ++i)
                FarnUtil.addParticle(new CampfireSmokeEffect(world, x, y, z));
        }
    }

    //sometime the item bugged out and drop the glitched item that keep duplicate
    public static void dropUnbuggedItem(ItemStack stack, World world, int x, int y, int z)
    {
        dropItem(new ItemStack(stack.itemId, 1, stack.getDamage()), world, x, y, z);
    }

    //normal item drooped
    public static void dropItem(ItemStack stack, World world, int x, int y, int z)
    {
        if (stack != null && stack.count > 0)
        {
            Random rand = world.random;
            ItemEntity entityitem = new ItemEntity(world, x + rand.nextDouble() * 0.75 + 0.125, y + rand.nextDouble() * 0.375 + 0.5, z, stack);

            entityitem.velocityX = rand.nextGaussian() * 0.025;
            entityitem.velocityY = rand.nextGaussian() * 0.025 + 0.2;
            entityitem.velocityZ = rand.nextGaussian() * 0.025;

            world.spawnEntity(entityitem);
        }
    }

    //insert the item inside campfire
    public boolean insertFood(ItemStack stack) {
        if(stack != null && CampFireRecipeManager.getResultFor(stack) != null) {
            for(int slotIndex = 0; slotIndex < cooking_food.length; ++slotIndex) {
                if(cooking_food[slotIndex] == null) {
                    setStack(slotIndex, stack);
                    cookingDuration[slotIndex] = 0;
                    return true;
                }
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
        this.cooking_food = new ItemStack[this.size()];

        for(int var3 = 0; var3 < var2.size(); ++var3) {
            NbtCompound var4 = (NbtCompound)var2.get(var3);
            byte var5 = var4.getByte("Slot");
            if (var5 >= 0 && var5 < this.cooking_food.length) {
                this.cooking_food[var5] = new ItemStack(var4);
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("cookedTime", cookingDuration);
        NbtList var2 = new NbtList();

        for(int var3 = 0; var3 < this.cooking_food.length; ++var3) {
            if (this.cooking_food[var3] != null) {
                NbtCompound var4 = new NbtCompound();
                var4.putByte("Slot", (byte)var3);
                this.cooking_food[var3].writeNbt(var4);
                var2.add(var4);
            }
        }

        nbt.put("Items", var2);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (this.cooking_food[slot] != null) {
            if (this.cooking_food[slot].count <= amount) {
                ItemStack var4 = this.cooking_food[slot];
                this.cooking_food[slot] = null;
                dirty = true;
                return var4;
            } else {
                ItemStack var3 = this.cooking_food[slot].split(amount);
                if (this.cooking_food[slot].count == 0) {
                    this.cooking_food[slot] = null;
                }
                dirty = true;
                return var3;
            }
        } else {
            return null;
        }
    }

    @Environment(EnvType.SERVER)
    public Packet createUpdatePacket() {
        MessagePacket packet = new MessagePacket(CampFireStationAPI.NAMESPACE.id("campfire_client"));
        packet.ints = new int[11];
        packet.ints[0] = x;
        packet.ints[1] = y;
        packet.ints[2] = z;
        for(int index = 0; index < 4; ++index) {
            if(cooking_food[index] != null) {
                packet.ints[index + 3] = cooking_food[index].itemId;
                packet.ints[3 + index + 4] = cooking_food[index].getDamage();
            } else {
                packet.ints[index + 3] = 0;
                packet.ints[3 + index + 4] = 0;
            }
        }
        return packet;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.cooking_food[slot] = stack;
        if (stack != null && stack.count > this.getMaxCountPerStack()) {
            stack.count = this.getMaxCountPerStack();
        }
        dirty = true;
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
            if(getPushedBlockData() == 0) {
                for(int slotIndex = 0; slotIndex < cooking_food.length; ++slotIndex) {
                    if(cooking_food[slotIndex] != null) {
                        if(cookingDuration[slotIndex] >= cookingTimeLimit)
                            finishCookedFood(slotIndex);
                        else
                            ++cookingDuration[slotIndex];
                    }
                }
            }
        }

        if(isServer) {
            if(dirty) {
                this.markDirty();
                dirty = false;
            }
        } else if(CampFireStationAPI.shouldRenderSmoke()) {
            renderParticle();
        }
    }

    @Override
    public int size() {
        return this.cooking_food.length;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.cooking_food[slot];
    }
}
