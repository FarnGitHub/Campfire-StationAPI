package farn.campfire.mixin.item_renderer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import farn.campfire.block_entity.CampFireBlockEntityRenderer;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArsenicItemRenderer.class)
public class ArsenicItemRendererMixin {

    //turn off item rotation when rendering campfire's items
    @WrapOperation(method="renderVanilla", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glRotatef(FFFF)V", ordinal = 1))
    public void turnOffRotation(float angle, float x, float y, float z, Operation<Void> original) {
        if(!CampFireBlockEntityRenderer.stopRotate) original.call(angle,x,y,z);
    }
}
