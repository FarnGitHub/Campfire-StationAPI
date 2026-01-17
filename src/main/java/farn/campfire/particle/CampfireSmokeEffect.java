package farn.campfire.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Tessellator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class CampfireSmokeEffect extends Particle {
    public static final int TEXTURE_COUNT = 12;
    public static final String[] TEXTURES = new String[TEXTURE_COUNT];
    static
    {
        for (int i = 0; i < TEXTURE_COUNT; i++)
            TEXTURES[i] = "/assets/campfire/particle/big_smoke_" + i + ".png";
    }

    protected final int texIndex;

    public CampfireSmokeEffect(World world, int x, int y, int z)
    {
        super(world, x, y, z, 0.0F, 0.0F, 0.0F);
        this.setPosition(x + 0.5 + (random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)),
                y + random.nextDouble() + random.nextDouble(),
                z + 0.5 + (random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)));
        this.scale = 6.0F * (random.nextFloat() * 0.5F + 0.5F);
        this.setBoundingBoxSpacing(0.25F, 0.25F);
        this.gravityStrength = 0.000003F;
        this.velocityY = 0.075 + this.random.nextFloat() / 500.0F;
        this.noClip = false;

        this.maxParticleAge = random.nextInt(50) + 80;

        this.texIndex = random.nextInt(TEXTURE_COUNT);
    }

    @Override
    public void render(Tessellator tess, float partialTicks, float rotX, float rotXZ, float rotZ, float rotYZ, float rotXY)
    {
        LivingEntity view = Minecraft.INSTANCE.camera;
        double interpX = view.lastTickX + (view.x - view.lastTickX) * partialTicks;
        double interpY = view.lastTickY + (view.y - view.lastTickY) * partialTicks;
        double interpZ = view.lastTickZ + (view.z - view.lastTickZ) * partialTicks;

        float partialPosX = (float) (prevX + (x - prevX) * partialTicks - interpX);
        float partialPosY = (float) (prevY + (y - prevY) * partialTicks - interpY);
        float partialPosZ = (float) (prevZ + (z - prevZ) * partialTicks - interpZ);

        Minecraft.INSTANCE.textureManager.bindTexture(Minecraft.INSTANCE.textureManager.getTextureId(TEXTURES[this.texIndex % TEXTURES.length]));
        float scalePar = 0.1F * scale;
        float light = this.getBrightnessAtEyes(1.0F);
        tess.color(red * light, green * light, blue * light, 0.25F);
        tess.vertex(partialPosX - rotX * scalePar - rotYZ * scalePar, partialPosY - rotXZ * scalePar, partialPosZ - rotZ * scalePar - rotXY * scalePar, 1, 1);
        tess.vertex(partialPosX - rotX * scalePar + rotYZ * scalePar, partialPosY + rotXZ * scalePar, partialPosZ - rotZ * scalePar + rotXY * scalePar, 1, 0);
        tess.vertex(partialPosX + rotX * scalePar + rotYZ * scalePar, partialPosY + rotXZ * scalePar, partialPosZ + rotZ * scalePar + rotXY * scalePar, 0, 0);
        tess.vertex(partialPosX + rotX * scalePar - rotYZ * scalePar, partialPosY - rotXZ * scalePar, partialPosZ + rotZ * scalePar - rotXY * scalePar, 0, 1);
    }

    @Override
    public int getGroup()
    {
        return 3;
    }

    @Override
    public void tick()
    {
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;

        if (this.particleAge++ >= this.maxParticleAge) {
            this.markDead();
        } else {
            this.velocityY -= this.gravityStrength;
            this.move(0.0F, this.velocityY, 0.0F);
        }
    }
}
