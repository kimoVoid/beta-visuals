package io.github.kimovoid.betavisuals;

import java.io.*;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.GameOptions;
import net.minecraft.locale.Language;
import net.minecraft.world.World;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

@Environment(EnvType.CLIENT)
public class BVOptions {

    private final Minecraft minecraft;
    private final File file;

    // general
    public int renderDistance = 12;
    public float brightness = 0.0F;
    public int fpsLimit = 260;
    public boolean vsync = false;
    public boolean fullscreen = false;

    // quality
    public int mipmapLevel = 0;
    public int mipmapType = 0;
    // 0 means default, 1 means always fancy, 2 means always fast, 3 means off
    public int clouds = 0;
    public int leaves = 0;
    public int grass = 0;
    public int rainAndSnow = 0;

    // details
    public float cloudHeight = 0.0F;
    public boolean betterGrass = false;
    public int vignette = 0;
    public boolean entityShadows = true;

    // render
    public boolean whiteLineFix = true;
    public float fogStart = 0.2F;

    // other
    public boolean showFps = false;
    public boolean asyncScreenshots = true;
    public float chatTextOpacity = 1.0F;
    public float chatBgOpacity = 0.5F;
    public float chatScale = 1.0F;

    public BVOptions(Minecraft mc) {
        this.minecraft = mc;
        this.file = new File(Minecraft.getWorkingDirectory(), "bvoptions.txt");
        this.load();
    }

    private GameOptions getOptions() {
        return this.minecraft.options;
    }
    
    public String getName(BVOptions.Option option) {
        Language language = Language.getInstance();
        if (option.getOriginal() != null) {
            return language.translate(option.getOriginal().getName());
        }
        return language.translate(option.getName());
    }

    public String getValueString(BVOptions.Option option) {
        if (option.getOriginal() != null) {
            return this.getOptions().getAsString(option.getOriginal()).split(": ")[1];
        }

        Language language = Language.getInstance();
        if (option.isBoolean()) {
            boolean b = this.getBoolean(option);
            return b ? language.translate("options.on") : language.translate("options.off");
        }

        if (option.isDetail()) {
            int detail = this.getDetail(option);
            return switch (option) {
                case CLOUDS, RAIN_SNOW ->
                        detail == 0 ? language.translate("options.default") : detail == 1 ? language.translate("options.graphics.fancy") : detail == 2 ? language.translate("options.graphics.fast") : language.translate("options.off");
                case LEAVES, GRASS ->
                        detail == 0 ? language.translate("options.default") : detail == 1 ? language.translate("options.graphics.fancy") : language.translate("options.graphics.fast");
                default ->
                        detail == 0 ? language.translate("options.default") : detail == 1 ? language.translate("options.on") : language.translate("options.off");
            };
        }

        return switch (option) {
            case RENDER_DISTANCE -> this.renderDistance + " chunks";
            case BRIGHTNESS -> this.brightness == 0.0F ? "Moody" : this.brightness == 1.0F ? "Bright" : (int) (this.brightness * 100) + "%";
            case FRAMERATE_LIMIT -> this.fpsLimit >= option.getMax() ? "Unlimited" : this.fpsLimit + " fps";
            case CLOUD_HEIGHT -> "+" + (int) (this.cloudHeight * 100) + "%";
            case MIPMAP -> this.mipmapLevel == 0 ? language.translate("options.off") : "" + this.mipmapLevel;
            case MIPMAP_TYPE -> this.mipmapType == 0 ? "Nearest" : this.mipmapType == 1 ? "Crispy" : "Linear";
            case FOG -> "" + this.fogStart;
            case CHAT_TEXT_OPACITY -> (int) (this.chatTextOpacity * 100) + "%";
            case CHAT_BACKGROUND_OPACITY -> (int) (this.chatBgOpacity * 100) + "%";
            case CHAT_SCALE -> (int) (this.chatScale * 100) + "%";
            default -> "";
        };
    }
    
