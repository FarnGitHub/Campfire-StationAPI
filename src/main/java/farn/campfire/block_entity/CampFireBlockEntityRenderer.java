package farn.campfire.block_entity;

import farn.farn_util.api.static_item.StaticItemRendererAPI;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

import net.minecraft.item.ItemStack;

public class CampFireBlockEntityRenderer extends BlockEntityRenderer
{

    protected ItemEntity dummyItemEntity;

    @Override
    public void render(BlockEntity tile, double x, double y, double z, float scale) {
        try {
            if (tile instanceof CampFireBlockEntity ctile)
                render(ctile, x, y, z, scale);
        } catch (Exception ignored) {
        }
    }

    private void render(CampFireBlockEntity ctile, double x, double y, double z, float scale) {
        if(dummyItemEntity == null) {
            dummyItemEntity = new ItemEntity(ctile.world);
            dummyItemEntity.setPosition(0,0,0);
        } else if(dummyItemEntity.world != ctile.world)
            dummyItemEntity.setWorld(ctile.world);

        dummyItemEntity.minBrightness = ctile.world.method_1782((int)x,(int)y,(int)z);

        for (int slot = 0; slot < ctile.size(); ++slot) {
            ItemStack stack = ctile.getStack(slot);

            if (stack != null) {
                dummyItemEntity.stack = stack;
                int renderSlot = CampFireRenderHelper.MAPPING[slot];
                GL11.glPushMatrix();
                StaticItemRendererAPI.setStaticItemRender(true);
                GL11.glDisable(GL11.GL_BLEND);
                if (renderAsBlock(stack.getItem())) {
                    double[] position = CampFireRenderHelper.getBlockPos(renderSlot);
                    GL11.glTranslated(x + position[0], y + position[1], z + position[2]);
                    GL11.glRotatef(renderSlot * 90, 0, 1, 0);
                    GL11.glTranslated(-0.125, 0.075, 0.0);
                } else {
                    double[] position = CampFireRenderHelper.getItemPos(renderSlot);
                    GL11.glTranslated(x + position[0], y + position[1], z + position[2]);
                    GL11.glRotatef(180, 0, 1, 1);
                    GL11.glRotatef(renderSlot * -90, 0, 0, 1);
                    GL11.glRotatef(270, 0, 0, 1);
                }
                GL11.glScalef(0.625F, 0.625F, 0.625F);
                EntityRenderDispatcher.INSTANCE.render(dummyItemEntity, 0, 0, 0, 0.0F, 0.0F);
                StaticItemRendererAPI.setStaticItemRender(false);
                GL11.glPopMatrix();
            }
        }
    }

    private boolean renderAsBlock(Item item) {
        return item instanceof BlockItem block
                && BlockRenderManager.isSideLit(block.getBlock().getRenderType());
    }

}
