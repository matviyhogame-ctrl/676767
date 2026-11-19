package beerlegit;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;
import java.util.List;

public class GuiScreen extends Screen {
    private static final int CAT_W = 90;
    private static final int MOD_W = 130;
    private static final int SET_W = 140;
    private static final int RH = 16;
    private static final int PAD = 8;
    private static final int PANEL_H = 220;

    private Category selected = Category.HUD;
    private Module selectedMod = null;
    private Setting dragging = null;
    private int dragX, dragW;

    // panel top-left
    private int px, py;

    private static class Hit {
        enum Type { CAT, MOD, TOGGLE, SETTING, SLIDER }
        Type type;
        Category cat;
        Module mod;
        Setting set;
        int x, y, w, h;
    }

    private final List<Hit> hits = new ArrayList<>();

    public GuiScreen() {
        super(new LiteralText("BEER VISUALS"));
    }

    @Override public boolean isPauseScreen() { return false; }

    @Override
    public void init() {
        px = (this.width - (CAT_W + MOD_W + SET_W + PAD * 2)) / 2;
        py = (this.height - PANEL_H) / 2;
    }

    @Override
    public void removed() {
        BeerLegit.saveConfig();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT || keyCode == 256) {
            BeerLegit.wasDown = true;
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void fillRound(MatrixStack ms, int x1, int y1, int x2, int y2, int color) {
        fill(ms, x1, y1, x2, y2, color);
    }

    @Override
    public void render(MatrixStack ms, int mx, int my, float delta) {
        // dim background
        fill(ms, 0, 0, this.width, this.height, 0x88000000);
        hits.clear();

        int totalW = CAT_W + MOD_W + SET_W + PAD * 2;
        // main panel bg
        fillRound(ms, px - 4, py - 22, px + totalW + 4, py + PANEL_H + 4, 0xF0120C1C);
        // top bar
        fillRound(ms, px - 4, py - 22, px + totalW + 4, py - 2, 0xFF1A1030);
        this.textRenderer.drawWithShadow(ms, "BEER VISUALS", px + 6, py - 16, 0xFFC084FC);
        this.textRenderer.drawWithShadow(ms, "Right Shift to close", px + totalW - 110, py - 16, 0xFF9B8FB8);

        // ===== CATEGORIES =====
        int cy = py;
        fillRound(ms, px, cy, px + CAT_W, cy + PANEL_H, 0xEE0E0A18);
        for (Category c : Category.values()) {
            boolean sel = c == selected;
            int bg = sel ? 0xFF4C1D95 : 0x00000000;
            if (sel) fill(ms, px, cy, px + 3, cy + RH + 4, 0xFFA855F7);
            fill(ms, px, cy, px + CAT_W, cy + RH + 4, bg);
            int col = sel ? 0xFFC084FC : 0xFF9B8FB8;
            this.textRenderer.drawWithShadow(ms, c.label, px + 10, cy + 5, col);
            Hit h = new Hit();
            h.type = Hit.Type.CAT; h.cat = c; h.x = px; h.y = cy; h.w = CAT_W; h.h = RH + 4;
            hits.add(h);
            cy += RH + 4;
        }

        // ===== MODULES =====
        int mx0 = px + CAT_W + PAD;
        fillRound(ms, mx0, py, mx0 + MOD_W, py + PANEL_H, 0xEE0E0A18);
        int my0 = py + 4;
        for (Module m : BeerLegit.MODULES) {
            if (m.category != selected) continue;
            boolean on = m.enabled;
            boolean sel = m == selectedMod;
            int bg = sel ? 0xFF2A1848 : (on ? 0xF0281450 : 0x00000000);
            fill(ms, mx0, my0, mx0 + MOD_W, my0 + RH, bg);
            if (on) fill(ms, mx0, my0, mx0 + 2, my0 + RH, 0xFFA855F7);
            // toggle indicator
            int tx = mx0 + MOD_W - 18;
            fill(ms, tx, my0 + 3, tx + 12, my0 + 13, on ? 0xFF7C3AED : 0xFF2A2040);
            if (on) fill(ms, tx + 6, my0 + 4, tx + 11, my0 + 12, 0xFFFFFFFF);
            else fill(ms, tx + 1, my0 + 4, tx + 6, my0 + 12, 0xFF888888);

            this.textRenderer.drawWithShadow(ms, m.name, mx0 + 8, my0 + 4, on ? 0xFFFFFFFF : 0xFFBDB4D6);

            Hit hm = new Hit();
            hm.type = Hit.Type.MOD; hm.mod = m; hm.x = mx0; hm.y = my0; hm.w = MOD_W - 22; hm.h = RH;
            hits.add(hm);
            Hit ht = new Hit();
            ht.type = Hit.Type.TOGGLE; ht.mod = m; ht.x = tx; ht.y = my0; ht.w = 14; ht.h = RH;
            hits.add(ht);
            my0 += RH + 2;
        }

        // ===== SETTINGS =====
        int sx = mx0 + MOD_W + PAD;
        fillRound(ms, sx, py, sx + SET_W, py + PANEL_H, 0xEE0E0A18);
        if (selectedMod != null) {
            this.textRenderer.drawWithShadow(ms, selectedMod.name, sx + 6, py + 6, 0xFFC084FC);
            int sy = py + 22;
            if (selectedMod.settings.isEmpty()) {
                this.textRenderer.drawWithShadow(ms, "No settings", sx + 6, sy, 0xFF6B6280);
            } else {
                for (Setting s : selectedMod.settings) {
                    if (s.bool) {
                        fill(ms, sx + 4, sy, sx + SET_W - 4, sy + RH, 0xFF160E24);
                        this.textRenderer.drawWithShadow(ms, s.name, sx + 8, sy + 4, 0xFFECE8F7);
                        int bx = sx + SET_W - 22;
                        fill(ms, bx, sy + 3, bx + 12, sy + 13, s.on() ? 0xFF7C3AED : 0xFF2A2040);
                        if (s.on()) fill(ms, bx + 6, sy + 4, bx + 11, sy + 12, 0xFFFFFFFF);
                        else fill(ms, bx + 1, sy + 4, bx + 6, sy + 12, 0xFF888888);
                        Hit hs = new Hit();
                        hs.type = Hit.Type.SETTING; hs.set = s; hs.x = sx + 4; hs.y = sy; hs.w = SET_W - 8; hs.h = RH;
                        hits.add(hs);
                    } else {
                        fill(ms, sx + 4, sy, sx + SET_W - 4, sy + RH + 8, 0xFF160E24);
                        String val = s.name + ": " + (Math.round(s.value * 100.0) / 100.0);
                        this.textRenderer.drawWithShadow(ms, val, sx + 8, sy + 3, 0xFFECE8F7);
                        int barX = sx + 8;
                        int barW = SET_W - 20;
                        int barY = sy + RH;
                        fill(ms, barX, barY, barX + barW, barY + 4, 0xFF2A2040);
                        int fw = (int) (barW * (s.value - s.min) / (s.max - s.min));
                        fill(ms, barX, barY, barX + fw, barY + 4, 0xFFA855F7);
                        fill(ms, barX + fw - 2, barY - 2, barX + fw + 2, barY + 6, 0xFFC084FC);
                        Hit hs = new Hit();
                        hs.type = Hit.Type.SLIDER; hs.set = s; hs.x = barX; hs.y = barY - 4; hs.w = barW; hs.h = 12;
                        hits.add(hs);
                        sy += 8;
                    }
                    sy += RH + 4;
                }
            }
        } else {
            this.textRenderer.drawWithShadow(ms, "Select module", sx + 6, py + 10, 0xFF6B6280);
        }

        // footer hint
        this.textRenderer.drawWithShadow(ms, "LMB toggle / select   ·   drag sliders", px, py + PANEL_H + 10, 0xFF6B6280);
        super.render(ms, mx, my, delta);
    }

    private void applySlider(Setting s, double mouseX, int x, int w) {
        double f = (mouseX - x) / (double) w;
        f = Math.max(0, Math.min(1, f));
        s.value = Math.round((s.min + (s.max - s.min) * f) * 100.0) / 100.0;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Hit h : hits) {
            if (mouseX >= h.x && mouseX < h.x + h.w && mouseY >= h.y && mouseY < h.y + h.h) {
                if (h.type == Hit.Type.CAT) {
                    selected = h.cat;
                    selectedMod = null;
                    return true;
                }
                if (h.type == Hit.Type.MOD) {
                    selectedMod = h.mod;
                    return true;
                }
                if (h.type == Hit.Type.TOGGLE) {
                    h.mod.toggle();
                    return true;
                }
                if (h.type == Hit.Type.SETTING) {
                    if (h.set.bool) h.set.value = h.set.on() ? 0 : 1;
                    return true;
                }
                if (h.type == Hit.Type.SLIDER) {
                    dragging = h.set;
                    dragX = h.x;
                    dragW = h.w;
                    applySlider(h.set, mouseX, h.x, h.w);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (dragging != null) {
            applySlider(dragging, mouseX, dragX, dragW);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = null;
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
