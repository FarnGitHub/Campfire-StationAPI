package farn.campfire.block;

import farn.campfire.CampFireStationAPI;
import farn.campfire.block_entity.CampFireBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.client.model.block.BlockWithInventoryRenderer;
import net.modificationstation.stationapi.api.client.model.block.BlockWithWorldRenderer;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.Random;

@EnvironmentInterface(value = EnvType.CLIENT, itf = BlockWithWorldRenderer.class)
@EnvironmentInterface(value = EnvType.CLIENT, itf = BlockWithInventoryRenderer.class)
public class CampFireBlock extends TemplateBlockWithEntity implements BlockWithWorldRenderer, BlockWithInventoryRenderer {
    private final Random random = new Random();

    public CampFireBlock(Identifier identifier, Material material) {
        super(identifier, material);
        this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.4375F, 1.0F);
        this.textureId = CampFireStationAPI.campfire_log;
        this.setLuminance(1.0F);
    }

    public Box getCollisionShape(World world, int x, int y, int z) {
        return Box.createCached(x, y, z, x + 1.0F, y + 0.4375F, z + 1.0F);
    }

    @Override
    public int getTexture(int side, int meta) {
        return meta == -2 ? CampFireStationAPI.campfire_log_lit : (meta == -3 ? CampFireStationAPI.campfire_fire : CampFireStationAPI.campfire_log);
    }

    public void updateBoundingBox(BlockView blockView, int x, int y, int z) {
        this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.4375F, 1.0F);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean renderWorld(BlockRenderManager tileRenderer, BlockView tileView, int x, int y, int z) {
        return CampFireBlockRenderer.INSTANCE.renderWorldBlock(tileView, x,y,z, this, tileRenderer);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void renderInventory(BlockRenderManager tileRenderer, int meta) {
        CampFireBlockRenderer.INSTANCE.renderInventoryBlock(this, tileRenderer);
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new CampFireBlockEntity();
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        if(!world.isRemote) {
            CampFireBlockEntity campfireEntity = (CampFireBlockEntity) world.getBlockEntity(x, y, z);
            if(campfireEntity.insertFood(player.inventory.getSelectedItem())) {
                player.inventory.removeStack(player.inventory.selectedSlot, 1);
            }
        }
        return true;
    }

    @Override
    public void onBreak(World world, int x, int y, int z) {
        CampFireBlockEntity campFire = (CampFireBlockEntity)world.getBlockEntity(x, y, z);

        for(int index = 0; index < campFire.size(); ++index) {
            ItemStack stack = campFire.getStack(index);
            if (stack != null) {
                float veloX = this.random.nextFloat() * 0.8F + 0.1F;
                float veloY = this.random.nextFloat() * 0.8F + 0.1F;
                float veloZ = this.random.nextFloat() * 0.8F + 0.1F;

                while(stack.count > 0) {
                    int countReducer = this.random.nextInt(21) + 10;
                    if (countReducer > stack.count) {
                        countReducer = stack.count;
                    }

                    stack.count -= countReducer;
                    ItemEntity item = new ItemEntity(world, (double) x + veloX, (double)y + veloY, (double)z + veloZ, new ItemStack(stack.itemId, countReducer, stack.getDamage()));
                    float offset = 0.05F;
                    item.velocityX = this.random.nextGaussian() * offset;
                    item.velocityY = this.random.nextGaussian() * offset + 0.2F;
                    item.velocityZ = this.random.nextGaussian() * offset;
                    world.spawnEntity(item);
                }
            }
        }

        super.onBreak(world, x, y, z);
    }

    public int getDroppedItemCount(Random random) {
        return 2;
    }

    protected int getDroppedItemMeta(int blockMeta) {
        return 1;
    }

    public int getDroppedItemId(int blockMeta, Random random) {
        return Item.COAL.id;
    }
}
