package br.ol.planetlander.entity;

import br.ol.planetlander.PixelsEntityCollider;
import br.ol.planetlander.PlanetLanderEntity;
import br.ol.planetlander.PlanetLanderGame;
import me.winspeednl.libz.core.GameCore;
import me.winspeednl.libz.image.Sprite;

/**
 *
 * @author leonardo
 */
public class LandingPlatform extends PlanetLanderEntity {
    
    private Terrain terrain;
    private int[][] frames;
    private long blinkStartTime;

    public LandingPlatform(PlanetLanderGame game) {
        super(game);
    }

    @Override
    public void init(GameCore gc) {
        x = 150;
        y = 150;
        w = 30;
        h = 5;
        frames = new int[5][w * h];
        for (int n=0; n<4; n++) {
            frames[n] = new Sprite("/res/landingPlatform_" + n + ".png", 0, 0, w, h).pixels;
        }
        pixels = frames[0];
        collider = new PixelsEntityCollider(this, new Sprite("/res/landingPlatform_collision_map.png", 0, 0, w, h).pixels, w, h, 0xFFFFFFFF);
        terrain = game.getParameter("getTerrain", Terrain.class);
    }

    @Override
    public void update(GameCore gc) {
        if (System.currentTimeMillis() - blinkStartTime < 3000) {
            int frame = (int) (System.nanoTime() / 100000000) % 2;
            pixels = frames[frame];
        }
        else {
            int frame = (int) (System.nanoTime() / 1000000000) % 2;
            pixels = frames[frame + 2];
        }
    }

    // broadcast messages
    
    @Override
    public void stateChanged() {
        switch (game.getState()) {
            case TITLE:
            case READY:
                x = terrain.platformX;
                y = terrain.platformY;
            case PLAYING:
            case CLEARED:
            case GAME_OVER:
            case ENDING:
                visible = true;
                break;
            default: visible = false;
        }
    }

    public void blinkLandingPlatform() {
        blinkStartTime = System.currentTimeMillis();
    }
    
    // public get parameters
    
    public LandingPlatform getLandingPlatform() {
        return this;
    }
    
}
