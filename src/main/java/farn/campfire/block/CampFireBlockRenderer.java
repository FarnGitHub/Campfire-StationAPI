package farn.campfire.block;

import javax.annotation.Nullable;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.world.BlockView;
import net.modificationstation.stationapi.api.client.texture.Sprite;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import org.lwjgl.opengl.GL11;


import net.minecraft.block.Block;

/**
 * Original code by connor135246
 */
public class CampFireBlockRenderer
{
    public static final CampFireBlockRenderer INSTANCE = new CampFireBlockRenderer();

    public boolean renderWorldBlock(BlockView access, int x, int y, int z, Block block, BlockRenderManager renderer)
    {
        renderer.inventoryColorEnabled = false;
        renderCampfire(access, x, y, z, block, access.getBlockMeta(x, y, z), renderer, false, false, false);
        return true;
    }

    public void renderInventoryBlock(Block block, BlockRenderManager renderer)
    {
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        renderer.inventoryColorEnabled = true;
        renderCampfire(null, block, 5, renderer, true, false, true);
        renderer.inventoryColorEnabled = false;
    }

    public static void renderCampfire(@Nullable BlockView access, final Block block, final int meta, final BlockRenderManager renderer, final boolean doDraw,
                                      final boolean mixedFire, final boolean flatSideColor)
    {
        renderCampfire(access, 0, 0, 0, block, meta, renderer, doDraw, mixedFire, flatSideColor);
    }