    public float getFloat(BVOptions.Option option) {
        return switch (option) {
            case RENDER_DISTANCE -> this.renderDistance;
            case BRIGHTNESS -> this.brightness;
            case FRAMERATE_LIMIT -> this.fpsLimit;
            case MIPMAP -> this.mipmapLevel;
            case MIPMAP_TYPE -> this.mipmapType;
            case FOG -> this.fogStart;
            case CLOUD_HEIGHT -> this.cloudHeight;
            case CHAT_TEXT_OPACITY -> this.chatTextOpacity;
            case CHAT_BACKGROUND_OPACITY -> this.chatBgOpacity;
            case CHAT_SCALE -> this.chatScale;
            default -> 0;
        };
    }

    public boolean getBoolean(BVOptions.Option option) {
        if (option.getOriginal() != null) {
            return this.getOptions().getBoolean(option.getOriginal());
        }

        return switch (option) {
            case VSYNC -> this.vsync;
            case FULLSCREEN -> this.fullscreen;
            case BETTER_GRASS -> this.betterGrass;
            case WHITE_LINE_FIX -> this.whiteLineFix;
            case ENTITY_SHADOWS -> this.entityShadows;
            case SHOW_FPS -> this.showFps;
            case ASYNC_SCREENSHOTS -> this.asyncScreenshots;
            default -> false;
        };
    }

    public int getDetail(BVOptions.Option option) {
        return switch (option) {
            case VIGNETTE -> this.vignette;
            case CLOUDS -> this.clouds;
            case LEAVES -> this.leaves;
            case GRASS -> this.grass;
            case RAIN_SNOW -> this.rainAndSnow;
            default -> 0;
        };
    }

    public void set(BVOptions.Option option, int i) {
        if (option.getOriginal() != null) {
            this.getOptions().set(option.getOriginal(), i);
        }

        switch (option) {
            case VSYNC -> {
                this.vsync = !this.vsync;
                Display.setVSyncEnabled(this.vsync);
            }
            case FULLSCREEN -> {
                this.fullscreen = !this.fullscreen;
                try {
                    Display.setFullscreen(this.fullscreen);
                } catch (LWJGLException ignored) {
                }
            }
            case BETTER_GRASS -> {
                this.betterGrass = !this.betterGrass;
                if (this.minecraft.world != null) {
                    this.minecraft.worldRenderer.reload();
                }
            }
            case VIGNETTE -> this.vignette = (this.vignette + i) % 3;
            case ENTITY_SHADOWS -> this.entityShadows = !this.entityShadows;
            case CLOUDS -> this.clouds = (this.clouds + i) % 4;
            case LEAVES -> {
                this.leaves = (this.leaves + i) % 3;
                if (this.minecraft.world != null) {
                    this.minecraft.worldRenderer.reload();
                }
            }
            case GRASS -> {
                this.grass = (this.grass + i) % 3;
                if (this.minecraft.world != null) {
                    this.minecraft.worldRenderer.reload();
                }
            }
            case RAIN_SNOW -> this.rainAndSnow = (this.rainAndSnow + i) % 4;
            case WHITE_LINE_FIX -> this.whiteLineFix = !this.whiteLineFix;
            case SHOW_FPS -> this.showFps = !this.showFps;
            case ASYNC_SCREENSHOTS -> this.asyncScreenshots = !this.asyncScreenshots;
        }
    }

    public void set(BVOptions.Option option, float value, boolean apply) {
        switch (option) {
            case RENDER_DISTANCE -> this.renderDistance = (int) value;
            case BRIGHTNESS -> {
                this.brightness = value;
                if (apply && !Mouse.isButtonDown(0) && this.minecraft.world != null) this.updateLighting(this.minecraft.world);
            }
            case FRAMERATE_LIMIT -> this.fpsLimit = (int) value;
            case MIPMAP -> {
                this.mipmapLevel = (int) value;
                if (apply && !Mouse.isButtonDown(0)) this.minecraft.textureManager.reload();
            }
            case MIPMAP_TYPE -> {
                this.mipmapType = (int) value;
                if (apply && !Mouse.isButtonDown(0)) this.minecraft.textureManager.reload();
            }
            case FOG -> this.fogStart = value;
            case CLOUD_HEIGHT -> this.cloudHeight = value;
            case CHAT_TEXT_OPACITY -> this.chatTextOpacity = value;
            case CHAT_BACKGROUND_OPACITY -> this.chatBgOpacity = value;
            case CHAT_SCALE -> this.chatScale = value;
        }
    }

