package io.github.kimovoid.betavisuals.screenshot;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Window;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HiResScreenshot {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    /**
     * Compat so mods can choose to disable screen re-init.
     * It will switch back to true after a screenshot is taken.
     */
    public static boolean resizeGui = true;

    public static String take(int scale) {
        Minecraft mc = Minecraft.INSTANCE;
        String string = DATE_FORMAT.format(new Date());
        File file;
        for(int i = 1; (file = new File(Minecraft.getWorkingDirectory(), "screenshots/" + string + (i == 1 ? "" : "_" + i) + ".png")).exists(); ++i) {
        }

        String result = "Failed to save hi-res screenshot as " + file.getName();
        int targetW = mc.width * scale;
        int targetH = mc.height * scale;

        int maxSize = GL11.glGetInteger(EXTFramebufferObject.GL_MAX_RENDERBUFFER_SIZE_EXT);
        if (targetW > maxSize || targetH > maxSize) {
            return result;
        }

        int fbo = EXTFramebufferObject.glGenFramebuffersEXT();
        int colorTex = GL11.glGenTextures();
        int depthRbo = EXTFramebufferObject.glGenRenderbuffersEXT();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTex);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB8, targetW, targetH, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, fbo);
        EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT, GL11.GL_TEXTURE_2D, colorTex, 0);
        EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, depthRbo);
        EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, GL11.GL_DEPTH_COMPONENT, targetW, targetH);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, depthRbo);

        int status = EXTFramebufferObject.glCheckFramebufferStatusEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT);
        if (status != EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT) {
            return result;
        }

        int originalW = mc.width;
        int originalH = mc.height;
        GL11.glViewport(0, 0, targetW, targetH);

        resize(mc, targetW, targetH);
        mc.gameRenderer.render(1.0F);

        ByteBuffer buf = BufferUtils.createByteBuffer(targetW * targetH * 3);
        GL11.glReadPixels(0, 0, targetW, targetH, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buf);

        resize(mc, originalW, originalH);
        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
        GL11.glViewport(0, 0, originalW, originalH);

        try {
            write(buf, targetW, targetH, file);
            result = "Saved hi-res screenshot as " + file.getName();
        } catch (Exception ignored) {
        }

        GL11.glDeleteTextures(colorTex);
        EXTFramebufferObject.glDeleteRenderbuffersEXT(depthRbo);
        EXTFramebufferObject.glDeleteFramebuffersEXT(fbo);

        resizeGui = true;
        return result;
    }

    private static void write(ByteBuffer buf, int width, int height, File outFile) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int[] row = new int[width];
        for (int y = 0; y < height; y++) {
            int srcY = height - 1 - y;
            int rowStart = srcY * width * 3;
            for (int x = 0; x < width; x++) {
                int i = rowStart + x * 3;
                int r = buf.get(i)     & 0xFF;
                int g = buf.get(i + 1) & 0xFF;
                int b = buf.get(i + 2) & 0xFF;
                row[x] = (r << 16) | (g << 8) | b;
            }
            image.setRGB(0, y, width, 1, row, 0, width);
        }

        File parent = outFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        if (!ImageIO.write(image, "png", outFile)) {
            throw new IOException("No PNG writer available");
        }
    }

    private static void resize(Minecraft mc, int width, int height) {
        mc.width = width;
        mc.height = height;
        if (mc.screen != null && resizeGui) {
            Window window = new Window(mc.options, width, height);
            mc.screen.init(mc, window.getWidth(), window.getHeight());
        }
    }
}