    public static void renderCampfire(@Nullable BlockView access, final int x, final int y, final int z, final Block block, final int meta,
                                      final BlockRenderManager renderer, final boolean doDraw, final boolean mixedFire, final boolean flatSideColor)
    {
        Tessellator tess = Tessellator.INSTANCE;

        final boolean enableAO = renderer.useAo;
        renderer.useAo = false;

        // 16777215 is the default return of Block.colorMultiplier
        int color = access == null ? 16777215 : block.getColorMultiplier(access, x, y, z);
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;

        if (GameRenderer.anaglyph3d)
        {
            float anar = (r * 30.0F + g * 59.0F + b * 11.0F) / 100.0F;
            float anag = (r * 30.0F + g * 70.0F) / 100.0F;
            float anab = (r * 30.0F + b * 70.0F) / 100.0F;
            r = anar;
            g = anag;
            b = anab;
        }

        final boolean isLit = true;
        final boolean northSouth = !(meta == 4 || meta == 5);
        // sideLogY: 3 pixels, from the ground to the bottom face of the upper logs
        // logY1: 4 pixels, from the ground to top face of the lower logs
        // logY2: 7 pixels, from the ground to top face of the upper logs
        // firepitO: 5 pixels, from a side perpendicular to the facing direction to the edge of the firepit
        // logO1: 1 pixel, from a side to the edge of the closer parallel log
        // logO2: 11 pixels, from a side to the edge of the further parallel log
        final double sideLogY = 0.1875, logY1 = 0.25, logY2 = 0.4375, firepitO = 0.3125, logO1 = 0.0625, logO2 = 0.6875;
        final float colorYNeg, colorYPos, colorZ, colorX;
        if (flatSideColor)
            colorYNeg = colorYPos = colorZ = colorX = 1.0F;
        else
        {
            colorYNeg = 0.5F;
            colorYPos = 1.0F;
            colorZ = 0.8F;
            colorX = 0.6F;
        }

        if (doDraw)
        {
            tess.startQuads();
            tess.normal(0.0F, -1.0F, 0.0F);
        }

        if(!renderer.inventoryColorEnabled) tess.color(r * colorYNeg, g * colorYNeg, b * colorYNeg);

        // upper log bottoms
        renderLogTopOrBot(northSouth ? x : (x + logO1), y + sideLogY, northSouth ? (z + logO1) : z, block, renderer, 0,
                meta == 3 ? 0 : (meta == 4 ? 2 : (meta == 5 ? 1 : 3)), isLit);
        renderLogTopOrBot(northSouth ? x : (x + logO2), y + sideLogY, northSouth ? (z + logO2) : z, block, renderer, 0,
                meta == 3 ? 0 : (meta == 4 ? 2 : (meta == 5 ? 1 : 3)), isLit);

        // bottom sides
        if (renderer.skipFaceCulling || access == null || block.isSideVisible(access, x, y - 1, z, 0))
        {
            final int rotateFromMeta = meta == 3 ? 2 : (meta == 4 ? 3 : (meta == 5 ? 0 : 1));

            renderLogTopOrBot(northSouth ? (x + logO1) : x, y, northSouth ? z : (z + logO1), block, renderer, 0, rotateFromMeta, false);
            renderLogTopOrBot(northSouth ? (x + logO2) : x, y, northSouth ? z : (z + logO2), block, renderer, 0, rotateFromMeta, false);
            renderFirepitTopOrBot(northSouth ? (x + firepitO) : x, y, northSouth ? z : (z + firepitO), block, renderer, 0, rotateFromMeta, isLit);
        }
        if (doDraw)
        {
            tess.draw();
            tess.startQuads();
            tess.normal(0.0F, 1.0F, 0.0F);
        }
        if(!renderer.inventoryColorEnabled) tess.color(r * colorYPos, g * colorYPos, b * colorYPos);

        // north-south log tops
        renderLogTopOrBot(x + logO1, northSouth ? (y - 1 + logY1) : (y - 1 + logY2), z, block, renderer, 1, meta == 3 || meta == 5 ? 2 : 1, false);
        renderLogTopOrBot(x + logO2, northSouth ? (y - 1 + logY1) : (y - 1 + logY2), z, block, renderer, 1, meta == 3 || meta == 5 ? 2 : 1, false);

        // east-west log tops
        renderLogTopOrBot(x, northSouth ? (y - 1 + logY2) : (y - 1 + logY1), z + logO1, block, renderer, 1, meta == 3 || meta == 5 ? 0 : 3, false);
        renderLogTopOrBot(x, northSouth ? (y - 1 + logY2) : (y - 1 + logY1), z + logO2, block, renderer, 1, meta == 3 || meta == 5 ? 0 : 3, false);

        // firepit top
        renderFirepitTopOrBot(northSouth ? (x + firepitO) : x, y - 0.9375, northSouth ? z : (z + firepitO), block, renderer, 1,
                meta == 3 ? 2 : (meta == 4 ? 0 : (meta == 5 ? 3 : 1)), isLit);
        if (doDraw)
        {
            tess.draw();
            tess.startQuads();
            tess.normal(0.0F, 0.0F, -1.0F);
        }
        if(!renderer.inventoryColorEnabled) tess.color(r * colorZ, g * colorZ, b * colorZ);

        // north log sides
        renderLogSide(x, northSouth ? (y + sideLogY) : y, z + logO1, block, renderer, 2, northSouth ? isLit : false, false);
        renderLogSide(x, northSouth ? (y + sideLogY) : y, z + logO2, block, renderer, 2, isLit, !northSouth);

        // north side faces
        if (renderer.skipFaceCulling || access == null || block.isSideVisible(access, x, y, z - 1, 2))
        {
            renderLogEnd(x + logO1, northSouth ? y : (y + sideLogY), z, block, renderer, 2);
            renderLogEnd(x + logO2, northSouth ? y : (y + sideLogY), z, block, renderer, 2);
            if (northSouth)
                renderFirepitSide(x + firepitO, y, z, block, renderer, 2, meta < 3 || meta > 5);
        }

        if (doDraw)
        {
            tess.draw();
            tess.startQuads();
            tess.normal(0.0F, 0.0F, 1.0F);
        }
        if(!renderer.inventoryColorEnabled) tess.color(r * colorZ, g * colorZ, b * colorZ);

        // south side faces
        if (renderer.skipFaceCulling || access == null || block.isSideVisible(access, x, y, z + 1, 3))
        {
            renderLogEnd(x + logO2, northSouth ? y : (y + sideLogY), z, block, renderer, 3);
            renderLogEnd(x + logO1, northSouth ? y : (y + sideLogY), z, block, renderer, 3);
            if (northSouth)
                renderFirepitSide(x + firepitO, y, z, block, renderer, 3, meta == 3);
        }

        // south log sides
        renderLogSide(x, northSouth ? (y + sideLogY) : y, z - logO1, block, renderer, 3, northSouth ? isLit : false, false);
        renderLogSide(x, northSouth ? (y + sideLogY) : y, z - logO2, block, renderer, 3, isLit, !northSouth);

        if (doDraw)
        {
            tess.draw();
            tess.startQuads();
            tess.normal(-1.0F, 0.0F, 0.0F);
        }
        if(!renderer.inventoryColorEnabled) tess.color(r * colorX, g * colorX, b * colorX);

        // west log sides
        renderLogSide(x + logO1, northSouth ? y : (y + sideLogY), z, block, renderer, 4, northSouth ? false : isLit, false);
        renderLogSide(x + logO2, northSouth ? y : (y + sideLogY), z, block, renderer, 4, isLit, northSouth);

        // west side faces
        if (renderer.skipFaceCulling || access == null || block.isSideVisible(access, x - 1, y, z, 4))
        {
            renderLogEnd(x, northSouth ? (y + sideLogY) : y, z + logO2, block, renderer, 4);
            renderLogEnd(x, northSouth ? (y + sideLogY) : y, z + logO1, block, renderer, 4);
            if (!northSouth)
                renderFirepitSide(x, y, z + firepitO, block, renderer, 4, meta == 4);
        }

        if (doDraw)
        {
            tess.draw();
            tess.startQuads();
            tess.normal(1.0F, 0.0F, 0.0F);
        }
        if(!renderer.inventoryColorEnabled) tess.color(r * colorX, g * colorX, b * colorX);

        // east log sides
        renderLogSide(x - logO1, northSouth ? y : (y + sideLogY), z, block, renderer, 5, northSouth ? false : isLit, false);
        renderLogSide(x - logO2, northSouth ? y : (y + sideLogY), z, block, renderer, 5, isLit, northSouth);

        // east side faces
        if (renderer.skipFaceCulling || access == null || block.isSideVisible(access, x + 1, y, z, 5))
        {
            renderLogEnd(x, northSouth ? (y + sideLogY) : y, z + logO1, block, renderer, 5);
            renderLogEnd(x, northSouth ? (y + sideLogY) : y, z + logO2, block, renderer, 5);
            if (!northSouth)
                renderFirepitSide(x, y, z + firepitO, block, renderer, 5, meta == 5);
        }

        if (doDraw)
        {
            tess.draw();
        }

        // fire
        if (isLit)
        {
            boolean lighting = doDraw && GL11.glGetBoolean(GL11.GL_LIGHTING);

            if (doDraw)
            {
                if (lighting)
                    GL11.glDisable(GL11.GL_LIGHTING);
                tess.startQuads();
            }

            renderFire(x, y, z, block, renderer, mixedFire);

            if (doDraw)
            {
                tess.draw();
                if (lighting)
                    GL11.glEnable(GL11.GL_LIGHTING);
            }
        }

        renderer.useAo = enableAO;
    }

