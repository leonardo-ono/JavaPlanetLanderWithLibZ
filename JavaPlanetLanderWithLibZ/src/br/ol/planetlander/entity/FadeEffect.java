package br.ol.planetlander.entity;

import br.ol.planetlander.PlanetLanderEntity;
import br.ol.planetlander.PlanetLanderGame;
import java.util.Arrays;
import me.winspeednl.libz.core.GameCore;
import me.winspeednl.libz.screen.Render;

/**
 * FadeEffect class.
 *
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class FadeEffect extends PlanetLanderEntity {
    
    private double alpha = 1;
    private double targetAlpha = 1;
    
    public FadeEffect(PlanetLanderGame game) {
        super(game);
    }

    @Override
    public void init(GameCore gc) {
        w = PlanetLanderGame.SCREEN_WIDTH;
        h = PlanetLanderGame.SCREEN_HEIGHT;
        pixels = new int[w * h];
        Arrays.fill(pixels, 0xFF000000);
        visible = true;
    }

    @Override
    public void update(GameCore gc) {
        double s = (targetAlpha - alpha);
        if (Math.abs(s) < 0.01) {
            alpha = targetAlpha;
        }
        else {
            s = s < 0 ? -1 : 1;
            alpha += s * 0.02;
        }
    }
    
    @Override
    public void render(GameCore gc, Render render) {
        if (!visible) {
            return;
        }
        for (int dx = 0; dx < w; dx++) {
            for (int dy = 0; dy < h; dy++) {
                int sourceColor = render.getPixels()[(x + dx) + (y + dy) * w];
                int sourceRed = getR(sourceColor);
                int sourceGreen = getG(sourceColor);
                int sourceBlue = getB(sourceColor);
                int destinationColor = pixels[dx + dy * w];
                int destinationRed = getR(destinationColor);
                int destinationGreen = getG(destinationColor);
                int destinationBlue = getB(destinationColor);
                int finalRed = lerpColor(sourceRed, destinationRed, alpha);
                int finalGreen = lerpColor(sourceGreen, destinationGreen, alpha);
                int finalBlue = lerpColor(sourceBlue, destinationBlue, alpha);
                int finalColor = getRGB(finalRed, finalGreen, finalBlue);
                render.setPixel(x + dx, y + dy, finalColor);
            }
        }
    }
    
    private int getR(int color) {
        return ((color & 0x00FF0000) >> 16);
    }
    
    private int getG(int color) {
        return ((color & 0x0000FF00) >> 8);
    }
    
    private int getB(int color) {
        return (color & 0x000000FF);
    }
    
    private int getRGB(int r, int g, int b) {
        int a = 255;
        return (a << 24) + (r << 16) + (g << 8) + b;
    }
    
    private int lerpColor(int a, int b, double p) {
        return (int) (a + p * (b - a));
    }
    
    // broadcast messages
    
    public void hideFadeEffect() {
        visible = false;
    }
    
    public void fadeIn() {
        targetAlpha = 0;
        visible = true;
    }

    public void fadeOut() {
        targetAlpha = 1;
        visible = true;
    }

    // public parameters
    
    public boolean fadeEffectFinished() {
        return alpha == targetAlpha;
    }
    
}