    public void updateLighting(World world) {
        float[] lightLevels = world.dimension.brightnessTable;
        float minimumLevel = 0.05F;

        if (world.dimension.noSky) { // if(isNether)
            minimumLevel = 0.1F + this.brightness * 0.15F;
        }

        float k = 3.0f * (1.0F - this.brightness);
        for (int level = 0; level <= 15; ++level) {
            float var3 = 1.0F - (float) level / 15.0f;
            lightLevels[level] = (1.0F - var3) / (var3 * k + 1.0F) * (1.0F - minimumLevel) + minimumLevel;
        }

        Minecraft.INSTANCE.worldRenderer.reload();
    }

    public int getRenderDistance() {
        return this.renderDistance <= 12 ? this.renderDistance + 4 : Math.max(this.renderDistance, 16);
    }

    public void load() {
        try {
            if (!this.file.exists()) {
                return;
            }

            BufferedReader bufferedReader = new BufferedReader(new FileReader(this.file));
            String string;
            while ((string = bufferedReader.readLine()) != null) {
                try {
                    String[] strings = string.split(":");
                    switch (strings[0]) {
                        case "renderDistance" -> this.renderDistance = Integer.parseInt(strings[1]);
                        case "brightness" -> this.brightness = Float.parseFloat(strings[1]);
                        case "fpsLimit" -> this.fpsLimit = Integer.parseInt(strings[1]);
                        case "vsync" -> this.vsync = strings[1].equalsIgnoreCase("true");
                        case "fullscreen" -> this.fullscreen = strings[1].equalsIgnoreCase("true");
                        case "cloudHeight" -> this.cloudHeight = Float.parseFloat(strings[1]);
                        case "mipmapLevel" -> this.mipmapLevel = Integer.parseInt(strings[1]);
                        case "mipmapType" -> this.mipmapType = Integer.parseInt(strings[1]);
                        case "betterGrass" -> this.betterGrass = strings[1].equalsIgnoreCase("true");
                        case "fogStart" -> this.fogStart = Float.parseFloat(strings[1]);
                        case "vignette" -> this.vignette = Integer.parseInt(strings[1]);
                        case "entityShadows" -> this.entityShadows = strings[1].equalsIgnoreCase("true");
                        case "clouds" -> this.clouds = Integer.parseInt(strings[1]);
                        case "leaves" -> this.leaves = Integer.parseInt(strings[1]);
                        case "grass" -> this.grass = Integer.parseInt(strings[1]);
                        case "rainAndSnow" -> this.rainAndSnow = Integer.parseInt(strings[1]);
                        case "showFps" -> this.showFps = strings[1].equalsIgnoreCase("true");
                        case "asyncScreenshots" -> this.asyncScreenshots = strings[1].equalsIgnoreCase("true");
                        case "chatTextOpacity" -> this.chatTextOpacity = Float.parseFloat(strings[1]);
                        case "chatBgOpacity" -> this.chatBgOpacity = Float.parseFloat(strings[1]);
                        case "chatScale" -> this.chatScale = Float.parseFloat(strings[1]);
                    }
                } catch (Exception exception6) {
                    System.out.println("Skipping bad option: " + string);
                }
            }

            bufferedReader.close();
        } catch (Exception e) {
            System.out.println("Failed to load BV options");
        }
    }

