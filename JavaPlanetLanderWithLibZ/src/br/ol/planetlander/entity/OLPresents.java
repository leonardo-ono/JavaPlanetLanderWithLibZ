package br.ol.planetlander.entity;

import br.ol.planetlander.PlanetLanderEntity;
import br.ol.planetlander.PlanetLanderGame;
import me.winspeednl.libz.core.GameCore;
import me.winspeednl.libz.screen.Font;
import me.winspeednl.libz.screen.Render;

/**
 * OLPresents class.
 *
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class OLPresents extends PlanetLanderEntity {
    
    public OLPresents(PlanetLanderGame game) {
        super(game);
    }

    @Override
    public void updateOLPresents(GameCore gc) {
        yield:
        while (true) {
            switch (instructionPointer) {
                case 0:
                    log.info("starting OLPresents ...");
                    game.broadcastMessage("fadeIn");
                    instructionPointer = 1;
                case 1:
                    Boolean fadeEffectFinished = game.getParameter("fadeEffectFinished", Boolean.class);
                    if (!fadeEffectFinished) {
                        break yield;
                    }
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 2;
                case 2:
                    if (System.currentTimeMillis() - waitTime < 3000) {
                        break yield;
                    }
                    instructionPointer = 3;
                case 3:
                    game.broadcastMessage("fadeOut");
                    instructionPointer = 4;
                case 4:
                    fadeEffectFinished = game.getParameter("fadeEffectFinished", Boolean.class);
                    if (!fadeEffectFinished) {
                        break yield;
                    }
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 5;
                case 5:
                    if (System.currentTimeMillis() - waitTime < 1000) {
                        break yield;
                    }
                    game.setState(PlanetLanderGame.State.TITLE);
                    break yield;
            }
        }
    }

    @Override
    public void render(GameCore gc, Render render) {
        if (!visible) {
            return;
        }
        game.drawText(render, "O.L. PRESENTS", 0xFFFFFFFF, 0, 105, 320, 2, Font.STANDARDX2, 2, 0xFF333355);
    }
    
    @Override
    public void stateChanged() {
        if (game.getState() == PlanetLanderGame.State.OL_PRESENTS) {
            visible = true;
            instructionPointer = 0;
        }
        else {
            visible = false;
        }
    }
    
}
