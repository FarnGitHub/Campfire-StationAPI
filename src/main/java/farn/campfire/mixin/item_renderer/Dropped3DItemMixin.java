package farn.campfire.mixin.item_renderer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import farn.campfire.block_entity.CampFireBlockEntityRenderer;
import farn.threeD_item.Item3D;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Item3D.class)
public class Dropped3DItemMixin {

    //Same as ArsenicItemRendererMixin but 3D, apply when there is 3D Dropped item mod
    @WrapOperation(method="render3DVanilla", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glRotatef(FFFF)V", ordinal = 1))
    private static void disableRotation(float angle, float x, float y, float z, Operation<Void> original) {
        if(CampFireBlockEntityRenderer.renderOnCampfire) original.call(180.0F, 0.0F,1.0F,0.0F);
        else original.call(angle,x,y,z);
    }

    @WrapOperation(method="render3DVanilla", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glTranslatef(FFF)V", ordinal = 0))
    private static void preventItemBopping(float x, float y, float z, Operation<Void> original, @Local(ordinal = 4, argsOnly = true) LocalFloatRef offset) {
        original.call(x, CampFireBlockEntityRenderer.renderOnCampfire ? y - offset.get() : y, z);
    }

    @WrapOperation(method="render3DVanilla", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glScalef(FFF)V", ordinal = 1))
    private static void renderInFrame1(float x, float y, float z, Operation<Void> original) {
        if(CampFireBlockEntityRenderer.renderOnCampfire) {
            original.call(x + 0.0128205F, y + 0.0128205F, z + 0.0128205F);
        } else {
            original.call(x,y,z);
        }
    }
}
