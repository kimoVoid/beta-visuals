package io.github.kimovoid.betavisuals.gui;

import io.github.kimovoid.betavisuals.BVOptions;
import io.github.kimovoid.betavisuals.gui.widget.OptionListWidget;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.Window;
import net.minecraft.locale.Language;
import org.lwjgl.input.Mouse;

public class VideoSettingsScreen extends Screen {

    private final Screen parent;
    protected String title = "Video Settings";
    private final GameOptions options;
    private OptionListWidget listWidget;
    private BVOptions.Category category;

    private static final BVOptions.Category[] CATEGORIES = new BVOptions.Category[] {
            BVOptions.Category.GENERAL,
            BVOptions.Category.QUALITY,
            BVOptions.Category.DETAILS,
            BVOptions.Category.RENDER,
            BVOptions.Category.OTHER
    };

    public VideoSettingsScreen(Screen parent, GameOptions options) {
        this.parent = parent;
        this.options = options;
        this.category = CATEGORIES[0];
    }

    @Override
    public void init() {
        this.title = Language.getInstance().translate("options.videoTitle");
        this.buttons.clear();
        this.buttons.add(new ButtonWidget(200, this.width / 2 - 100, this.height - 27, Language.getInstance().translate("gui.done")));

        for (int i = 0; i < CATEGORIES.length; i++) {
            BVOptions.Category category = CATEGORIES[i];
            ButtonWidget btn = new ButtonWidget(201 + i, 5, 32 + (i * 25), 115, 20, Language.getInstance().translate(category.getName()));
            btn.active = this.category != category;
            this.buttons.add(btn);
        }

        this.listWidget = new OptionListWidget(this.minecraft, this.width, this.height, 32, this.height - 32, 25, this.category);
    }

    @Override
    public void tick() {
        this.listWidget.tick();
    }

    @Override
    public void handleMouse() {
        int x = Mouse.getEventX() * this.width / this.minecraft.width;
        int y = this.height - Mouse.getEventY() * this.height / this.minecraft.height - 1;
        int button = Mouse.getEventButton();
        if (Mouse.getEventButtonState()) {
            this.mouseClicked(x, y, button);
        } else if (button != -1) {
            this.mouseReleased(x, y, button);
        }
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (button.active) {
            if (button.id == 200) { // done
                this.minecraft.options.save();
                this.minecraft.openScreen(this.parent);
            }

            if (button.id > 200) { // categories
                int ordinal = button.id - 201;
                this.setCategory(BVOptions.Category.values()[ordinal]);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        int i = this.options.guiScale;
        super.mouseClicked(mouseX, mouseY, button);
        this.listWidget.mouseClicked(mouseX, mouseY, button);
        if (this.options.guiScale != i) {
            Window window = new Window(this.minecraft.options, this.minecraft.width, this.minecraft.height);
            int w = window.getWidth();
            int h = window.getHeight();
            this.init(this.minecraft, w, h);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int button) {
        int i = this.options.guiScale;
        super.mouseReleased(mouseX, mouseY, button);
        this.listWidget.mouseReleased(mouseX, mouseY, button);
        if (this.options.guiScale != i) {
            Window window = new Window(this.minecraft.options, this.minecraft.width, this.minecraft.height);
            int w = window.getWidth();
            int h = window.getHeight();
            this.init(this.minecraft, w, h);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        this.renderBackground();
        this.listWidget.render(mouseX, mouseY, tickDelta);
        this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 12, 16777215);

        String bvVersion = "§8Beta Visuals " + FabricLoader.getInstance().getModContainer("betavisuals").get().getMetadata().getVersion().getFriendlyString();
        String mcVersion = "§8Minecraft Beta 1.7.3";
        this.textRenderer.drawWithShadow(bvVersion, 4, this.height - 12, 1);
        this.textRenderer.drawWithShadow(mcVersion, this.width - this.textRenderer.getWidth(mcVersion) - 4, this.height - 12, 1);

        super.render(mouseX, mouseY, tickDelta);
    }

    private void setCategory(BVOptions.Category category) {
        this.category = category;
        this.init();
    }
}
