package br.ol.planetlander.entity;

import br.ol.planetlander.PlanetLanderEntity;
import br.ol.planetlander.PlanetLanderGame;
import me.winspeednl.libz.core.GameCore;
import me.winspeednl.libz.screen.Font;
import me.winspeednl.libz.screen.Render;

/**
 * HUD class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class HUD extends PlanetLanderEntity {
    
    private final String fuelMessage = "FUEL:";
    private Ship ship;
    private Terrain terrain;
    
    public HUD(PlanetLanderGame game) {
        super(game);
    }

    @Override
    public void init(GameCore gc) {
        x = 32;
        y = 10;
        h = 6;
        ship = game.getParameter("getShip", Ship.class);
        terrain = game.getParameter("getTerrain", Terrain.class);
    }

    @Override
    public void render(GameCore gc, Render render) {
        if (!visible) {
            return;
        }
        String currentLevel = "LEVEL: " + terrain.currentLevel;
        game.drawText(render, currentLevel, 0xFFFFFFFF, 0, 10, PlanetLanderGame.SCREEN_WIDTH - 10, 3, Font.STANDARD);
        game.drawText(render, fuelMessage, 0xFFFFFFFF, 10, 10, Font.STANDARD);
        for (int dx = 0; dx < ship.fuel; dx++) {
            for (int dy = 0; dy < h; dy++) {
                render.setPixel(x + dx, y + dy, 0xFFFF0000);
            }
        }
        render.drawRect(x - 1, y - 1, 102, 6, 0xFFFFFFFF, 1, false);
    }
    
    // broadcast messages
    
    @Override
    public void stateChanged() {
        switch (game.getState()) {
            case READY:
            case PLAYING:
            case CLEARED:
            case GAME_OVER:
                visible = true;
                break;
            default:
                visible = false;
        }
    }    

}
