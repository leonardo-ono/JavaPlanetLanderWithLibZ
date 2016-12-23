package br.ol.planetlander.entity;

import br.ol.planetlander.PlanetLanderEntity;
import br.ol.planetlander.PlanetLanderGame;
import me.winspeednl.libz.core.GameCore;
import me.winspeednl.libz.image.Sprite;

/**
 * ShipPropulsion class.
 *
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class ShipPropulsion extends PlanetLanderEntity {
    
    private Ship ship;
    private int[][] frames;
    private int type;
    private String name;
    private int offsetX;
    private int offsetY;
    
    // type: 0=vertical, 1=left, 2=right
    public ShipPropulsion(PlanetLanderGame game, int type) {
        super(game);
        this.type = type;
        switch (type) {
            case 0:
                name = "vertical";
                offsetX = 5;
                offsetY = 14;
                w = 6;
                h = 4;
                break;
            case 1:
                name = "left";
                offsetX = -3;
                offsetY = 2;
                w = 4;
                h = 6;
                break;
            case 2:
                name = "right";
                offsetX = 15;
                offsetY = 2;
                w = 4;
                h = 6;
                break;
        }
    }

    @Override
    public void init(GameCore gc) {
        frames = new int[2][w * h];
        for (int n=0; n<2; n++) {
            frames[n] = new Sprite("/res/propulsion_" + name 
                    + "_" + n + ".png", 0, 0, w, h).pixels;
        }
        pixels = frames[0];
        ship = game.getParameter("getShip", Ship.class);
    }

    @Override
    public void update(GameCore gc) {
        visible = ship.visible && ship.isPropulsionActivated(type);
        x = ship.x + offsetX;
        y = ship.y + offsetY;
        int frame = (int) (System.nanoTime() / 100000000) % 2;
        pixels = frames[frame];
    }
    
}
