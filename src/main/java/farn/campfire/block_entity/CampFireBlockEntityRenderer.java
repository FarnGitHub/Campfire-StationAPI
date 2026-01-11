package farn.campfire.block_entity;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import org.lwjgl.opengl.GL11;

import net.minecraft.item.ItemStack;

public class CampFireBlockEntityRenderer extends BlockEntityRenderer
{

    protected ItemEntity[] invRender = new ItemEntity[4];
    public static boolean stopRotate = false;

    @Override
    public void render(BlockEntity tile, double x, double y, double z, float scale)
    {
        try {
            if (tile instanceof CampFireBlockEntity ctile)
            {

                for (int slot = 0; slot < ctile.size(); ++slot)
                {
                    ItemStack stack = ctile.getStack(slot);

                    if (stack != null)
                    {
                        int renderSlot = RENDER_SLOT_MAPPING[slot];

                        if (invRender[slot] == null)
                        {
                            invRender[slot] = new ItemEntity(ctile.world, 0,0,0, stack);
                        }
                        else
                            invRender[slot].setWorld(ctile.world);
                        GL11.glPushMatrix();
                        stopRotate = true;
                        GL11.glDisable(GL11.GL_BLEND);
                        double[] position = getRenderPositionFromRenderSlot(renderSlot);
                        GL11.glTranslated(x + position[0], y + position[1], z + position[2]);
                        if (stack.getItem() instanceof BlockItem itemForm && BlockRenderManager.isSideLit(Block.BLOCKS[itemForm.id].getRenderType()))
                        {
                            GL11.glRotatef(renderSlot * 90, 0, 1, 0);
                            GL11.glTranslated(-0.125, -0.01625, 0.0);
                        }
                        else
                        {
                            GL11.glRotatef(180, 0, 1, 1);
                            GL11.glRotatef(renderSlot * -90, 0, 0, 1);
                            GL11.glRotatef(270, 0, 0, 1);
                        }
                        GL11.glScalef(0.625F, 0.625F, 0.625F);
                        invRender[slot].minBrightness = 1.0F;
                        EntityRenderDispatcher.INSTANCE.render(invRender[slot], 0, 0, 0, 0.0F, 0.0F);
                        stopRotate = false;
                        GL11.glPopMatrix();
                    } else {
                        invRender[slot] = null;
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private static final double BASE_X_OFFSET = 0.9375;
    private static final double BASE_Y_OFFSET = 0.45;
    private static final double BASE_Z_OFFSET = 0.9375;
    private static final double ACROSS = 0.875;
    private static final double EDGE = 0.125;
    private static final double[][] RENDER_POSITION_ITEM = new double[][] {
            { BASE_X_OFFSET, BASE_Y_OFFSET, BASE_Z_OFFSET + EDGE - ACROSS },
            { BASE_X_OFFSET - EDGE, BASE_Y_OFFSET, BASE_Z_OFFSET },
            { BASE_X_OFFSET - ACROSS, BASE_Y_OFFSET, BASE_Z_OFFSET - EDGE },
            { BASE_X_OFFSET + EDGE - ACROSS, BASE_Y_OFFSET, BASE_Z_OFFSET - ACROSS } };
    private static final int[] RENDER_SLOT_MAPPING = new int[]{3, 0, 1, 2};
    public static double[] getRenderPositionFromRenderSlot(int renderslot)
    {
        return RENDER_POSITION_ITEM[Math.abs(renderslot) % 4];
    }

}
