package br.ol.planetlander.entity;

import br.ol.planetlander.PlanetLanderEntity;
import br.ol.planetlander.PlanetLanderGame;
import static br.ol.planetlander.PlanetLanderGame.State.CLEARED;
import me.winspeednl.libz.core.GameCore;
import me.winspeednl.libz.screen.Font;
import me.winspeednl.libz.screen.Render;

/**
 * LevelCleared class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class LevelCleared extends PlanetLanderEntity {
    
    private String text = "";
    private Terrain terrain;
    
    public LevelCleared(PlanetLanderGame game) {
        super(game);
    }

    @Override
    public void init(GameCore gc) {
        terrain = game.getParameter("getTerrain", Terrain.class);
    }
    
    @Override
    public void updateCleared(GameCore gc) {
        yield:
        while (true) {
            switch (instructionPointer) {
                case 0:
                    log.info("starting LevelCleared ...");
                    visible = false;
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 1; 
                case 1:
                    if (System.currentTimeMillis() - waitTime < 2000) {
                        break yield;
                    }
                    visible = true;
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 2; 
                case 2:
                    if (System.currentTimeMillis() - waitTime < 3000) {
                        break yield;
                    }
                    if (!terrain.hasMoreLevels()) {
                        waitTime = System.currentTimeMillis();
                        instructionPointer = 4;
                        break yield;
                    }
                    game.broadcastMessage("fadeOut");
                    instructionPointer = 3;
                case 3:
                    boolean fadeEffectFinished = game.getParameter("fadeEffectFinished", Boolean.class);
                    if (!fadeEffectFinished) {
                        break yield;
                    }
                    visible = false;
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 4; 
                case 4:
                    if (System.currentTimeMillis() - waitTime < 1000) {
                        break yield;
                    }
                    instructionPointer = 5;
                case 5:
                    game.broadcastMessage("startNextLevel");
                    break yield;
            }
        }
    }
    
    @Override
    public void render(GameCore gc, Render render) {
        if (visible) {
            int textX = 0;
            int textY = (int) (gc.getHeight() * 0.45);
            int textWidth = gc.getWidth();
            int textAlign = 2;
            int textColor = 0xFFFFFFFF;
            int textBorderColor = 0xFF333355;
            game.drawText(render, text, textColor
                    , textX, (int) textY
                    , textWidth, textAlign, Font.STANDARDX2, 2, textBorderColor);
        }
    }
    
    // received broadcast messages
    
    @Override
    public void stateChanged() {
        visible = game.getState() == CLEARED;
        if (visible) {
            text = "LEVEL " + terrain.currentLevel + " CLEARED :) !";
            instructionPointer = 0;
        }
    }
    
}
