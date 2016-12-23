package br.ol.planetlander;

/**
 * PixelsEntityCollider class.
 * 
 * Pixels based collision detection.
 *
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class PixelsEntityCollider {

    private final PlanetLanderEntity entity;
    private final int[] data;
    private final int width;
    private final int height;
    private final int collisionColor;
    
    public PixelsEntityCollider(PlanetLanderEntity entity, int[] collisionData
            , int width, int height, int collisionColor) {
        
        this.entity = entity;
        this.data = collisionData;
        this.width = width;
        this.height = height;
        this.collisionColor = collisionColor;
    }
    
    public int getX() {
        return entity.x;
    }
    
    public int getY() {
        return entity.y;
    }
    
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDataValue(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return collisionColor + 1;
        }
        return data[x + y * width];
    }

    public boolean isCollisionPixel(int sx, int sy) {
        int x = sx - entity.x;
        int y = sy - entity.y;
        return getDataValue(x, y) == collisionColor;
    }
    
    public boolean collides(PixelsEntityCollider collider) {
        int thisArea = getWidth() * getHeight();
        int colliderArea = collider.getWidth() * collider.getHeight();
        PixelsEntityCollider collider1 = this;
        PixelsEntityCollider collider2 = collider;
        if (colliderArea < thisArea) {
            collider1 = collider;
            collider2 = this;
        }
        
        int x1 = collider1.getX();
        int x2 = x1 + collider1.getWidth();
        int y1 = collider1.getY();
        int y2 = y1 + collider1.getHeight();
        for (int y = y1; y < y2; y++) {
            for (int x = x1; x < x2; x++) {
                if (collider1.isCollisionPixel(x, y) && collider2.isCollisionPixel(x, y)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isInside(PixelsEntityCollider collider) {
        int x1 = getX();
        int x2 = x1 + getWidth();
        int y1 = getY();
        int y2 = y1 + getHeight();
        for (int y = y1; y < y2; y++) {
            for (int x = x1; x < x2; x++) {
                if (isCollisionPixel(x, y) && !collider.isCollisionPixel(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }
    
}
