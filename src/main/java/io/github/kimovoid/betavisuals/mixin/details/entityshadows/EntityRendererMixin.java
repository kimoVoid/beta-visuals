package io.github.kimovoid.betavisuals.mixin.details.entityshadows;

import io.github.kimovoid.betavisuals.BetaVisuals;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @Inject(method = "renderShadow", at = @At("HEAD"), cancellable = true)
    private void cancelShadow(Entity entity, double dx, double dy, double dz, float yaw, float tickDelta, CallbackInfo ci) {
        if (!BetaVisuals.OPTIONS.entityShadows) {
            ci.cancel();
        }
    }
}
