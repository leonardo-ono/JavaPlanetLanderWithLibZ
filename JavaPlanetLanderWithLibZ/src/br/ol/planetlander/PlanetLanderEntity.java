package br.ol.planetlander;

import me.winspeednl.libz.core.GameCore;
import me.winspeednl.libz.core.Input;
import me.winspeednl.libz.entities.LibZ_Entity;
import me.winspeednl.libz.logger.Logger;
import me.winspeednl.libz.screen.Render;

/**
 * PlanetLanderEntity abstract class.
 *
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public abstract class PlanetLanderEntity extends LibZ_Entity {

    public GameCore gc;
    public PlanetLanderGame game;
    public int[] pixels;
    public PixelsEntityCollider collider;
    public boolean visible;
    protected final Logger log;
    protected int instructionPointer = 0;
    protected long waitTime;
    protected Input input;

    public PlanetLanderEntity(PlanetLanderGame game) {
        this.game = game;
        this.gc = game.getGameCore();
        this.input = gc.getInput();
        log = new Logger(getClass().getName());
    }
    
    public void init(GameCore gc) {
    }
    
    @Override
    public void update(GameCore gc) {
        preUpdate(gc);
        switch (game.getState()) {
            case INITIALIZING: updateInitializing(gc); break;
            case POWERED_BY_LIBZ: updatePoweredByLibZ(gc); break;
            case OL_PRESENTS: updateOLPresents(gc); break;
            case TITLE: updateTitle(gc); break;
            case READY: updateReady(gc); break;
            case PLAYING: updatePlaying(gc); break;
            case CLEARED: updateCleared(gc); break;
            case GAME_OVER: updateGameOver(gc); break;
            case ENDING: updateEnding(gc); break;
        }
        posUpdate(gc);
    }

    public void preUpdate(GameCore gc) {
    }

    public void updateInitializing(GameCore gc) {
    }

    public void updatePoweredByLibZ(GameCore gc) {
    }

    public void updateOLPresents(GameCore gc) {
    }

    public void updateTitle(GameCore gc) {
    }

    public void updateReady(GameCore gc) {
    }

    public void updatePlaying(GameCore gc) {
    }

    public void updateCleared(GameCore gc) {
    }

    public void updateGameOver(GameCore gc) {
    }

    public void updateEnding(GameCore gc) {
    }

    public void posUpdate(GameCore gc) {
    }
    
    @Override
    public void render(GameCore gc, Render render) {
        if (!visible) {
            return;
        }
        for (int dx = 0; dx < w; dx++) {
            for (int dy = 0; dy < h; dy++) {
                int p = pixels[dx + dy * w];
                if ((p & 0xFF000000) == 0xFF000000) {
                    render.setPixel(x + dx, y + dy, p);
                }
            }
        }
    }

    // broadcasted messages
    
    public void stateChanged() {
    }
    
}
