package farn.campfire.block_entity;

public class CampFireRenderHelper {

    private static final double BASE_X_OFFSET = 0.9375;
    private static final double BASE_Y_OFFSET = 0.45;
    private static final double BASE_Z_OFFSET = 0.9375;
    private static final double ACROSS = 0.875;
    private static final double EDGE = 0.125;
    private static final double OFFSET_FIX_X = EDGE * 0.625;
    private static final double OFFSET_FIX_Z = EDGE * 0.375;
    private static final double[][] RENDER_POSITION_ITEM = new double[][] {
            { BASE_X_OFFSET, BASE_Y_OFFSET, BASE_Z_OFFSET + EDGE - ACROSS },
            { BASE_X_OFFSET - OFFSET_FIX_X, BASE_Y_OFFSET, BASE_Z_OFFSET - OFFSET_FIX_Z },
            { BASE_X_OFFSET - ACROSS, BASE_Y_OFFSET, BASE_Z_OFFSET - EDGE },
            { BASE_X_OFFSET + OFFSET_FIX_X - ACROSS, BASE_Y_OFFSET, BASE_Z_OFFSET + OFFSET_FIX_Z - ACROSS } };
    private static final double[][] RENDER_POSITION_BLOCK_ITEM = new double[][] {
            { BASE_X_OFFSET, BASE_Y_OFFSET, BASE_Z_OFFSET + EDGE - ACROSS },
            { BASE_X_OFFSET - EDGE, BASE_Y_OFFSET, BASE_Z_OFFSET - (EDGE * 2) },
            { BASE_X_OFFSET - ACROSS, BASE_Y_OFFSET, BASE_Z_OFFSET - EDGE },
            { BASE_X_OFFSET + EDGE - ACROSS, BASE_Y_OFFSET, BASE_Z_OFFSET - ACROSS + (EDGE * 2)} };
    public static final int[] MAPPING = new int[]{3, 0, 1, 2};
    public static double[] getItemPos(int renderslot) {
        return RENDER_POSITION_ITEM[Math.abs(renderslot) % 4];
    }

    public static double[] getBlockPos(int renderslot) {
        return RENDER_POSITION_BLOCK_ITEM[Math.abs(renderslot) % 4];
    }
}
