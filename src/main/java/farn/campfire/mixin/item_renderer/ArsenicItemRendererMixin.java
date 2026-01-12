package farn.campfire.mixin.item_renderer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import farn.campfire.block_entity.CampFireBlockEntityRenderer;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicItemRenderer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArsenicItemRenderer.class)
public class ArsenicItemRendererMixin {

    //turn off item rotation when rendering campfire's items
    @WrapOperation(method="renderVanilla", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glRotatef(FFFF)V", ordinal = 1))
    public void turnOffRotation(float angle, float x, float y, float z, Operation<Void> original) {
        if(!CampFireBlockEntityRenderer.renderOnCampfire) original.call(angle,x,y,z);
    }

    @WrapOperation(method="renderVanilla", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glScalef(FFF)V", ordinal = 1))
    public void renderInFrame1(float x, float y, float z, Operation<Void> original) {
        if(CampFireBlockEntityRenderer.renderOnCampfire) {
            GL11.glScalef(x + 0.0128205F, y + 0.0128205F, z + 0.0128205F);
            GL11.glTranslatef(0.0F, -0.05F, 0.0F);
        } else {
            original.call(x,y,z);
        }
    }
}