    // Campfire Texture Face Renderers
    // Renders the piece at the bottom-north-west corner of the face. Adjust the given coordinates to render at a particular part of the face.

    /**
     * @param side
     *            - should be 2 (north), 3 (south), 4 (west), or 5 (east)
     */
    public static void renderLogEnd(double x, double y, double z, Block block, BlockRenderManager renderer, int side)
    {
        if (side == 2 || side == 3)
            block.setBoundingBox(0, 0.5F, 0, 0.25F, 0.75F, 1);
        else if (side == 4 || side == 5)
            block.setBoundingBox(0, 0.5F, 0, 1, 0.75F, 0.25F);

        y = y - block.minY;

        renderFace(x, y, z, block, renderer, block.getTexture(0, 0), side);
    }

    /**
     * @param side
     *            - should be 2 (north), 3 (south), 4 (west), or 5 (east)
     * @param lower
     *            - lowers the texture by one pixel
     */
    public static void renderLogSide(double x, double y, double z, Block block, BlockRenderManager renderer, int side, boolean lit, boolean lower)
    {
        float maxX = 1, maxZ = 1;

        if (side == 2)
            maxZ = 0.25F;
        else if (side == 4)
            maxX = 0.25F;

        float minY = 0.75F;
        if (lower)
            minY -= 0.0625F;
        float maxY = minY + 0.25F;

        block.setBoundingBox(0, minY, 0, maxX, maxY, maxZ);

        y = y - block.minY;

        renderFace(x, y, z, block, renderer, block.getTexture(0, lit ? -2 : 0), side);
    }

