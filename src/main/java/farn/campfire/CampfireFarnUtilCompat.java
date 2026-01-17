package farn.campfire;

import farn.farn_util.FarnUtil;
import net.minecraft.client.particle.Particle;

public class CampfireFarnUtilCompat {

    public static void setStaticItemRenderer(boolean enabled) {
        FarnUtil.setStaticItemRender(enabled);
    }

    public static void addParticle(Particle particle) {
        FarnUtil.addParticle(particle);
    }
}
