package main;

import object.Key;

import java.awt.*;
import java.awt.image.BufferedImage;

public class UI {

    GamePanel gp;
    Graphics2D g2;
    Font arial_40;
    Font consolas_18;
    BufferedImage keyImage;

    public boolean messageOn = false;
    public String message = "";
    int messageCounter = 0;

    public String pauseMessage = "";


    public UI(GamePanel gp) {
        this.gp = gp;

        arial_40 = new Font("Arial", Font.PLAIN, 20);
        consolas_18 = new Font("Consolas", Font.PLAIN, 18);

        Key key = new Key();
        keyImage = key.image;
    }

    public void showMessage(String s) {
        message = s;
        messageOn = true;
    }

    public void draw(Graphics g2) {
        this.g2 = (Graphics2D) g2;

        g2.setFont(consolas_18);
        g2.setColor(Color.WHITE);
        g2.drawImage(keyImage, 115, 33, 20, 20, null);
        g2.drawString("Keys = " + gp.player.hasKey, 30, 50);
        g2.drawString("Combat mode (Q): " + gp.player.combatState, 30, 30);
        g2.drawString("World position = x: " + gp.player.worldX + " y:" + gp.player.worldY, 400, 30);



        if (messageOn) {
            g2.drawString(message, 300, 250);

            messageCounter++;

            if (messageCounter > 60) {
                messageCounter = 0;
                messageOn = false;
            }
        }

        if (gp.gameState == GameState.PAUSED) {
            pauseMessage = "Paused";
            g2.drawString(pauseMessage, 300, 250);
        }
        if (gp.gameState == GameState.PLAYING) {
            pauseMessage = "";

        }


    }
}
