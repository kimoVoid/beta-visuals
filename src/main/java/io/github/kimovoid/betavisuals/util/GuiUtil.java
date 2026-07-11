package io.github.kimovoid.betavisuals.util;

import net.minecraft.client.render.vertex.Tesselator;
import org.lwjgl.opengl.GL11;

public class GuiUtil {

    public static void fill(int x1, int y1, int x2, int y2, int color) {
        if (x1 < x2) {
            int i6 = x1;
            x1 = x2;
            x2 = i6;
        }

        if (y1 < y2) {
            int j = y1;
            y1 = y2;
            y2 = j;
        }

        float a = (float)(color >> 24 & 255) / 255.0F;
        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;

        Tesselator tesselator = Tesselator.INSTANCE;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(r, g, b, a);
        tesselator.begin();
        tesselator.vertex(x1, y2, 0.0F);
        tesselator.vertex(x2, y2, 0.0F);
        tesselator.vertex(x2, y1, 0.0F);
        tesselator.vertex(x1, y1, 0.0F);
        tesselator.end();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }
}
