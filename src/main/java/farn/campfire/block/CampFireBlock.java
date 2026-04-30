package farn.campfire.block;

import farn.campfire.CampFireStationAPI;
import farn.campfire.block_entity.CampFireBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.List;
import java.util.Random;

public class CampFireBlock extends TemplateBlockWithEntity {

    public CampFireBlock(Identifier identifier, Material material) {
        super(identifier, material);
        this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.4375F, 1.0F);
        this.textureId = CampFireStationAPI.campfire_log;
        this.setLuminance(1.0F);
    }

    public Box getCollisionShape(World world, int x, int y, int z) {
        return Box.createCached(x, y, z, x + 1.0F, y + 0.4375F, z + 1.0F);
    }

    public void updateBoundingBox(BlockView blockView, int x, int y, int z) {
        this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.4375F, 1.0F);
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public boolean isFullCube() {
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
            if(campfireEntity.insertFood(player.inventory.getSelectedItem()))
                player.inventory.removeStack(player.inventory.selectedSlot, 1);
            else
                return false;
        }
        return true;
    }

    @Override
    public void onBreak(World world, int x, int y, int z) {
        if(!world.isRemote) {
            Random random = world.random;
            CampFireBlockEntity campFire = (CampFireBlockEntity)world.getBlockEntity(x, y, z);

            for(int index = 0; index < campFire.size(); ++index) {
                ItemStack stack = campFire.getStack(index);
                if (stack != null) {
                    float veloX = random.nextFloat() * 0.8F + 0.1F;
                    float veloY = random.nextFloat() * 0.8F + 0.1F;
                    float veloZ = random.nextFloat() * 0.8F + 0.1F;

                    while(stack.count > 0) {
                        int countReducer = random.nextInt(21) + 10;
                        if (countReducer > stack.count) {
                            countReducer = stack.count;
                        }

                        stack.count -= countReducer;
                        ItemEntity item = new ItemEntity(world, (double) x + veloX, (double)y + veloY, (double)z + veloZ, new ItemStack(stack.itemId, countReducer, stack.getDamage()));
                        float offset = 0.05F;
                        item.velocityX = random.nextGaussian() * offset;
                        item.velocityY = random.nextGaussian() * offset + 0.2F;
                        item.velocityZ = random.nextGaussian() * offset;
                        world.spawnEntity(item);
                    }
                }
            }
        }

        super.onBreak(world, x, y, z);
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        Direction direction = context.getHorizontalPlayerFacing().rotateClockwise(Direction.Axis.Y);

        return getDefaultState().with(Properties.HORIZONTAL_FACING, direction);
    }

    @Override
    public List<ItemStack> getDropList(World world, int x, int y, int z, BlockState state, int meta) {
        return List.of(new ItemStack(Item.COAL.id, 2, 1));
    }
}
