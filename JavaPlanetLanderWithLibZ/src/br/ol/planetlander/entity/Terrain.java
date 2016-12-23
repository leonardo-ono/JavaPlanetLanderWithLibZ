package br.ol.planetlander.entity;

import br.ol.planetlander.PixelsEntityCollider;
import br.ol.planetlander.PlanetLanderEntity;
import br.ol.planetlander.PlanetLanderGame;
import static br.ol.planetlander.PlanetLanderGame.State.*;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.winspeednl.libz.core.GameCore;
import me.winspeednl.libz.image.Sprite;

/**
 * Terrain class.
 *
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Terrain extends PlanetLanderEntity {
    
    public static final int LAST_LEVEL = 3;
    public int currentLevel;
    
    public int platformX;
    public int platformY;

    public int shipX;
    public int shipY;

    public int fuelX;
    public int fuelY;
    public int fuelStartValue;
    public boolean fuelVisible;
    
    public boolean meteorsVisible;
    
    public Terrain(PlanetLanderGame game) {
        super(game);
    }

    @Override
    public void init(GameCore gc) {
        x = 0;
        y = 0;
        w = 320;
        h = 240;
    }

    public void loadLevel(int level) {
        pixels = new Sprite("/res/level_" + level + ".png", 0, 0, w, h).pixels;
        collider = new PixelsEntityCollider(this, new Sprite("/res/level_" + level + "_collision_map.png", 0, 0, w, h).pixels, w, h, 0xFFFFFFFF);
        Properties p = new Properties();
        try {
            p.load(getClass().getResourceAsStream("/res/level_" + level + ".txt"));
            shipX = Integer.parseInt(p.getProperty("ship.x"));
            shipY = Integer.parseInt(p.getProperty("ship.y"));
            platformX = Integer.parseInt(p.getProperty("platform.x"));
            platformY = Integer.parseInt(p.getProperty("platform.y"));
            fuelX = Integer.parseInt(p.getProperty("fuel.x"));
            fuelY = Integer.parseInt(p.getProperty("fuel.y"));
            fuelVisible = Boolean.parseBoolean(p.getProperty("fuel.visible"));
            fuelStartValue = Integer.parseInt(p.getProperty("fuel.startValue"));
            meteorsVisible = Boolean.parseBoolean(p.getProperty("meteors.visible"));
        } catch (IOException ex) {
            Logger.getLogger(Terrain.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
        
        if (meteorsVisible) {
            game.broadcastMessage("showAllMeteors");
        }
        else {
            game.broadcastMessage("hideAllMeteors");
        }
    }

    // broadcast messages
    
    @Override
    public void stateChanged() {
        switch (game.getState()) {
            case TITLE:
                currentLevel = 1;
                loadLevel((int) (LAST_LEVEL * Math.random()) + 1);
                visible = true;
                break;
            case READY:
                loadLevel(currentLevel);
            case PLAYING:
            case CLEARED:
            case GAME_OVER:
            case ENDING:
                visible = true;
                break;
            default: visible = false;
        }
    }

    public void startNextLevel() {
        if (currentLevel == LAST_LEVEL) {
            game.setState(ENDING);
        }
        else {
            currentLevel++;
            loadLevel(currentLevel);
            game.setState(READY);
        }
    }
    
    // public get parameters
    
    public Terrain getTerrain() {
        return this;
    }

    public boolean hasMoreLevels() {
        return currentLevel < LAST_LEVEL;
    }
    
}
