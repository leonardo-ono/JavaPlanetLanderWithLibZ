package br.ol.planetlander.entity;

import br.ol.planetlander.PlanetLanderEntity;
import br.ol.planetlander.PlanetLanderGame;
import me.winspeednl.libz.core.GameCore;
import me.winspeednl.libz.image.Sprite;

/**
 * Explosion class.
 *
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Explosion extends PlanetLanderEntity {
    
    private int[][] frames;
    private long startTime;

    public Explosion(PlanetLanderGame game) {
        super(game);
    }

    @Override
    public void init(GameCore gc) {
        w = 16;
        h = 16;
        frames = new int[5][w * h];
        for (int n=0; n<5; n++) {
            frames[n] = new Sprite("/res/explosion_" 
                    + n + ".png", 0, 0, w, h).pixels;
        }
    }
    
    public void show(int x, int y) {
        this.x = x;
        this.y = y;
        startTime = System.currentTimeMillis();
    }

    @Override
    public void update(GameCore gc) {
        int frame = (int) ((System.currentTimeMillis() - startTime) / 100);
        if (frame >= 0 && frame < 5) {
            pixels = frames[frame];
            visible = true;
        }
        else {
            visible = false;
        }
    }
    
}
