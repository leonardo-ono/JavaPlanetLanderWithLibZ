package br.ol.planetlander.entity;

import br.ol.planetlander.PlanetLanderEntity;
import br.ol.planetlander.PlanetLanderGame;
import br.ol.planetlander.PlanetLanderGame.State;
import static br.ol.planetlander.PlanetLanderGame.State.GAME_OVER;
import me.winspeednl.libz.core.GameCore;
import me.winspeednl.libz.screen.Font;
import me.winspeednl.libz.screen.Render;

/**
 * GameOver class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class GameOver extends PlanetLanderEntity {
    
    private final String gameOverText = "GAME OVER";
    private int letterIndex = 0;

    public GameOver(PlanetLanderGame game) {
        super(game);
    }

    @Override
    public void updateGameOver(GameCore gc) {
        yield:
        while (true) {
            switch (instructionPointer) {
                case 0:
                    log.info("starting game over ...");
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 1; 
                    break yield;
                case 1:
                    if (System.currentTimeMillis() - waitTime < 2000) {
                        break yield;
                    }
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 2; 
                    break yield;
                case 2:
                    if (System.currentTimeMillis() - waitTime < 200) {
                        break yield;
                    }
                    waitTime = System.currentTimeMillis();
                    letterIndex++;
                    if (letterIndex == gameOverText.length()) {
                        waitTime = System.currentTimeMillis();
                        instructionPointer = 3;
                    }
                    break yield;
                case 3:
                    if (System.currentTimeMillis() - waitTime < 5000) {
                        break yield;
                    }
                    instructionPointer = 4;
                case 4:
                    game.broadcastMessage("fadeOut");
                    instructionPointer = 5;
                case 5:
                    boolean fadeEffectFinished = game.getParameter("fadeEffectFinished", Boolean.class);
                    if (!fadeEffectFinished) {
                        break yield;
                    }
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 6;
                case 6:
                    if (System.currentTimeMillis() - waitTime < 1000) {
                        break yield;
                    }
                    game.setState(State.TITLE);
                    instructionPointer = 7;
                case 7:
                    break yield;
            }
        }
    }

    @Override
    public void render(GameCore gc, Render render) {
        if (visible) {
            int textX = 0;
            int textY = (int) (gc.getHeight() * 0.5) - 20;
            int textWidth = gc.getWidth();
            int textAlign = 2;
            int textColor = 0xFFFFFFFF;
            int textBorderColor = 0xFF333355;
            game.drawText(render, gameOverText.substring(0, letterIndex), textColor
                    , textX, textY
                    , textWidth, textAlign, Font.STANDARDX4, 2, textBorderColor);
        }
    }
    
    // received broadcast messages
    
    @Override
    public void stateChanged() {
        visible = game.getState() == GAME_OVER;
        if (game.getState() == GAME_OVER) {
            instructionPointer = 0;
            letterIndex = 0;
        }
    }
    
}
