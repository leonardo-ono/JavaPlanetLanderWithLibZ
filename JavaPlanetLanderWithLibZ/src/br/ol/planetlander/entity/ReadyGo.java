package br.ol.planetlander.entity;

import br.ol.planetlander.PlanetLanderEntity;
import br.ol.planetlander.PlanetLanderGame;
import br.ol.planetlander.PlanetLanderGame.State;
import me.winspeednl.libz.core.GameCore;
import me.winspeednl.libz.screen.Font;
import me.winspeednl.libz.screen.Render;

/**
 * ReadyGo class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class ReadyGo extends PlanetLanderEntity {
    
    private String text;
    private Terrain terrain;
    
    public ReadyGo(PlanetLanderGame game) {
        super(game);
    }

    @Override
    public void init(GameCore gc) {
        terrain = game.getParameter("getTerrain", Terrain.class);
    }

    @Override
    public void updateReady(GameCore gc) {
        yield:
        while (true) {
            switch (instructionPointer) {
                case 0:
                    log.info("starting Ready ...");
                    game.broadcastMessage("fadeIn");
                    instructionPointer = 1;
                case 1:
                    boolean fadeEffectFinished = game.getParameter("fadeEffectFinished", Boolean.class);
                    if (!fadeEffectFinished) {
                        break yield;
                    }
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 2; 
                case 2:
                    if (System.currentTimeMillis() - waitTime < 2000) {
                        break yield;
                    }
                    game.setState(State.PLAYING);
                    break yield;
            }
        }
    }

    @Override
    public void updatePlaying(GameCore gc) {
        yield:
        while (true) {
            switch (instructionPointer) {
                case 0:
                    log.info("starting Go ...");
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 2; 
                    break yield;
                case 2:
                    if (System.currentTimeMillis() - waitTime < 500) {
                        break yield;
                    }
                    visible = false;
                    break yield;
            }
        }
    }

    @Override
    public void render(GameCore gc, Render render) {
        if (visible) {
            int textX = 0;
            int textY = (int) (gc.getHeight() * 0.50);
            int textWidth = gc.getWidth();
            int textAlign = 2;
            int textColor = 0xFFFFFFFF;
            int textBorderColor = 0xFF333355;
            game.drawText(render, text, textColor
                    , textX, (int) textY
                    , textWidth, textAlign, Font.STANDARDX2, 2, textBorderColor);

            // draw level
            textY = (int) (gc.getHeight() * 0.40);
            String level = "LEVEL " + terrain.currentLevel;
            game.drawText(render, level, textColor
                    , textX, (int) textY
                    , textWidth, textAlign, Font.STANDARDX2, 2, textBorderColor);
        }
    }
    
    // broadcast messages
    
    @Override
    public void stateChanged() {
        switch (game.getState()) {
            case READY:
                instructionPointer = 0;
                text = "READY ?";
                visible = true;
                break;
            case PLAYING:
                instructionPointer = 0;
                text = "GO !";
                visible = true;
                break;
            default:
                visible = false;
                break;
        }
    }
    
}
