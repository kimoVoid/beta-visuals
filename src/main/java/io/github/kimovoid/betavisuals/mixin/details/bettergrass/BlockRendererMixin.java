package io.github.kimovoid.betavisuals.mixin.details.bettergrass;

import io.github.kimovoid.betavisuals.BetaVisuals;
import net.minecraft.block.Block;
import net.minecraft.client.render.block.BlockRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockRenderer.class)
public class BlockRendererMixin {

    @Shadow public static boolean fancyGraphics;

    /* With AO */
    @Redirect(method = "tesselateWithMaxAmbientOcclusion", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/block/BlockRenderer;fancyGraphics:Z", opcode = Opcodes.GETSTATIC))
    private boolean alwaysRenderFancyGrassAO() {
        return fancyGraphics || BetaVisuals.OPTIONS.betterGrass;
    }

    @Redirect(method = "tesselateWithMaxAmbientOcclusion", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/BlockRenderer;tesselateNorthFace(Lnet/minecraft/block/Block;DDDI)V", ordinal = 1))
    private void betterGrassNorthAO(BlockRenderer instance, Block block, double x, double y, double z, int sprite) {
        instance.tesselateNorthFace(block, x, y, z, BetaVisuals.OPTIONS.betterGrass ? 0 : sprite);
    }

    @Redirect(method = "tesselateWithMaxAmbientOcclusion", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/BlockRenderer;tesselateSouthFace(Lnet/minecraft/block/Block;DDDI)V", ordinal = 1))
    private void betterGrassSouthAO(BlockRenderer instance, Block block, double x, double y, double z, int sprite) {
        instance.tesselateSouthFace(block, x, y, z, BetaVisuals.OPTIONS.betterGrass ? 0 : sprite);
    }

    @Redirect(method = "tesselateWithMaxAmbientOcclusion", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/BlockRenderer;tesselateEastFace(Lnet/minecraft/block/Block;DDDI)V", ordinal = 1))
    private void betterGrassEastAO(BlockRenderer instance, Block block, double x, double y, double z, int sprite) {
        instance.tesselateEastFace(block, x, y, z, BetaVisuals.OPTIONS.betterGrass ? 0 : sprite);
    }

    @Redirect(method = "tesselateWithMaxAmbientOcclusion", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/BlockRenderer;tesselateWestFace(Lnet/minecraft/block/Block;DDDI)V", ordinal = 1))
    private void betterGrassWestAO(BlockRenderer instance, Block block, double x, double y, double z, int sprite) {
        instance.tesselateWestFace(block, x, y, z, BetaVisuals.OPTIONS.betterGrass ? 0 : sprite);
    }

    /* Without AO */
    @Redirect(method = "tesselateWithoutAmbientOcclusion", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/block/BlockRenderer;fancyGraphics:Z", opcode = Opcodes.GETSTATIC))
    private boolean alwaysRenderFancyGrass() {
        return fancyGraphics || BetaVisuals.OPTIONS.betterGrass;
    }

    @Redirect(method = "tesselateWithoutAmbientOcclusion", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/BlockRenderer;tesselateNorthFace(Lnet/minecraft/block/Block;DDDI)V", ordinal = 1))
    private void betterGrassNorth(BlockRenderer instance, Block block, double x, double y, double z, int sprite) {
        instance.tesselateNorthFace(block, x, y, z, BetaVisuals.OPTIONS.betterGrass ? 0 : sprite);
    }

    @Redirect(method = "tesselateWithoutAmbientOcclusion", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/BlockRenderer;tesselateSouthFace(Lnet/minecraft/block/Block;DDDI)V", ordinal = 1))
    private void betterGrassSouth(BlockRenderer instance, Block block, double x, double y, double z, int sprite) {
        instance.tesselateSouthFace(block, x, y, z, BetaVisuals.OPTIONS.betterGrass ? 0 : sprite);
    }

    @Redirect(method = "tesselateWithoutAmbientOcclusion", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/BlockRenderer;tesselateEastFace(Lnet/minecraft/block/Block;DDDI)V", ordinal = 1))
    private void betterGrassEast(BlockRenderer instance, Block block, double x, double y, double z, int sprite) {
        instance.tesselateEastFace(block, x, y, z, BetaVisuals.OPTIONS.betterGrass ? 0 : sprite);
    }

    @Redirect(method = "tesselateWithoutAmbientOcclusion", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/BlockRenderer;tesselateWestFace(Lnet/minecraft/block/Block;DDDI)V", ordinal = 1))
    private void betterGrassWest(BlockRenderer instance, Block block, double x, double y, double z, int sprite) {
        instance.tesselateWestFace(block, x, y, z, BetaVisuals.OPTIONS.betterGrass ? 0 : sprite);
    }
}
