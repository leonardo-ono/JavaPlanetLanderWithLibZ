package leveleditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author leonardo
 */
public class Panel extends JPanel {
    
    private BufferedImage editImage;
    
    private BufferedImage viewImage;
    private BufferedImage collisionImage;
    
    private BufferedImage terrainTexture;
    private BufferedImage terrainMask;
    
    private BufferedImage starsBackground;
    
    private Graphics2D eig;
    private Graphics2D vig;
    private Graphics2D cig;
    
    private boolean dragging;
    private boolean erase;
    private Point mouse = new Point();
    
    private Point platformPosition = new Point();
    private boolean positioningPlatform;
    
    private Point shipPosition = new Point();
    
    private Point fuelPosition = new Point();
    public boolean fuelVisible = false;
    public boolean meteorsVisible = false;
    public int fuelStartValue = 100;
    
    private int mode; // 0 = edit, 1 = ship
    
    public Panel() {
        MouseHandler mouseHandler = new MouseHandler();
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
        setPreferredSize(new Dimension(640, 480));
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
    
    public void start() {
        int width = 320;
        int height = 240;
        editImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        viewImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        collisionImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        try {
            terrainTexture = ImageIO.read(getClass().getResourceAsStream("terrain.png"));
            terrainMask = ImageIO.read(getClass().getResourceAsStream("terrain_mask.png"));
        }
        catch (Exception e){
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        
        eig = (Graphics2D) editImage.getGraphics();
        eig.setBackground(Color.BLACK);
        eig.clearRect(0, 0, width, height);

        vig = (Graphics2D) viewImage.getGraphics();
        vig.setBackground(Color.BLACK);
        vig.clearRect(0, 0, width, height);

        cig = (Graphics2D) collisionImage.getGraphics();
        cig.setBackground(Color.BLACK);
        cig.clearRect(0, 0, width, height);
        
        createStarsBackground(width, height);
        
        refresh();
    }

    private void createStarsBackground(int width, int height) {
        starsBackground = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // stars background
        Graphics gs = starsBackground.getGraphics();
        gs.setColor(Color.BLACK);
        gs.fillRect(0, 0, width, height);
        for (int s=0; s<200; s++) {
            int rx = (int) (width * Math.random());
            int ry = (int) (height * Math.random());
            int t =  (int) (128 * Math.random());
            gs.setColor(new Color(t, t, t));
            gs.fillRect(rx, ry, 1, 1);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.scale(2, 2);
        
        if (editImage == null) {
            return;
        }
        
        if (mode == 0) { // edit
            if (dragging && !positioningPlatform) {
                eig.setColor(erase ? Color.BLACK : Color.WHITE);
                eig.fillOval(mouse.x - 20, mouse.y - 20, 40, 40);
            }
        }
        else if (mode == 1) { // ship
            if (dragging) {
                shipPosition.setLocation(mouse);
            }
        }
        else if (mode == 2) { // fuel
            if (dragging) {
                fuelPosition.setLocation(mouse);
            }
        }

        // landing platform
        adjustLandingPlatform();
        //vig.setColor(Color.BLUE);
        //vig.fillRect(platformPosition.x, platformPosition.y, 30, 5);
        
        g2d.drawImage(viewImage, 0, 0, null);
        // g2d.drawImage(collisionImage, 0, 0, null);
        
        // cursor
        g2d.setColor(Color.WHITE);
        g2d.drawOval(mouse.x - 20, mouse.y - 20, 40, 40);

        // ship position
        g2d.setColor(Color.GREEN);
        g2d.drawOval(shipPosition.x - 10, shipPosition.y - 10, 20, 20);

        // fuel position
        g2d.setColor(Color.YELLOW);
        g2d.drawOval(fuelPosition.x - 10, fuelPosition.y - 10, 20, 20);
        
    }
    
    private class MouseHandler extends MouseAdapter {

        @Override
        public void mouseReleased(MouseEvent e) {
            dragging = false;
            positioningPlatform = false;
            mouse.setLocation(e.getX() / 2, e.getY() / 2);
            refresh();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            dragging = false;
            mouse.setLocation(e.getX() / 2, e.getY() / 2);
            refresh();
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {
            dragging = true;
            erase = SwingUtilities.isRightMouseButton(e);
            positioningPlatform = SwingUtilities.isMiddleMouseButton(e);
            if (positioningPlatform) {
                platformPosition.setLocation(e.getX() / 2, e.getY() / 2);
                System.out.println("platform position = " + platformPosition);
            }
            mouse.setLocation(e.getX() / 2, e.getY() / 2);
            refresh();
        }
        
    }
    
    private void refresh() {
        updateViewAndCollisionImages();
        repaint();
    }

    private void updateViewAndCollisionImages() {
        //viewImage.getGraphics().clearRect(0, 0, viewImage.getWidth(), viewImage.getHeight());
        vig.drawImage(starsBackground, 0, 0, null);
        
        collisionImage.getGraphics().clearRect(0, 0, collisionImage.getWidth(), collisionImage.getHeight());
        for (int y = 0; y < viewImage.getHeight(); y += 2) {
            for (int x = 0; x < viewImage.getWidth(); x += 2) {
                if (editImage.getRGB(x, y) == 0xFFFFFFFF) {
                    floodFill(x, y);
                }
            }
        }
    }

    private void floodFill(int x, int y) {
        int tx = x % terrainTexture.getWidth();
        int ty = y % terrainTexture.getHeight();
        
                
        if (x < 0 || y < 0 || x >= viewImage.getWidth() || y >= viewImage.getHeight() || collisionImage.getRGB(x, y) == 0xFFFFFFFF) {
            return;
        }
        
        if (terrainMask.getRGB(tx, ty) != 0xFFFFFFFF) {
            return;
        }

        collisionImage.setRGB(x, y, 0xFFFFFFFF);
        
        int tc = terrainTexture.getRGB(tx, ty);
        viewImage.setRGB(x, y, tc);
        
        floodFill(x - 1, y);
        floodFill(x + 1, y);
        floodFill(x, y - 1);
        floodFill(x, y + 1);
    }
    

    public boolean isInsideTerrain(int sx, int sy, int width, int height) {
        int x1 = sx;
        int x2 = sx + width;
        int y1 = sy;
        int y2 = sy + height;
        for (int y = y1; y < y2; y++) {
            for (int x = x1; x < x2; x++) {
                if (getCollisionImageRGB(x, y) != 0xFFFFFFFF) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private int getCollisionImageRGB(int x, int y) {
        if (x < 0 || y < 0 || x >= collisionImage.getWidth() || y >= collisionImage.getHeight()) {
            return -1;
        }
        return collisionImage.getRGB(x, y);
    }


    private void removeFloodFill(int x, int y) {
        int tx = x % terrainTexture.getWidth();
        int ty = y % terrainTexture.getHeight();
                
        if (x < 0 || y < 0 || x >= collisionImage.getWidth() || y >= collisionImage.getHeight() || terrainMask.getRGB(tx, ty) != 0xFFFFFFFF || collisionImage.getRGB(x, y) != 0xFFFFFFFF) {
            return;
        }

        collisionImage.setRGB(x, y, 0xFF000000);
        
        int tc = 0xFF000000; //starsBackground.getRGB(x, y);
        viewImage.setRGB(x, y, tc);
        
        removeFloodFill(x - 1, y);
        removeFloodFill(x + 1, y);
        removeFloodFill(x, y - 1);
        removeFloodFill(x, y + 1);
    }
    

    public boolean containsTerrain(int sx, int sy, int width, int height) {
        int x1 = sx;
        int x2 = sx + width;
        int y1 = sy;
        int y2 = sy + height;
        for (int y = y1; y < y2; y++) {
            for (int x = x1; x < x2; x++) {
                if (getCollisionImageRGB(x, y) == 0xFFFFFFFF) {
                    return true;
                }
            }
        }
        return false;
    }
        
    private void adjustLandingPlatform() {
        int startY = platformPosition.y - 1;
        while (containsTerrain(platformPosition.x, startY, 30, 1) && startY > 0) {
            startY--;
        }
        
        cig.setColor(Color.BLACK);
        int clearRectHeight = platformPosition.y - startY;
        cig.fillRect(platformPosition.x, platformPosition.y - clearRectHeight, 30, clearRectHeight);
        
        vig.setColor(Color.BLACK);
        vig.fillRect(platformPosition.x, platformPosition.y - clearRectHeight, 30, clearRectHeight);
        
        for (int y=platformPosition.y - 1; y > startY; y--) {
            removeFloodFill(platformPosition.x - 1, y);
            removeFloodFill(platformPosition.x + 30, y);
        }
        
        vig.setColor(Color.BLUE);
        vig.fillRect(platformPosition.x, platformPosition.y, 30, 5);

        cig.setColor(Color.WHITE);
        cig.fillRect(platformPosition.x, platformPosition.y, 30, 5);
    }

    private String path = "/media/My Passport/Leo/apps/java/1979_LunarLander/JavaPlanetLanderWithLibZ/src/res/";
    
    public void save(int level) {
        try {
            ImageIO.write(viewImage, "png", new File(path + "level_" + level + ".png"));
            ImageIO.write(collisionImage, "png", new File(path + "level_" + level + "_collision_map.png"));
            saveLevelInfo(level);
            System.out.println("salvou !");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void saveLevelInfo(int level) throws IOException {
        Properties p = new Properties();
        p.put("level", "" + level);
        
        p.put("ship.x", "" + shipPosition.x);
        p.put("ship.y", "" + shipPosition.y);
        
        p.put("platform.x", "" + platformPosition.x);
        p.put("platform.y", "" + platformPosition.y);
        
        p.put("fuel.x", "" + fuelPosition.x);
        p.put("fuel.y", "" + fuelPosition.y);
        p.put("fuel.visible", "" + fuelVisible);
        p.put("fuel.startValue", "" + fuelStartValue);
        
        p.put("meteors.visible", "" + meteorsVisible);
        
        p.store(new FileWriter(path + "level_" + level + ".txt"), "Level " + level);
    }
    
}