    /**
     * @param side
     *            - should be 0 (down) or 1 (up)
     * @param rotate
     *            - 0 and 3 are opposite, 1 and 2 are opposite
     */
    public static void renderLogTopOrBot(double x, double y, double z, Block block, BlockRenderManager renderer, int side, int rotate, boolean lit)
    {
        float minX = 0, maxX = 1, minZ = 0, maxZ = 1;

        if (side == 0)
        {
            renderer.flipTextureHorizontally = true;
            renderer.bottomFaceRotation = rotate;
        }
        else if (side == 1)
            renderer.topFaceRotation = rotate;

        if (rotate == 0)
        {
            minZ = 0;
            if (lit)
                minZ += 0.25F;
            maxZ = minZ + 0.25F;
        }
        else if ((rotate == 1 && side == 1) || (rotate == 2 && side == 0))
        {
            minX = 0.75F;
            if (lit)
                minX -= 0.25;
            maxX = minX + 0.25F;
        }
        else if ((rotate == 2 && side == 1) || (rotate == 1 && side == 0))
        {
            minX = 0;
            if (lit)
                minX += 0.25F;
            maxX = minX + 0.25F;
        }
        else if (rotate == 3)
        {
            minZ = 0.75F;
            if (lit)
                minZ -= 0.25F;
            maxZ = minZ + 0.25F;
        }

        block.setBoundingBox(minX, 0, minZ, maxX, 1, maxZ);

        x = x - block.minX;
        z = z - block.minZ;

        renderFace(x, y, z, block, renderer, block.getTexture(0, lit ? -2 : 0), side);

        renderer.flipTextureHorizontally = false;
        renderer.bottomFaceRotation = 0;
        renderer.topFaceRotation = 0;
    }

    /**
     * @param side
     *            - should be 2 (north), 3 (south), 4 (west), or 5 (east)
     * @param front
     *            - should be true if side is the front of the campfire, false if it's the back
     */
    public static void renderFirepitSide(double x, double y, double z, Block block, BlockRenderManager renderer, int side, boolean front)
    {
        float minX = 0, maxX = 1, minZ = 0, maxZ = 1;

        if (front)
        {
            if (side == 2 || side == 3)
                maxX = 0.375F;
            else if (side == 4 || side == 5)
                maxZ = 0.375F;
        }
        else
        {
            if (side == 2 || side == 3)
                minX = 0.625F;
            else if (side == 4 || side == 5)
                minZ = 0.625F;
        }

        block.setBoundingBox(minX, 0, minZ, maxX, 0.0625F, maxZ);

        x = x - block.minX;
        z = z - block.minZ;

        renderFace(x, y, z, block, renderer, block.getTexture(0, 0), side);
    }

    /**
     * @param side
     *            - should be 0 (down) or 1 (up)
     * @param rotate
     *            - 0 and 3 are opposite, 1 and 2 are opposite
     */
    public static void renderFirepitTopOrBot(double x, double y, double z, Block block, BlockRenderManager renderer, int side, int rotate, boolean lit)
    {
        float minX = 0, maxX = 1, minZ = 0, maxZ = 1;

        if (side == 0)
        {
            renderer.flipTextureHorizontally = true;
            renderer.bottomFaceRotation = rotate;
        }
        else if (side == 1)
            renderer.topFaceRotation = rotate;

        if (rotate == 0)
        {
            minZ = 0.5F;
            maxZ = minZ + 0.375F;
        }
        else if ((rotate == 1 && side == 1) || (rotate == 2 && side == 0))
        {
            minX = 0.125F;
            maxX = minX + 0.375F;
        }
        else if ((rotate == 2 && side == 1) || (rotate == 1 && side == 0))
        {
            minX = 0.5F;
            maxX = minX + 0.375F;
        }
        else if (rotate == 3)
        {
            minZ = 0.125F;
            maxZ = minZ + 0.375F;
        }

        block.setBoundingBox(minX, 0, minZ, maxX, 1, maxZ);

        x = x - block.minX;
        z = z - block.minZ;

        renderFace(x, y, z, block, renderer, block.getTexture(0, side == 1 && lit ? -2 : 0), side);

        renderer.flipTextureHorizontally = false;
        renderer.bottomFaceRotation = 0;
        renderer.topFaceRotation = 0;
    }

    /**
     * Draws the campfire fire. If mixedFire is true, the fire will be half regular and half soul.
     */
    public static void renderFire(double x, double y, double z, Block block, BlockRenderManager renderer, boolean mixedFire)
    {
        Tessellator tess = Tessellator.INSTANCE;
        tess.color(1.0F, 1.0F, 1.0F);
        if (mixedFire)
            drawCrossedSquaresTwoIcons(block.getAtlas(),block.getTexture(0, -3), block.getTexture(0, -3), x, y, z, 1.0F);
        else
            renderer.renderCross(block, -3, x, y, z);
    }

