package io.github.kimovoid.betavisuals.mixin.general.renderdistance;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.kimovoid.betavisuals.BetaVisuals;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.GameRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow private float renderDistance;

    @Inject(
            method = "setupCamera",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.PUTFIELD,
                    target = "Lnet/minecraft/client/render/GameRenderer;renderDistance:F",
                    shift = At.Shift.AFTER
            )
    )
    public void setRenderDistance(float tickDelta, int anaglyphRenderPass, CallbackInfo ci) {
        this.renderDistance = BetaVisuals.OPTIONS.renderDistance * 16;
    }

    @WrapOperation(
            method = "renderWorld",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD,
                    target = "Lnet/minecraft/client/options/GameOptions;viewDistance:I"
            )
    )
    public int setSkyFog(GameOptions instance, Operation<Integer> original) {
        return BetaVisuals.OPTIONS.renderDistance > 4 ? 0 : 3;
    }

    /*
    @Unique public float originalViewDistance = 0F;

    @Inject(method = "setupFog", at = @At(value = "HEAD"))
    public void injectViewDistance(int mode, float tickDelta, CallbackInfo ci) {
        originalViewDistance = this.renderDistance;
        this.renderDistance = this.renderDistance * 0.2F;
    }

    @Inject(method = "setupFog", at = @At(value = "TAIL"))
    public void returnOriginalViewDistance(int mode, float tickDelta, CallbackInfo ci) {
        this.renderDistance = originalViewDistance;
    }
     */
}