    public void save() {
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(this.file));
            printWriter.println("renderDistance:" + this.renderDistance);
            printWriter.println("brightness:" + this.brightness);
            printWriter.println("fpsLimit:" + this.fpsLimit);
            printWriter.println("vsync:" + this.vsync);
            printWriter.println("fullscreen:" + this.fullscreen);
            printWriter.println("cloudHeight:" + this.cloudHeight);
            printWriter.println("mipmapLevel:" + this.mipmapLevel);
            printWriter.println("mipmapType:" + this.mipmapType);
            printWriter.println("betterGrass:" + this.betterGrass);
            printWriter.println("fogStart:" + this.fogStart);
            printWriter.println("vignette:" + this.vignette);
            printWriter.println("entityShadows:" + this.entityShadows);
            printWriter.println("clouds:" + this.clouds);
            printWriter.println("leaves:" + this.leaves);
            printWriter.println("grass:" + this.grass);
            printWriter.println("rainAndSnow:" + this.rainAndSnow);
            printWriter.println("whiteLineFix:" + this.whiteLineFix);
            printWriter.println("showFps:" + this.showFps);
            printWriter.println("asyncScreenshots:" + this.asyncScreenshots);
            printWriter.println("chatTextOpacity:" + this.chatTextOpacity);
            printWriter.println("chatBgOpacity:" + this.chatBgOpacity);
            printWriter.println("chatScale:" + this.chatScale);
            printWriter.close();
        } catch (Exception e) {
            System.out.println("Failed to save BV options");
        }
    }

    public enum Category {
        GENERAL("options.category.general",
                Option.RENDER_DISTANCE,
                Option.BRIGHTNESS,
                Option.GUI_SCALE,
                Option.FRAMERATE_LIMIT,
                Option.VSYNC,
                Option.FULLSCREEN,
                Option.VIEW_BOBBING
        ),
        QUALITY("options.category.quality",
                Option.GRAPHICS,
                Option.AMBIENT_OCCLUSION,
                Option.MIPMAP,
                Option.MIPMAP_TYPE,
                Option.CLOUDS,
                Option.LEAVES,
                Option.GRASS,
                Option.RAIN_SNOW
        ),
        DETAILS("options.category.details",
                Option.CLOUD_HEIGHT,
                Option.BETTER_GRASS,
                Option.VIGNETTE,
                Option.ENTITY_SHADOWS
        ),
        RENDER("options.category.render",
                Option.ADVANCED_OPENGL,
                Option.ANAGLYPH,
                Option.FOG
        ),
        OTHER("options.category.other",
                Option.SHOW_FPS,
                Option.ASYNC_SCREENSHOTS,
                Option.CHAT_TEXT_OPACITY,
                Option.CHAT_BACKGROUND_OPACITY,
                Option.CHAT_SCALE
        );

        private final String name;
        private final Option[] options;

        Category(String name, Option... options) {
            this.name = name;
            this.options = options;
        }

        public String getName() {
            return this.name;
        }

        public Option[] getOptions() {
            return this.options;
        }
    }

    public enum Option {
        RENDER_DISTANCE("options.renderDistance", true, false, 2.0F, 32.0F, 1.0F),
        BRIGHTNESS("options.brightness", true, false, 0.0F, 1.0F, 0.0F),
        FRAMERATE_LIMIT("options.fpsLimit", true, false, 10.0F, 260.0F, 10.0F),
        VIEW_BOBBING("options.viewBobbing", false, true, GameOptions.Option.VIEW_BOBBING),
        ANAGLYPH("options.anaglyph", false, true, GameOptions.Option.ANAGLYPH),
        ADVANCED_OPENGL("options.advancedOpengl", false, true, GameOptions.Option.ADVANCED_OPENGL),
        GRAPHICS("options.graphics", false, false, GameOptions.Option.GRAPHICS),
        AMBIENT_OCCLUSION("options.ao", false, true, GameOptions.Option.AMBIENT_OCCLUSION),
        GUI_SCALE("options.guiScale", false, false, GameOptions.Option.GUI_SCALE),
        VSYNC("options.vsync", false, true, false),
        FULLSCREEN("options.fullscreen", false, true, false),
        CLOUD_HEIGHT("options.cloudHeight", true, false, 0.0F, 1.0F, 0.0F),
        MIPMAP("options.mipmap", true, false, 0.0F, 4.0F, 1.0F),
        MIPMAP_TYPE("options.mipmapType", true, false, 0.0F, 2.0F, 1.0F),
        BETTER_GRASS("options.betterGrass", false, true, false),
        FOG("options.fogStart", true, false, 0.2F, 0.8F, 0.2F),
        VIGNETTE("options.vignette", false, false, true),
        ENTITY_SHADOWS("options.entityShadows", false, true, false),
        CLOUDS("options.clouds", false, false, true, "options.section.quality"),
        LEAVES("options.leaves", false, false, true),
        GRASS("options.grass", false, false, true),
        RAIN_SNOW("options.rainAndSnow", false, false, true),
        SHOW_FPS("options.showFps", false, true, false),
        ASYNC_SCREENSHOTS("options.asyncScreenshots", false, true, false),
        WHITE_LINE_FIX("options.whiteLineFix", false, true, false),
        CHAT_TEXT_OPACITY("options.chatTextOpacity", true, false, 0.0F, 1.0F, 0.0F),
        CHAT_BACKGROUND_OPACITY("options.chatBgOpacity", true, false, 0.0F, 1.0F, 0.0F),
        CHAT_SCALE("options.chatScale", true, false, 0.0F, 1.0F, 0.0F);

        private final boolean isFloat;
        private final boolean isBoolean;
        private boolean detail;
        private final String name;
        private float min;
        private float max;
        private float step;
        private String section = "";
        private GameOptions.Option original;

        Option(String name, boolean isFloat, boolean isBoolean, boolean detail) {
            this.name = name;
            this.isFloat = isFloat;
            this.isBoolean = isBoolean;
            this.detail = detail;
        }

        Option(String name, boolean isFloat, boolean isBoolean, boolean detail, String section) {
            this(name, isFloat, isBoolean, detail);
            this.section = section;
        }

        Option(String name, boolean isFloat, boolean isBoolean, GameOptions.Option original) {
            this.name = name;
            this.isFloat = isFloat;
            this.isBoolean = isBoolean;
            this.original = original;
        }

        Option(String name, boolean isFloat, boolean isBoolean, float min, float max, float step) {
            this.name = name;
            this.isFloat = isFloat;
            this.isBoolean = isBoolean;
            this.min = min;
            this.max = max;
            this.step = step;
        }

        public String getSection() {
            return this.section;
        }

        public boolean isFloat() {
            return this.isFloat;
        }

        public boolean isBoolean() {
            return this.isBoolean;
        }

        public boolean isDetail() {
            return this.detail;
        }

        public float getMin() {
            return this.min;
        }

        public float getMax() {
            return this.max;
        }

        public float getStep() {
            return this.step;
        }

        public int getId() {
            return this.ordinal();
        }

        public String getName() {
            return this.name;
        }

        public GameOptions.Option getOriginal() {
            return this.original;
        }

        public float normalize(float value) {
            return this.clamp((this.clampAndRoundToStepMultiple(value) - this.min) / (this.max - this.min), 0.0F, 1.0F);
        }

        public float denormalize(float value) {
            return this.clampAndRoundToStepMultiple(this.min + (this.max - this.min) * this.clamp(value, 0.0F, 1.0F));
        }

        public float clampAndRoundToStepMultiple(float value) {
            value = this.roundToStepMultiple(value);
            return this.clamp(value, this.min, this.max);
        }

        private float roundToStepMultiple(float value) {
            if (this.step > 0.0F) {
                value = this.step * Math.round(value / this.step);
            }

            return value;
        }

        private float clamp(float x, float min, float max) {
            if (x < min) {
                return min;
            } else {
                return Math.min(x, max);
            }
        }
    }
}
