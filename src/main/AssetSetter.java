package main;

import entity.Player;
import object.Chest;
import object.Door;
import object.Key;

public class AssetSetter {

    GamePanel gp;
    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

        public void setObject(){

            gp.obj[0] = new Key();
            gp.obj[0].worldX = 23 * gp.tileSize;
            gp.obj[0].worldY = 7 * gp.tileSize;

            gp.obj[1] = new Key();
            gp.obj[1].worldX = 35 * gp.tileSize;
            gp.obj[1].worldY = 24 * gp.tileSize;

            gp.obj[2] = new Chest();
            gp.obj[2].worldX = 25 * gp.tileSize;
            gp.obj[2].worldY = 10 * gp.tileSize;

            gp.obj[3] = new Door();
            gp.obj[3].worldX = 32 * gp.tileSize;
            gp.obj[3].worldY = 23 * gp.tileSize;

        }
}
