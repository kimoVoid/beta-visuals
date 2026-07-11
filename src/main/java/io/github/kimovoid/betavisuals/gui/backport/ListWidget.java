package io.github.kimovoid.betavisuals.gui.backport;

import io.github.kimovoid.betavisuals.util.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.vertex.Tesselator;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

/**
 * This is lowkey just backported from release 1.7 because older versions
 * don't have all the functions that I need so why reinvent the wheel?
 */
public abstract class ListWidget {

    protected final Minecraft minecraft;
    protected int width;
    private int height;
    protected int minY;
    protected int maxY;
    protected int maxX;
    protected int minX;
    protected final int entryHeight;
    private int upButtonId;
    private int downButtonId;
    protected int mouseX;
    protected int mouseY;
    protected boolean centerAlongY = true;
    private float mouseYStart = -2.0F;
    private float scrollSpeedMultiplier;
    private float scrollAmount;
    private int pos = -1;
    private long time;
    private boolean renderSelectionHighlight = true;
    private boolean renderHeader;
    protected int headerHeight;
    private boolean scrolling = true;

    public ListWidget(Minecraft minecraft, int width, int height, int minY, int maxY, int entryHeight) {
        this.minecraft = minecraft;
        this.width = width;
        this.height = height;
        this.minY = minY;
        this.maxY = maxY;
        this.entryHeight = entryHeight;
        this.minX = 0;
        this.maxX = width;
    }

    public void setBounds(int width, int height, int minY, int maxY) {
        this.width = width;
        this.height = height;
        this.minY = minY;
        this.maxY = maxY;
        this.minX = 0;
        this.maxX = width;
    }

    public void setRenderSelectionHighlight(boolean renderSelectionHighlight) {
        this.renderSelectionHighlight = renderSelectionHighlight;
    }

    protected void setHeader(boolean renderHeader, int headerHeight) {
        this.renderHeader = renderHeader;
        this.headerHeight = renderHeader ? headerHeight : 0;
    }

    protected abstract int size();

    protected abstract void entryClicked(int index, boolean doubleClick, int mouseX, int mouseY);

    protected abstract boolean isEntrySelected(int index);

    protected int getHeight() {
        return this.size() * this.entryHeight + this.headerHeight;
    }

    protected abstract void renderBackground();

    protected abstract void renderEntry(int index, int x, int y, int entryHeight, Tesselator tesselator, int mouseX, int mouseY);

    protected void renderHeader(int x, int y, Tesselator tesselator) {}

    protected void headerClicked(int x, int y) {}

    protected void renderDecorations(int mouseX, int mouseY) {}

    public int getEntryAt(int x, int y) {
        int rowLeft = this.minX;
        int rowRight = this.maxX;
        int relY = y - this.minY - this.headerHeight + (int) this.scrollAmount - 4;
        int index = relY / this.entryHeight;
        return x < this.getScrollbarPosition() && x >= rowLeft && x <= rowRight && index >= 0 && relY >= 0 && index < this.size() ? index : -1;
    }

    public void setScrollButtonIds(int upButtonId, int downButtonId) {
        this.upButtonId = upButtonId;
        this.downButtonId = downButtonId;
    }

    private void capScrolling() {
        int maxScroll = this.getMaxScroll();
        if (maxScroll < 0) maxScroll /= 2;
        if (!this.centerAlongY && maxScroll < 0) maxScroll = 0;
        if (this.scrollAmount < 0.0F) this.scrollAmount = 0.0F;
        if (this.scrollAmount > maxScroll) this.scrollAmount = maxScroll;
    }

    public int getMaxScroll() {
        return this.getHeight() - (this.maxY - this.minY - 4);
    }

    public int getScrollAmount() {
        return (int) this.scrollAmount;
    }

    public boolean isMouseInList(int mouseY) {
        return mouseY >= this.minY && mouseY <= this.maxY;
    }

    public void scroll(int amount) {
        this.scrollAmount += amount;
        this.capScrolling();
        this.mouseYStart = -2.0F;
    }

    public void buttonClicked(ButtonWidget button) {
        if (!button.active) return;
        if (button.id == this.upButtonId) {
            this.scrollAmount -= (float) (this.entryHeight * 2) / 3;
            this.mouseYStart = -2.0F;
            this.capScrolling();
        } else if (button.id == this.downButtonId) {
            this.scrollAmount += (float) (this.entryHeight * 2) / 3;
            this.mouseYStart = -2.0F;
            this.capScrolling();
        }
    }

    public void render(int mouseX, int mouseY, float tickDelta) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        if (this.hasBackground()) this.renderBackground();
        int entryCount = this.size();
        int scrollbarX = this.getScrollbarPosition();
        int scrollbarRight = scrollbarX + 6;

