package br.ol.planetlander.entity;

import br.ol.planetlander.PlanetLanderEntity;
import br.ol.planetlander.PlanetLanderGame;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.winspeednl.libz.core.GameCore;
import me.winspeednl.libz.image.Sprite;
import me.winspeednl.libz.screen.Render;

/**
 * PoweredByLibZ class.
 *
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class PoweredByLibZ extends PlanetLanderEntity {
    
    private final List<Pixel> logoPixels = new ArrayList<>();
    
    private int logoPixelsSpiralFinishCount;
    private int logoPixelsExplosionFinishCount;
    
    public PoweredByLibZ(PlanetLanderGame game) {
        super(game);
        w = 120;
        h = 92;
        Sprite logoSprite = new Sprite("/res/poweredByLibZ.png", 0, 0, w, h);
        pixels = logoSprite.pixels;

        int offsetX = 100;
        int offsetY = 59;
        for (int ly = 0; ly < h; ly++) {
            for (int lx = 0; lx < w; lx++) {
                int color = pixels[lx + ly * w];
                if (color != 0xFFFFFFFF) {
                    logoPixels.add(new Pixel(lx + offsetX, ly + offsetY, color));
                }
            }
        }
    }

    private class Pixel {
        
        int color;
        boolean finishedSpiral = false;
        boolean finishedExplosion = false;
        
        // spiral
        double ox, oy, x, y, dx, dy;
        double d, a, s, v, vc, kx, ky;

        // explosion
        double sx, sy, vx, vy;
        
        Pixel(double x, double y, int color) {
            this.ox = this.x = x;
            this.oy = this.y = y;
            this.color = color;
        }
        
        void reset() {
            x = ox;
            y = oy;
            
            // spiral
            d = 300 + 50 * Math.random();
            a = Math.PI * Math.random();
            s = (int) (2 * Math.random()) == 0 ? -1 : 1;
            v = 0.05 + 0.05 * Math.random();
            vc = 0.95 + 0.02 * Math.random();
            kx = 0.5 + 0.5 * Math.random();
            ky = 0.5 + 0.5 * Math.random();

            // explosion
            sx = (int) (2 * Math.random()) == 0 ? -1 : 1;
            sy = (int) (2 * Math.random()) == 0 ? -1 : 1;
            vx = sx * (1.5 * Math.random());
            vy = sy * (0.5 * Math.random());
        
            finishedSpiral = false;
            finishedExplosion = false;
        }
        
        void drawSpiral(GameCore gc, Render render) {
            dx = d * Math.cos(a * kx);
            dy = d * Math.sin(a * ky);
            a += s * v;
            d = d * vc;
            if (d < 1.5 && !finishedSpiral) {
                finishedSpiral = true;
                logoPixelsSpiralFinishCount--;
                d = 0;
            }            
            render.setPixel((int) (x + dx), (int) (y + dy), color);
        }
        
        void drawExplosion(GameCore gc, Render render) {
            x += vx;
            y += vy;
            if (y > PlanetLanderGame.SCREEN_HEIGHT) {
                y = PlanetLanderGame.SCREEN_HEIGHT;
                vy = -vy * (0.25 + 0.25 * Math.random());
                if (Math.abs(vy) < 0.1 && !finishedExplosion) {
                    finishedExplosion = true;
                    logoPixelsExplosionFinishCount--;
                    vy = 0;
                }
            }
            vy += 0.1;
            render.setPixel((int) (x + dx), (int) (y + dy), color);
        }
        
    }
    
    @Override
    public void updatePoweredByLibZ(GameCore gc) {
        yield:
        while (true) {
            switch (instructionPointer) {
                case 0:
                    log.info("starting PoweredByLibZ ...");
                    game.broadcastMessage("fadeIn");
                    logoPixels.stream().forEach((pixel) -> {
                        pixel.reset();
                    });
                    logoPixelsSpiralFinishCount = logoPixels.size();
                    logoPixelsExplosionFinishCount = logoPixels.size();
                    instructionPointer = 1;
                case 1:
                    Boolean fadeEffectFinished = game.getParameter("fadeEffectFinished", Boolean.class);
                    if (!fadeEffectFinished) {
                        break yield;
                    }
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 2;
                case 2:
                    if (System.currentTimeMillis() - waitTime < 1000) {
                        break yield;
                    }
                    instructionPointer = 3;
                case 3:
                    if (logoPixelsSpiralFinishCount > 0) {
                        break yield;
                    }
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 4;
                case 4:
                    if (System.currentTimeMillis() - waitTime < 3000) {
                        break yield;
                    }
                    instructionPointer = 5;
                case 5:
                    if (logoPixelsExplosionFinishCount > 0) {
                        break yield;
                    }
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 6;
                case 6:
                    if (System.currentTimeMillis() - waitTime < 1000) {
                        break yield;
                    }
                    game.broadcastMessage("fadeOut");
                    instructionPointer = 7;
                case 7:
                    fadeEffectFinished = game.getParameter("fadeEffectFinished", Boolean.class);
                    if (!fadeEffectFinished) {
                        break yield;
                    }
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 8;
                case 8:
                    if (System.currentTimeMillis() - waitTime < 1000) {
                        break yield;
                    }
                    instructionPointer = 9;
                case 9:
                    game.setState(PlanetLanderGame.State.OL_PRESENTS);
                    break yield;
            }
        }
    }
    
    @Override
    public void render(GameCore gc, Render render) {
        if (!visible) {
            return;
        }
        
        Arrays.fill(render.getPixels(), 0xFFFFFFFF);
        
        logoPixels.stream().forEach((pixel) -> {
            if (instructionPointer == 3 || instructionPointer == 4) {
                pixel.drawSpiral(gc, render);
            }
            else if (instructionPointer == 5) {
                pixel.drawExplosion(gc, render);
            }
        });
    }

    @Override
    public void stateChanged() {
        if (game.getState() == PlanetLanderGame.State.POWERED_BY_LIBZ) {
            visible = true;
            instructionPointer = 0;
        }
        else {
            visible = false;
        }
    }
    
}
