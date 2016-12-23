package br.ol.planetlander.entity;

import br.ol.planetlander.PixelsEntityCollider;
import br.ol.planetlander.PlanetLanderEntity;
import br.ol.planetlander.PlanetLanderGame;
import br.ol.planetlander.PlanetLanderGame.State;
import com.sun.glass.events.KeyEvent;
import java.util.Arrays;
import me.winspeednl.libz.core.GameCore;
import me.winspeednl.libz.image.Sprite;

/**
 * Ship class.
 *
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Ship extends PlanetLanderEntity {
    
    public Terrain terrain;
    public LandingPlatform landingPlatform;
    
    public final double gravity = 0.01;
    public double dx, dy, vx, vy, fuel;
    public boolean[] propulsionActivated = new boolean[3]; // 0=vertical, 1=left, 2=right
    public PixelsEntityCollider landingCollider;
    public Explosion explosion;
    
    public Ship(PlanetLanderGame game) {
        super(game);
        explosion = new Explosion(game);
    }
    
    @Override
    public void init(GameCore gc) {
        w = 16;
        h = 16;
        x = 50;
        y = 50;
        pixels = new Sprite("/res/ship.png", 0, 0, w, h).pixels;
        collider = new PixelsEntityCollider(this, new Sprite("/res/ship_collision_map.png", 0, 0, w, h).pixels, w, h, 0xFFFFFFFF);
        landingCollider = new PixelsEntityCollider(this, new Sprite("/res/ship_landing_collision_map.png", 0, 0, w, h).pixels, w, h, 0xFFFFFFFF);
        terrain = game.getParameter("getTerrain", Terrain.class);
        landingPlatform = game.getParameter("getLandingPlatform", LandingPlatform.class);
    }
    
    @Override
    public void updatePlaying(GameCore gc) {
        yield:
        while (true) {
            switch (instructionPointer) {
                case 0:
                    Arrays.fill(propulsionActivated, false);
                    if (input.isKeyPressed(KeyEvent.VK_LEFT) && fuel > 0) {
                        vx -= 0.02;
                        fuel -= 0.01;
                        propulsionActivated[2] = true;
                    }
                    else if (input.isKeyPressed(KeyEvent.VK_RIGHT) && fuel > 0) {
                        vx += 0.02;
                        fuel -= 0.01;
                        propulsionActivated[1] = true;
                    }
                    else if (input.isKeyPressed(KeyEvent.VK_UP) && fuel > 0) {
                        vy -= 0.03;
                        fuel -= 0.1;
                        propulsionActivated[0] = true;
                    }
                    vy += gravity;
                    dx += vx;
                    dy += vy;
                    vx *= 0.99;
                    x = (int) dx;
                    y = (int) dy;

                    boolean outOfScreen = x < 0 || y < 0 || x > PlanetLanderGame.SCREEN_WIDTH - w || y > PlanetLanderGame.SCREEN_HEIGHT - h;
                    
                    boolean nextLevel = input.isKeyPressed(KeyEvent.VK_P);
                    
                    if (nextLevel || (collider.collides(landingPlatform.collider) 
                            && Math.abs(vx) < 0.15 && Math.abs(vy) < 0.15)) {
                        
                        Arrays.fill(propulsionActivated, false);
                        game.broadcastMessage("blinkLandingPlatform");
                        game.setState(State.CLEARED);
                    }
                    else if (outOfScreen || terrain.collider.collides(collider)) {
                        hit();
                    }
                    break yield;
            }
        }
    }

    public boolean isPropulsionActivated(int type) {
        return propulsionActivated[type];
    }
    
    // broadcast messages
    
    @Override
    public void stateChanged() {
        switch (game.getState()) {
            case READY:
                fuel = terrain.fuelStartValue;
                dx = x = terrain.shipX;
                dy = y = terrain.shipY;
                vx = vy = 0;
                Arrays.fill(propulsionActivated, false);
            case PLAYING:
                instructionPointer = 0;
            case CLEARED:
            case ENDING:
                visible = true;
                break;
            default:
                visible = false;
        }
    }

    public void hit() {
        explosion.show(x, y);
        visible = false;
        waitTime = System.currentTimeMillis();
        game.setState(State.GAME_OVER);
    }
    
    // public get parameters
    
    public Ship getShip() {
        return this;
    }

}
