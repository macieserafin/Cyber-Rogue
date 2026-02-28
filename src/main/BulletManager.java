package main;

import entity.Bullet;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;

public class BulletManager {

    private GamePanel gp;
    private ArrayList<Bullet> bullets = new ArrayList<Bullet>();

    public BulletManager(GamePanel gp) {
        this.gp = gp;
    }

    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    public void update() {

        Iterator<Bullet> iterator = bullets.iterator();

        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();

            bullet.update(gp);

            if (!bullet.alive) {
                iterator.remove();
            }
        }
    }

    public void draw(Graphics2D g2) {

        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);

            int screenX = bullet.worldX - gp.player.worldX + gp.player.screenX;
            int screenY = bullet.worldY - gp.player.worldY + gp.player.screenY;

            bullet.draw(g2, screenX, screenY);
        }
    }
}