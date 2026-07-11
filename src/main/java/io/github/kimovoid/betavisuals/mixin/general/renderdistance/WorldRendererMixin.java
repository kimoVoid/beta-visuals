package io.github.kimovoid.betavisuals.mixin.general.renderdistance;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.kimovoid.betavisuals.BetaVisuals;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.world.WorldRenderer;
import org.lwjgl.input.Mouse;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Shadow private int lastViewDistance;
    @Shadow private int chunkCountX;
    @Shadow private int chunkCountZ;

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD,
                    target = "Lnet/minecraft/client/options/GameOptions;viewDistance:I"
            )
    )
    public int fixRebuildCheck(GameOptions instance, Operation<Integer> original) {
        // This prevents the the rebuilding when the user is still dragging the slider
        if (!Mouse.isButtonDown(0)) {
            return BetaVisuals.OPTIONS.renderDistance;
        }
        return this.lastViewDistance;
    }

    @Inject(
            method = "reload",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.PUTFIELD,
                    target = "Lnet/minecraft/client/render/world/WorldRenderer;lastViewDistance:I",
                    shift = At.Shift.AFTER,
                    ordinal = 0
            )
    )
    public void injectNewRenderDistance(CallbackInfo ci) {
        this.lastViewDistance = BetaVisuals.OPTIONS.renderDistance;
    }

    @Inject(
            method = "reload",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.PUTFIELD,
                    target = "Lnet/minecraft/client/render/world/WorldRenderer;chunkCountX:I",
                    shift = At.Shift.AFTER,
                    ordinal = 0
            )
    )
    public void overrideHorizontalRenderDistnace(CallbackInfo ci) {
        this.chunkCountX = BetaVisuals.OPTIONS.renderDistance * 2 + 1;
    }

    @Inject(
            method = "reload",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.PUTFIELD,
                    target = "Lnet/minecraft/client/render/world/WorldRenderer;chunkCountZ:I",
                    shift = At.Shift.AFTER,
                    ordinal = 0
            )
    )
    public void overrideDeepRenderDistnace(CallbackInfo ci) {
        this.chunkCountZ = BetaVisuals.OPTIONS.renderDistance * 2 + 1;
    }
}
