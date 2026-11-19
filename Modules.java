package beerlegit;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

/**
 * Beer Visuals — pure visual / quality-of-life modules only.
 * No combat, movement, XRay, ESP or any advantage-giving features.
 */
public final class Modules {
    private Modules() {}

    public static void init(List<Module> list) {

        // ==================== HUD ====================
        list.add(new Module("HUD", Category.HUD) {
            {
                bool("Watermark", true);
                bool("Info Panel", true);
                bool("Coordinates", true);
                bool("FPS", true);
                bool("Armor Status", true);
                bool("Potion Effects", true);
            }
        });

        // ==================== VISUAL ====================
        list.add(new Module("Crosshair", Category.VISUAL) {
            {
                num("Style", 1, 0, 3);      // 0=vanilla, 1=cross, 2=dot, 3=circle
                num("Size", 6, 2, 16);
                num("Gap", 2, 0, 8);
                num("Thickness", 1, 1, 3);
                num("Red", 168, 0, 255);
                num("Green", 85, 0, 255);
                num("Blue", 247, 0, 255);
                bool("Outline", true);
            }
        });

        list.add(new Module("HitMarkers", Category.VISUAL) {
            {
                bool("Sound", true);
                bool("Particles", true);
                num("Size", 8, 4, 20);
                num("Duration", 12, 4, 30);
            }
            public int timer = 0;

            @Override public void onTick(MinecraftClient mc) {
                if (timer > 0) timer--;
            }
        });

        list.add(new Module("Fullbright", Category.VISUAL) {
            double oldGamma = 1.0;

            @Override public void onEnable(MinecraftClient mc) {
                if (mc.options != null) {
                    oldGamma = mc.options.gamma;
                    mc.options.gamma = 16.0;
                }
            }

            @Override public void onDisable(MinecraftClient mc) {
                if (mc.options != null) mc.options.gamma = oldGamma;
            }

            @Override public void onTick(MinecraftClient mc) {
                if (mc.options != null && mc.options.gamma < 15.0) {
                    mc.options.gamma = 16.0;
                }
            }
        });

        list.add(new Module("BetterOutline", Category.VISUAL) {
            {
                num("Width", 2.0, 1.0, 5.0);
                num("Red", 168, 0, 255);
                num("Green", 85, 0, 255);
                num("Blue", 247, 0, 255);
            }
        });

        // ==================== MISC ====================
        list.add(new Module("NoHurtCam", Category.MISC) {
            // pure visual – removes camera shake on damage (requires mixin in full version)
        });
    }
}
