package beerlegit;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Modern Beer Visuals HUD — inspired by clean visual clients.
 */
public final class Hud {
    private Hud() {}

    public static void render(MatrixStack ms, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden) return;

        Module hudMod = BeerLegit.get("HUD");
        if (hudMod == null || !hudMod.enabled) return;

        TextRenderer tr = mc.textRenderer;
        int sw = mc.getWindow().getScaledWidth();
        int sh = mc.getWindow().getScaledHeight();

        boolean showWatermark = getBool(hudMod, "Watermark", true);
        boolean showInfo = getBool(hudMod, "Info Panel", true);
        boolean showCoords = getBool(hudMod, "Coordinates", true);
        boolean showFps = getBool(hudMod, "FPS", true);
        boolean showArmor = getBool(hudMod, "Armor Status", true);
        boolean showPotions = getBool(hudMod, "Potion Effects", true);

        // ===== Watermark (top-left) =====
        if (showWatermark) {
            String title = "BEER VISUALS";
            int tw = tr.getWidth(title);
            fill(ms, 6, 6, 18 + tw, 22, 0xCC0F0A18);
            fill(ms, 6, 6, 9, 22, 0xFFA855F7);
            tr.drawWithShadow(ms, title, 14, 10, 0xFFC084FC);
            tr.drawWithShadow(ms, "1.16.5", 14 + tw + 6, 10, 0xFF6B7280);
        }

        // ===== Info panel (bottom-left) =====
        if (showInfo || showCoords || showFps) {
            int y = sh - 8;
            List<String> lines = new ArrayList<>();

            if (showCoords) {
                int x = (int) mc.player.getX();
                int py = (int) mc.player.getY();
                int z = (int) mc.player.getZ();
                lines.add(String.format("XYZ  %d  %d  %d", x, py, z));
            }
            if (showFps) {
                lines.add("FPS  " + mc.fpsDebugString.split(" ")[0]);
            }
            if (showInfo) {
                int hp = MathHelper.ceil(mc.player.getHealth());
                int maxHp = MathHelper.ceil(mc.player.getMaxHealth());
                lines.add("HP   " + hp + " / " + maxHp);
                lines.add("Armor " + mc.player.getArmor());
            }

            int panelH = lines.size() * 11 + 8;
            int panelY = sh - panelH - 6;
            fill(ms, 6, panelY, 130, sh - 6, 0xCC0F0A18);
            fill(ms, 6, panelY, 9, sh - 6, 0xFF7C3AED);

            int ly = panelY + 5;
            for (String line : lines) {
                tr.drawWithShadow(ms, line, 14, ly, 0xFFE5E7EB);
                ly += 11;
            }
        }

        // ===== Potion effects (right side) =====
        if (showPotions) {
            Collection<StatusEffectInstance> effects = mc.player.getStatusEffects();
            if (!effects.isEmpty()) {
                int y = 30;
                for (StatusEffectInstance e : effects) {
                    String name = e.getEffectType().getName().getString();
                    int amp = e.getAmplifier();
                    String text = name + (amp > 0 ? " " + (amp + 1) : "") + " " + formatDuration(e.getDuration());
                    int w = tr.getWidth(text);
                    fill(ms, sw - w - 16, y - 2, sw - 6, y + 10, 0xAA0F0A18);
                    tr.drawWithShadow(ms, text, sw - w - 12, y, 0xFFC084FC);
                    y += 14;
                }
            }
        }

        // ===== Enabled modules list (top-right) =====
        List<Module> on = new ArrayList<>();
        for (Module m : BeerLegit.MODULES) {
            if (m.enabled && m != hudMod) on.add(m);
        }
        on.sort((a, b) -> Integer.compare(tr.getWidth(b.name), tr.getWidth(a.name)));

        int y = 8;
        int i = 0;
        for (Module m : on) {
            float hue = 0.75f + 0.06f * (float) Math.sin(System.currentTimeMillis() / 1600.0 + i * 0.6);
            int c = MathHelper.hsvToRgb(hue, 0.5f, 1f) | 0xFF000000;
            int nw = tr.getWidth(m.name);
            int x = sw - nw - 14;
            fill(ms, x - 8, y - 2, sw - 6, y + 11, 0xAA0F0A18);
            fill(ms, sw - 9, y - 2, sw - 6, y + 11, 0xFFA855F7);
            tr.drawWithShadow(ms, m.name, x, y, c);
            y += 13;
            i++;
        }
    }

    private static boolean getBool(Module m, String name, boolean def) {
        for (Setting s : m.settings) {
            if (s.name.equals(name)) return s.on();
        }
        return def;
    }

    private static String formatDuration(int ticks) {
        int sec = ticks / 20;
        if (sec < 60) return sec + "s";
        return (sec / 60) + ":" + String.format("%02d", sec % 60);
    }

    private static void fill(MatrixStack ms, int x1, int y1, int x2, int y2, int color) {
        net.minecraft.client.gui.DrawableHelper.fill(ms, x1, y1, x2, y2, color);
    }
}