        if (mouseX > this.minX && mouseX < this.maxX && mouseY > this.minY && mouseY < this.maxY) {
            if (!Mouse.isButtonDown(0) || !this.isScrolling()) {
                for (; Mouse.next(); this.minecraft.screen.handleMouse()) {
                    int wheel = Mouse.getEventDWheel();
                    if (wheel != 0) {
                        wheel = wheel > 0 ? -1 : 1;
                        this.scrollAmount += (float) (wheel * this.entryHeight) / 2;
                    }
                }
                this.mouseYStart = -1.0F;
            } else if (this.mouseYStart == -1.0F) {
                int didClick = 1;
                if (mouseY >= this.minY && mouseY <= this.maxY) {
                    int rowLeft = this.width / 2 - this.getRowWidth() / 2;
                    int rowRight = this.width / 2 + this.getRowWidth() / 2;
                    int relY = mouseY - this.minY - this.headerHeight + (int) this.scrollAmount - 4;
                    int index = relY / this.entryHeight;
                    if (mouseX >= rowLeft && mouseX <= rowRight && index >= 0 && relY >= 0 && index < entryCount) {
                        boolean doubleClick = index == this.pos && System.currentTimeMillis() - this.time < 250L;
                        this.entryClicked(index, doubleClick, mouseX, mouseY);
                        this.pos = index;
                        this.time = System.currentTimeMillis();
                    } else if (mouseX >= rowLeft && mouseX <= rowRight && relY < 0) {
                        this.headerClicked(mouseX - rowLeft, mouseY - this.minY + (int) this.scrollAmount - 4);
                        didClick = 0;
                    }

                    if (mouseX >= scrollbarX && mouseX <= scrollbarRight) {
                        this.scrollSpeedMultiplier = -1.0F;
                        int maxScroll = Math.max(this.getMaxScroll(), 1);
                        int thumbHeight = (int) ((float) ((this.maxY - this.minY) * (this.maxY - this.minY)) / this.getHeight());
                        thumbHeight = Math.max(thumbHeight, 32);
                        thumbHeight = Math.min(thumbHeight, this.maxY - this.minY - 8);
                        this.scrollSpeedMultiplier /= (float) (this.maxY - this.minY - thumbHeight) / maxScroll;
                    } else {
                        this.scrollSpeedMultiplier = 1.0F;
                    }

                    this.mouseYStart = didClick != 0 ? mouseY : -2.0F;
                } else {
                    this.mouseYStart = -2.0F;
                }
            } else if (this.mouseYStart >= 0.0F) {
                this.scrollAmount -= (mouseY - this.mouseYStart) * this.scrollSpeedMultiplier;
                this.mouseYStart = mouseY;
            }
        }

        this.capScrolling();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        Tesselator tesselator = Tesselator.INSTANCE;
        if (this.hasBackground()) {
            GL11.glBindTexture(3553, this.minecraft.textureManager.load("/gui/background.png"));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            float texScale = 32.0F;
            tesselator.begin();
            tesselator.color(2105376);
            tesselator.vertex(this.minX, this.maxY, 0.0, this.minX / texScale, (this.maxY + (int) this.scrollAmount) / texScale);
            tesselator.vertex(this.maxX, this.maxY, 0.0, this.maxX / texScale, (this.maxY + (int) this.scrollAmount) / texScale);
            tesselator.vertex(this.maxX, this.minY, 0.0, this.maxX / texScale, (this.minY + (int) this.scrollAmount) / texScale);
            tesselator.vertex(this.minX, this.minY, 0.0, this.minX / texScale, (this.minY + (int) this.scrollAmount) / texScale);
            tesselator.end();
        } else {
            GuiUtil.fill(this.minX, this.minY, this.maxX, this.maxY, 838860800);
        }
        int listX = this.minX + 5; // hardcoded margin i had like 3h of sleep i dont want to use my brain
        int listY = this.minY + 4 - (int) this.scrollAmount;
        if (this.renderHeader && this.hasBackground()) this.renderHeader(listX, listY, tesselator);
        this.renderList(listX, listY, mouseX, mouseY);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        int fadeSize = 4;
        if (this.hasBackground()) {
            this.renderHoleBackground(0, this.minY);
            this.renderHoleBackground(this.maxY, this.height);
        }
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        tesselator.begin();
        tesselator.color(0, 0);
        tesselator.vertex(this.minX, this.minY + fadeSize, 0.0, 0.0, 1.0);
        tesselator.vertex(this.maxX, this.minY + fadeSize, 0.0, 1.0, 1.0);
        tesselator.color(0, 255);
        tesselator.vertex(this.maxX, this.minY, 0.0, 1.0, 0.0);
        tesselator.vertex(this.minX, this.minY, 0.0, 0.0, 0.0);
        tesselator.end();

        tesselator.begin();
        tesselator.color(0, 255);
        tesselator.vertex(this.minX, this.maxY, 0.0, 0.0, 1.0);
        tesselator.vertex(this.maxX, this.maxY, 0.0, 1.0, 1.0);
        tesselator.color(0, 0);
        tesselator.vertex(this.maxX, this.maxY - fadeSize, 0.0, 1.0, 0.0);
        tesselator.vertex(this.minX, this.maxY - fadeSize, 0.0, 0.0, 0.0);
        tesselator.end();

