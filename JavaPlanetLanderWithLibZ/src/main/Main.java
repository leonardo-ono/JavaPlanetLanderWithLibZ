package main;

import br.ol.planetlander.PlanetLanderGame;
import me.winspeednl.libz.core.GameCore;

/**
 *
 * @author leonardo
 */
public class Main {
    
    public static void main(String[] args) {
        GameCore gc = new GameCore(new PlanetLanderGame());
        gc.setTitle("Planet Lander - Powered by LibZ");
        gc.setScale(2);
        gc.setWidth(PlanetLanderGame.SCREEN_WIDTH);
        gc.setHeight(PlanetLanderGame.SCREEN_HEIGHT);
        gc.start();
    }
    
}