    public static void drawCrossedSquaresTwoIcons(Atlas atlas, int icon1Tex, int icon2Tex, double x, double y, double z, float size)
    {
        Tessellator tess = Tessellator.INSTANCE;

        Sprite icon1 = atlas.getTexture(icon1Tex).getSprite();
        Sprite icon2 = atlas.getTexture(icon2Tex).getSprite();

        double scaledSize = 0.45D * size;
        double minX = x + 0.5D - scaledSize;
        double midX = x + 0.5D;
        double maxX = x + 0.5D + scaledSize;
        double minZ = z + 0.5D - scaledSize;
        double midZ = z + 0.5D;
        double maxZ = z + 0.5D + scaledSize;

        double minU1 = (double) icon1.getMinU();
        double minV1 = (double) icon1.getMinV();
        double midU1 = (double) (icon1.getMaxU() + icon1.getMinU()) / 2;
        double maxU1 = (double) icon1.getMaxU();
        double maxV1 = (double) icon1.getMaxV();

        tess.vertex(minX, y + (double) size, minZ, minU1, minV1);
        tess.vertex(minX, y + 0.0D, minZ, minU1, maxV1);
        tess.vertex(midX, y + 0.0D, midZ, midU1, maxV1);
        tess.vertex(midX, y + (double) size, midZ, midU1, minV1);

        tess.vertex(midX, y + (double) size, midZ, midU1, minV1);
        tess.vertex(midX, y + 0.0D, midZ, midU1, maxV1);
        tess.vertex(minX, y + 0.0D, minZ, maxU1, maxV1);
        tess.vertex(minX, y + (double) size, minZ, maxU1, minV1);

        tess.vertex(minX, y + (double) size, maxZ, minU1, minV1);
        tess.vertex(minX, y + 0.0D, maxZ, minU1, maxV1);
        tess.vertex(midX, y + 0.0D, midZ, midU1, maxV1);
        tess.vertex(midX, y + (double) size, midZ, midU1, minV1);

        tess.vertex(midX, y + (double) size, midZ, midU1, minV1);
        tess.vertex(midX, y + 0.0D, midZ, midU1, maxV1);
        tess.vertex(minX, y + 0.0D, maxZ, maxU1, maxV1);
        tess.vertex(minX, y + (double) size, maxZ, maxU1, minV1);

        double minU2 = (double) icon2.getMinU();
        double minV2 = (double) icon2.getMinV();
        double midU2 = (double) (icon2.getMaxU() + icon2.getMinU()) / 2;
        double maxU2 = (double) icon2.getMaxU();
        double maxV2 = (double) icon2.getMaxV();

        tess.vertex(midX, y + (double) size, midZ, midU2, minV2);
        tess.vertex(midX, y + 0.0D, midZ, midU2, maxV2);
        tess.vertex(maxX, y + 0.0D, maxZ, maxU2, maxV2);
        tess.vertex(maxX, y + (double) size, maxZ, maxU2, minV2);

        tess.vertex(maxX, y + (double) size, maxZ, minU2, minV2);
        tess.vertex(maxX, y + 0.0D, maxZ, minU2, maxV2);
        tess.vertex(midX, y + 0.0D, midZ, midU2, maxV2);
        tess.vertex(midX, y + (double) size, midZ, midU2, minV2);

        tess.vertex(midX, y + (double) size, midZ, midU2, minV2);
        tess.vertex(midX, y + 0.0D, midZ, midU2, maxV2);
        tess.vertex(maxX, y + 0.0D, minZ, maxU2, maxV2);
        tess.vertex(maxX, y + (double) size, minZ, maxU2, minV2);

        tess.vertex(maxX, y + (double) size, minZ, minU2, minV2);
        tess.vertex(maxX, y + 0.0D, minZ, minU2, maxV2);
        tess.vertex(midX, y + 0.0D, midZ, midU2, maxV2);
        tess.vertex(midX, y + (double) size, midZ, midU2, minV2);
    }

    public static void renderFace(double x, double y, double z, Block block, BlockRenderManager renderer, int icon, int side)
    {
        if (side == 0)
            //renderFaceYNegFlippable(renderer, block, x, y, z, icon);
            renderer.renderBottomFace(block, x, y, z, icon);
        else if (side == 1)
            renderer.renderTopFace(block, x, y, z, icon);
        else if (side == 2)
            renderer.renderEastFace(block, x, y, z, icon);
        else if (side == 3)
            renderer.renderWestFace(block, x, y, z, icon);
        else if (side == 4)
            renderer.renderNorthFace(block, x, y, z, icon);
        else if (side == 5)
            renderer.renderSouthFace(block, x, y, z, icon);
    }
}