        int maxScroll = this.getMaxScroll();
        if (maxScroll > 0) {
            int thumbHeight = (this.maxY - this.minY) * (this.maxY - this.minY) / this.getHeight();
            thumbHeight = Math.max(thumbHeight, 32);
            thumbHeight = Math.min(thumbHeight, this.maxY - this.minY - 8);
            int thumbTop = (int) this.scrollAmount * (this.maxY - this.minY - thumbHeight) / maxScroll + this.minY;
            if (thumbTop < this.minY) thumbTop = this.minY;

            tesselator.begin();
            tesselator.color(0, 255);
            tesselator.vertex(scrollbarX, this.maxY, 0.0, 0.0, 1.0);
            tesselator.vertex(scrollbarRight, this.maxY, 0.0, 1.0, 1.0);
            tesselator.vertex(scrollbarRight, this.minY, 0.0, 1.0, 0.0);
            tesselator.vertex(scrollbarX, this.minY, 0.0, 0.0, 0.0);
            tesselator.end();

            tesselator.begin();
            tesselator.color(8421504, 255);
            tesselator.vertex(scrollbarX, thumbTop + thumbHeight, 0.0, 0.0, 1.0);
            tesselator.vertex(scrollbarRight, thumbTop + thumbHeight, 0.0, 1.0, 1.0);
            tesselator.vertex(scrollbarRight, thumbTop, 0.0, 1.0, 0.0);
            tesselator.vertex(scrollbarX, thumbTop, 0.0, 0.0, 0.0);
            tesselator.end();

            tesselator.begin();
            tesselator.color(12632256, 255);
            tesselator.vertex(scrollbarX, thumbTop + thumbHeight - 1, 0.0, 0.0, 1.0);
            tesselator.vertex(scrollbarRight - 1, thumbTop + thumbHeight - 1, 0.0, 1.0, 1.0);
            tesselator.vertex(scrollbarRight - 1, thumbTop, 0.0, 1.0, 0.0);
            tesselator.vertex(scrollbarX, thumbTop, 0.0, 0.0, 0.0);
            tesselator.end();
        }

        this.renderDecorations(mouseX, mouseY);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public void setScrolling(boolean scrolling) {
        this.scrolling = scrolling;
    }

    public boolean isScrolling() {
        return this.scrolling;
    }

    public int getRowWidth() {
        return 220;
    }

    protected void renderList(int x, int y, int mouseX, int mouseY) {
        int entryCount = this.size();
        Tesselator tesselator = Tesselator.INSTANCE;

        for (int index = 0; index < entryCount; index++) {
            int entryTop = y + index * this.entryHeight + this.headerHeight;
            int entryInner = this.entryHeight - 4;
            if (entryTop > this.maxY || entryTop + entryInner < this.minY) continue;

            if (this.renderSelectionHighlight && this.isEntrySelected(index)) {
                int rowLeft  = this.minX + (this.width / 2 - this.getRowWidth() / 2);
                int rowRight = this.minX + this.width / 2 + this.getRowWidth() / 2;
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                tesselator.begin();
                tesselator.color(8421504);
                tesselator.vertex(rowLeft, entryTop + entryInner + 2, 0.0, 0.0, 1.0);
                tesselator.vertex(rowRight, entryTop + entryInner + 2, 0.0, 1.0, 1.0);
                tesselator.vertex(rowRight, entryTop - 2, 0.0, 1.0, 0.0);
                tesselator.vertex(rowLeft, entryTop - 2, 0.0, 0.0, 0.0);
                tesselator.color(0);
                tesselator.vertex(rowLeft + 1, entryTop + entryInner + 1, 0.0, 0.0, 1.0);
                tesselator.vertex(rowRight - 1, entryTop + entryInner + 1, 0.0, 1.0, 1.0);
                tesselator.vertex(rowRight - 1, entryTop - 1, 0.0, 1.0, 0.0);
                tesselator.vertex(rowLeft + 1, entryTop - 1, 0.0, 0.0, 0.0);
                tesselator.end();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }

            this.renderEntry(index, x, entryTop, entryInner, tesselator, mouseX, mouseY);
        }
    }

    protected int getScrollbarPosition() {
        return this.width / 2 + 124;
    }

    private void renderHoleBackground(int top, int bottom) {
        Tesselator tesselator = Tesselator.INSTANCE;
        GL11.glBindTexture(3553, this.minecraft.textureManager.load("/gui/background.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float texScale = 32.0F;
        tesselator.begin();
        tesselator.color(4210752, 255);
        tesselator.vertex(this.minX, bottom, 0.0, 0.0, bottom / texScale);
        tesselator.vertex(this.minX + this.width, bottom, 0.0, this.width / texScale, bottom / texScale);
        tesselator.color(4210752, 255);
        tesselator.vertex(this.minX + this.width, top, 0.0, this.width / texScale, top / texScale);
        tesselator.vertex(this.minX, top, 0.0, 0.0, top / texScale);
        tesselator.end();
    }

    public void setX(int minX) {
        this.minX = minX;
        this.maxX = minX + this.width;
    }

    public int getEntryHeight() {
        return this.entryHeight;
    }

    private boolean hasBackground() {
        return Minecraft.INSTANCE.world == null;
    }
}
