package br.ol.planetlander;

import br.ol.planetlander.entity.Ending;
import br.ol.planetlander.entity.FadeEffect;
import br.ol.planetlander.entity.GameOver;
import br.ol.planetlander.entity.HUD;
import br.ol.planetlander.entity.Initializer;
import br.ol.planetlander.entity.LandingPlatform;
import br.ol.planetlander.entity.LevelCleared;
import br.ol.planetlander.entity.Meteor;
import br.ol.planetlander.entity.OLPresents;
import br.ol.planetlander.entity.PoweredByLibZ;
import br.ol.planetlander.entity.ReadyGo;
import br.ol.planetlander.entity.Ship;
import br.ol.planetlander.entity.ShipPropulsion;
import br.ol.planetlander.entity.Terrain;
import br.ol.planetlander.entity.Title;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import me.winspeednl.libz.core.GameCore;
import me.winspeednl.libz.core.LibZ;
import me.winspeednl.libz.screen.Font;
import me.winspeednl.libz.screen.Render;

/**
 * This game was implemented using LibZ game library 
 * that was developed by winspeednl.
 *
 * You can getParameter LibZ from: https://github.com/winspeednl/LibZ
 *
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class PlanetLanderGame extends LibZ {
    
    public static final int SCREEN_WIDTH = 320;
    public static final int SCREEN_HEIGHT = 240;

    public static enum State { INITIALIZING, POWERED_BY_LIBZ, OL_PRESENTS, TITLE, READY, PLAYING, CLEARED, GAME_OVER, ENDING }
    
    private GameCore gameCore;
    private final List<PlanetLanderEntity> entities = new ArrayList<>();
    
    private State state = State.INITIALIZING;
    
    public PlanetLanderGame() {
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        if (this.state != state) {
            this.state = state;
            broadcastMessage("stateChanged");
        }
    }

    public GameCore getGameCore() {
        return gameCore;
    }

    @Override
    public void init(GameCore gc) {
        this.gameCore = gc;
        entities.add(new Initializer(this));
        entities.add(new PoweredByLibZ(this));
        entities.add(new OLPresents(this));
        entities.add(new Terrain(this));
        entities.add(new LandingPlatform(this));
        
        for (int i=0; i<30; i++) {
            Meteor meteor = new Meteor(this, i % 5);
            entities.add(meteor);
            entities.add(meteor.explosion);
        }
        
        entities.add(new Ship(this));
        entities.add(new ShipPropulsion(this, 0));
        entities.add(new ShipPropulsion(this, 1));
        entities.add(new ShipPropulsion(this, 2));
        entities.add(getParameter("getShip", Ship.class).explosion);
        entities.add(new Title(this));
        entities.add(new ReadyGo(this));
        entities.add(new LevelCleared(this));
        entities.add(new GameOver(this));
        entities.add(new Ending(this));
        entities.add(new HUD(this));
        entities.add(new FadeEffect(this));
        
        entities.stream().forEach((entity) -> {
            entity.init(gc);
        });
    }

    @Override
    public void update(GameCore gc) {
        entities.stream().forEach((entity) -> {
            entity.update(gc);
        });
    }

    @Override
    public void render(GameCore gc, Render render) {
        entities.stream().forEach((entity) -> {
            entity.render(gc, render);
        });
    }

    public void drawText(Render render, String text, int color, int offX, int offY, Font font){
            text = text.toUpperCase();
            int offset = 0;
            for (int i = 0; i < text.length(); i++) {
                    int unicode = text.codePointAt(i) - 32;
                    for (int x = 0; x < font.widths[unicode]; x++) {
                            for (int y = 1; y < font.image.height; y++) {
                                    if (font.image.imagePixels[(x + font.offsets[unicode]) + y * font.image.width] == 0xFFFFFFFF)
                                            render.setPixel(x + offX + offset, y + offY - 1, color);
                            }
                    }
                    offset += font.widths[unicode];
            }
    }
    
    // draw text. align 1=left, 2=center, 3=right
    public void drawText(Render render, String text, int color
            , int x, int y, int w, int align, Font font) {
        
        int textWidth = render.getStringWidth(text, font);
        int tx = x; // left alignment
        switch (align) {
            case 2: tx = x + w / 2 - textWidth / 2; break;
            case 3: tx = x + w - textWidth; break;
        }
        drawText(render, text, color, tx, y, font);        
    }

    public void drawText(Render render, String text, int color, int x, int y
            , int w, int align, Font font, int thickness, int borderColor) {
        
        for (int dy=-thickness; dy<=thickness; dy++) {
            for (int dx=-thickness; dx<=thickness; dx++) {
                drawText(render, text, borderColor, x + dx, y + dy, w, align, font);
            }
        }
        drawText(render, text, color, x, y, w, align, font);
    }
    	    
    public void broadcastMessage(String message) {
        entities.forEach((entity) -> {
            try {
                Method method = entity.getClass().getMethod(message);
                if (method != null) {
                    method.invoke(entity);
                }
            } catch (Exception ex) {
            }
        });
    }

    public <T> T getParameter(String message, Class<T> returnType) {
        for (PlanetLanderEntity entity : entities) {
            try {
                Method method = entity.getClass().getMethod(message);
                if (method != null) {
                    Object r = method.invoke(entity);
                    return returnType.cast(r);
                }
            } catch (Exception ex) {
            }
        };
        return null;
    }
    
}
