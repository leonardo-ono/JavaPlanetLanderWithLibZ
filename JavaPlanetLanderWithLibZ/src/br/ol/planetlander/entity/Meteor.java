package br.ol.planetlander.entity;

import br.ol.planetlander.PixelsEntityCollider;
import br.ol.planetlander.PlanetLanderEntity;
import br.ol.planetlander.PlanetLanderGame;
import static br.ol.planetlander.PlanetLanderGame.State.TITLE;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import me.winspeednl.libz.core.GameCore;
import me.winspeednl.libz.image.Sprite;

/**
 * Meteor class.
 *
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Meteor extends PlanetLanderEntity {
    
    public double dx, dy, vx, vy;
    public Explosion explosion;
    public int type;
    public Ship ship;
    public Terrain terrain;
    
    public Meteor(PlanetLanderGame game, int type) {
        super(game);
        this.type = type;
        this.explosion = new Explosion(game);
    }
    
    @Override
    public void init(GameCore gc) {
        reset();
        int halfWidth = PlanetLanderGame.SCREEN_WIDTH / 2;
        dx = halfWidth + halfWidth * Math.random();
        dy = PlanetLanderGame.SCREEN_HEIGHT * Math.random();
        ship = game.getParameter("getShip", Ship.class);
        terrain = game.getParameter("getTerrain", Terrain.class);
        visible = true;
        try {
            BufferedImage provisoryMeteorImage = ImageIO.read(getClass().getResourceAsStream("/res/meteor_" + type + ".png"));
            w = provisoryMeteorImage.getWidth();
            h = provisoryMeteorImage.getHeight();
            pixels = new Sprite("/res/meteor_" + type + ".png", 0, 0, w, h).pixels;
            collider = new PixelsEntityCollider(this, new Sprite("/res/meteor_" + type + "_collision_map.png", 0, 0, w, h).pixels, w, h, 0xFFFFFFFF);
        } catch (IOException ex) {
            Logger.getLogger(Meteor.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }

    private void reset() {
        dx = (2 * PlanetLanderGame.SCREEN_WIDTH) * Math.random();
        dy = - 2 * h;
        vx = 0.15 + 0.25 * Math.random();
        vy = 0.15 + 0.25 * Math.random();
    }

    @Override
    public void updateTitle(GameCore gc) {
        updateInternal(gc);
    }

    @Override
    public void updatePlaying(GameCore gc) {
        updateInternal(gc);
        if (visible && collider.collides(ship.collider)) {
            ship.hit();
            showExplosion();
            reset();
        }
    }

    @Override
    public void updateCleared(GameCore gc) {
        updateInternal(gc);
    }

    @Override
    public void updateGameOver(GameCore gc) {
        updateInternal(gc);
    }
    
    public void updateInternal(GameCore gc) {
        if (!visible) {
            return;
        }
        dx -= vx;
        dy += vy;
        x = (int) dx;
        y = (int) dy;
        if (collider.collides(terrain.collider)) {
            showExplosion();
            reset();
        }
        if (x < -w || y > PlanetLanderGame.SCREEN_HEIGHT + h + 10) {
            reset();
        }
    }
    
    private void showExplosion() {
        explosion.show(x, y);
    }
    
    // broadcast messages

    @Override
    public void stateChanged() {
        switch (game.getState()) {
            case TITLE:
            case READY:
                int halfWidth = PlanetLanderGame.SCREEN_WIDTH / 2;
                dx = halfWidth + halfWidth * Math.random();
                dy = PlanetLanderGame.SCREEN_HEIGHT * Math.random();
                visible = false;
                break;
            case PLAYING:
            case GAME_OVER:
                visible = terrain.meteorsVisible;
                break;
            case CLEARED:
            case ENDING:
            default: visible = false;
        }
    }
    
}
