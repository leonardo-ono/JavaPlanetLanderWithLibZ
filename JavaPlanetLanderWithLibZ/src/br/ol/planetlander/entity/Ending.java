package br.ol.planetlander.entity;

import br.ol.planetlander.PlanetLanderEntity;
import br.ol.planetlander.PlanetLanderGame;
import br.ol.planetlander.PlanetLanderGame.State;
import static br.ol.planetlander.PlanetLanderGame.State.ENDING;
import com.sun.glass.events.KeyEvent;
import me.winspeednl.libz.core.GameCore;
import me.winspeednl.libz.screen.Font;
import me.winspeednl.libz.screen.Render;

/**
 * Ending class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Ending extends PlanetLanderEntity {
    
    private int currentIndex;
    private final int[] letterIndex = new int[3];
    private final String[] messageText =  { "CONGRATULATIONS :) !"
            , "YOU HAVE CLEARED ALL AVAILABLE LEVELS !"
            , "THANKS FOR PLAYING :) !" };

    public Ending(PlanetLanderGame game) {
        super(game);
    }

    @Override
    public void updateEnding(GameCore gc) {
        yield:
        while (true) {
            switch (instructionPointer) {
                case 0:
                    log.info("starting Ending ...");
                    waitTime = System.currentTimeMillis();
                    currentIndex = 0;
                    instructionPointer = 1; 
                case 1:
                    if (System.currentTimeMillis() - waitTime < 1000) {
                        break yield;
                    }
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 2; 
                case 2:
                    if (System.currentTimeMillis() - waitTime < 100) {
                        break yield;
                    }
                    waitTime = System.currentTimeMillis();
                    letterIndex[currentIndex]++;
                    if (letterIndex[currentIndex] == messageText[currentIndex].length()) {
                        currentIndex++;
                        if (currentIndex > 2) {
                            instructionPointer = 3;
                        }
                        else {
                            instructionPointer = 1;
                        }
                        waitTime = System.currentTimeMillis();
                    }
                    break yield;
                case 3:
                    if (System.currentTimeMillis() - waitTime < 3000) {
                        break yield;
                    }
                    instructionPointer = 4;
                case 4:
                    if (gc.getInput().isKeyPressed(KeyEvent.VK_SPACE)) {
                        instructionPointer = 5;
                    }
                    if (System.currentTimeMillis() - waitTime < 10000) {
                        break yield;
                    }
                    instructionPointer = 5;
                    break yield;
                case 5:
                    game.setState(State.INITIALIZING);
                    break yield;
            }
        }
    }

    @Override
    public void render(GameCore gc, Render render) {
        if (!visible) {
            return;
        }
        
        int textX = 0;
        int textY = 0;
        int textWidth = gc.getWidth();
        int textAlign = 2;
        int textColor = 0xFFFFFFFF;
        int textBorderColor = 0xFF333355;

        textY = (int) (gc.getHeight() * 0.20);
        game.drawText(render, messageText[0].substring(0, letterIndex[0]), textColor
                , textX, textY
                , textWidth, textAlign, Font.STANDARDX2, 2, textBorderColor);

        textY = (int) (gc.getHeight() * 0.40);
        game.drawText(render, messageText[1].substring(0, letterIndex[1]), textColor
                , textX, textY
                , textWidth, textAlign, Font.STANDARD, 2, textBorderColor);

        textY = (int) (gc.getHeight() * 0.50);
        game.drawText(render, messageText[2].substring(0, letterIndex[2]), textColor
                , textX, textY
                , textWidth, textAlign, Font.STANDARD, 2, textBorderColor);
    }
    
    // broadcast messages
    
    @Override
    public void stateChanged() {
        visible = game.getState() == ENDING;
        if (game.getState() == ENDING) {
            instructionPointer = 0;
            letterIndex[0] = 0;
            letterIndex[1] = 0;
            letterIndex[2] = 0;
        }
    }
    
}
