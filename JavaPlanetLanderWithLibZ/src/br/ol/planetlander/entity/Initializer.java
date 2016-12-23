package br.ol.planetlander.entity;

import br.ol.planetlander.PlanetLanderEntity;
import br.ol.planetlander.PlanetLanderGame;
import me.winspeednl.libz.core.GameCore;

/**
 * Initializer class.
 *
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Initializer extends PlanetLanderEntity {
    
    public Initializer(PlanetLanderGame game) {
        super(game);
    }
    
    @Override
    public void updateInitializing(GameCore gc) {
        yield:
        while (true) {
            switch (instructionPointer) {
                case 0:
                    log.info("Initializing ...");
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 1;
                case 1:
                    if (System.currentTimeMillis() - waitTime < 2000) {
                        break yield;
                    }
                    game.setState(PlanetLanderGame.State.POWERED_BY_LIBZ);
                    //game.setState(PlanetLanderGame.State.TITLE);
                    break yield;
            }
        }
    }

    @Override
    public void stateChanged() {
        if (game.getState() == PlanetLanderGame.State.INITIALIZING) {
            instructionPointer = 0;
        }
    }    
    
}
