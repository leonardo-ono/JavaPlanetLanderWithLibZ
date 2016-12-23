package br.ol.planetlander.entity;

import br.ol.planetlander.PlanetLanderEntity;
import br.ol.planetlander.PlanetLanderGame;
import com.sun.glass.events.KeyEvent;
import me.winspeednl.libz.core.GameCore;
import me.winspeednl.libz.screen.Font;
import me.winspeednl.libz.screen.Render;

/**
 * Title class.
 *
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Title extends PlanetLanderEntity {

    private int titleIndex;
    private double titleY;
    private boolean pushSpaceToPlayMessageVisible;
    private boolean creditsMessageVisible;
    
    public Title(PlanetLanderGame game) {
        super(game);
    }

    @Override
    public void updateTitle(GameCore gc) {
        yield:
        while (true) {
            switch (instructionPointer) {
                case 0:
                    log.info("starting Title ...");
                    game.broadcastMessage("fadeIn");
                    instructionPointer = 1;
                case 1:
                    Boolean fadeEffectFinished = game.getParameter("fadeEffectFinished", Boolean.class);
                    if (!fadeEffectFinished) {
                        break yield;
                    }
                    instructionPointer = 2;
                case 2:
                    if (titleIndex >= 6) {
                        instructionPointer = 4;
                        break yield;
                    }
                    titleIndex++;
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 3;
                case 3:
                    if (System.currentTimeMillis() - waitTime < 100) {
                        break yield;
                    }
                    instructionPointer = 2;
                    break yield;
                case 4:
                    if (titleY >= 69) {
                        instructionPointer = 5;
                        break yield;
                    }
                    titleY += (70 - titleY) * 0.1;
                    break yield;
                case 5:
                    pushSpaceToPlayMessageVisible = (int) (System.nanoTime()* 0.000000005) % 2 == 0;
                    creditsMessageVisible = true;
                    if (input.isKeyPressed(KeyEvent.VK_SPACE)) {
                        game.setState(PlanetLanderGame.State.READY);
                    }
                    break yield;
            }
        }
    }

    @Override
    public void render(GameCore gc, Render render) {
        if (!visible) {
            return;
        }
        
        String text = "";
        int textX = 0;
        int textWidth = gc.getWidth();
        int textAlign = 2;
        int textColor = 0xFFFFFFFF;
        int textBorderColor = 0xFF333355;
        
        String planetText = "PLANET".substring(0, titleIndex);
        game.drawText(render, planetText, 0xFFFFFFFF, 70, 40, 320, 1, Font.STANDARDX4, 2, textBorderColor);
        game.drawText(render, "LANDER", 0xFFFFFFFF, 130, (int) titleY, 320, 1, Font.STANDARDX4, 2, textBorderColor);
        
        // draw "push space to play" message
        if (pushSpaceToPlayMessageVisible) {
            text = "PUSH SPACE TO PLAY";
            int textY = (int) (gc.getHeight() / 1.80);
            game.drawText(render, text, textColor
                    , textX, textY
                    , textWidth, textAlign, Font.STANDARDX2, 1, textBorderColor);
        }

        // draw credits
        if (creditsMessageVisible) {
            text = "PROGRAMMED BY O.L. (C) 2016";
            int textY = (int) (gc.getHeight() * 0.85);
            game.drawText(render, text, textColor
                    , textX, textY
                    , textWidth, textAlign, Font.STANDARD, 1, textBorderColor);

            text = "POWERED BY LIBZ";
            textY = (int) (gc.getHeight() * 0.85) + 10;
            game.drawText(render, text, textColor
                    , textX, textY
                    , textWidth, textAlign, Font.STANDARD, 1, textBorderColor);
        }        
    }
    
    @Override
    public void stateChanged() {
        if (game.getState() == PlanetLanderGame.State.TITLE) {
            visible = true;
            pushSpaceToPlayMessageVisible = false;
            creditsMessageVisible = false;
            instructionPointer = 0;
            titleIndex = 0;
            titleY = - 50;
        }
        else {
            visible = false;
        }
    }
    
}
