package entity;

import main.GamePanel;
import main.KeyHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Player extends Entity {

    GamePanel gp;
    KeyHandler keyH;

    public final int screenX;
    public final int screenY;

    private double velX = 0.0;
    private double velY = 0.0;

    private double accel = 0.69;
    private double maxSpeed = 4;
    private double friction = 0.69; // im bliżej 1.0 tym dłużej się ślizga

    private double worldXf;
    private double worldYf;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;

        screenX = gp.screenWidth/2 - (gp.tileSize/2);
        screenY = gp.screenHeight/2- (gp.tileSize/2);

        solidArea = new Rectangle(8 , 16, 32, 32); //hitbox

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues(){

        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;

        worldXf = worldX;
        worldYf = worldY;

        speed = 4;

        direction = "idle";
    }

    public void getPlayerImage() {
        try {
            up1 = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("player/player_up_1.png")));
            up2 = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("player/player_up_2.png")));
            down1 = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("player/player_down_1.png")));
            down2 = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("player/player_down_2.png")));
            left1 = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("player/player_left_1.png")));
            left2 = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("player/player_left_2.png")));
            right1 = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("player/player_right_1.png")));
            right2 = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("player/player_right_2.png")));

            idle1 = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("player/player_idle_1.png")));
            idle2 = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("player/player_idle_2.png")));

            upShoot    = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("player/player_up_combat.png")));
            downShoot  = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("player/player_down_combat.png")));
            leftShoot  = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("player/player_left_combat.png")));
            rightShoot = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("player/player_right_combat.png")));



        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void update() {

        boolean movingInput = keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed;

        //UPDATE DIRECTION FROM VELOCITY

        //Math.abs() -> wartość bezwzględną liczby |x|
        if (Math.abs(velX) < 0.05 && Math.abs(velY) < 0.05) {
            direction = "idle";
        }
        else {
            if (Math.abs(velX) > Math.abs(velY)) {
                if (velX > 0) {
                    direction = "right";
                }
                else if (velX < 0) {
                    direction = "left";
                }
            }
            else {
                if (velY > 0) {
                    direction = "down";
                }
                else if (velY < 0) {
                    direction = "up";
                }
            }
        }

        collisionOn = false;
        gp.cChecker.checkTile(this);


        // walidacja OPPOSITE DIRECTION BRAKING

        if (keyH.leftPressed && !keyH.rightPressed) {
            if (velX > 0) {
                velX = 0;
            }
        }
        else if (keyH.rightPressed && !keyH.leftPressed) {
            if (velX < 0) {
                velX = 0;
            }
        }

        if (keyH.upPressed && !keyH.downPressed) {
            if (velY > 0) {
                velY = 0;
            }
        }
        else if (keyH.downPressed && !keyH.upPressed) {
            if (velY < 0) {
                velY = 0;
            }
        }

        //APPLY ACCELERATION FROM INPUT

        if (keyH.leftPressed && !keyH.rightPressed) {
            velX -= accel;
        }
        else if (keyH.rightPressed && !keyH.leftPressed) {
            velX += accel;
        }

        if (keyH.upPressed && !keyH.downPressed) {
            velY -= accel;
        }
        else if (keyH.downPressed && !keyH.upPressed) {
            velY += accel;
        }

        //CLAMP MAXIMUM VELOCITY

        if (velX > maxSpeed) {
            velX = maxSpeed;
        }
        if (velX < -maxSpeed) {
            velX = -maxSpeed;
        }
        if (velY > maxSpeed) {
            velY = maxSpeed;
        }
        if (velY < -maxSpeed) {
            velY = -maxSpeed;
        }

        // NORMALIZE DIAGONAL MOVEMENT

        double speedLength = Math.sqrt(velX * velX + velY * velY);

        if (speedLength > maxSpeed) {
            velX = (velX / speedLength) * maxSpeed;
            velY = (velY / speedLength) * maxSpeed;
        }

        //APPLY FRICTION WHEN NO INPUT IS PRESENT

        if (!keyH.leftPressed && !keyH.rightPressed) {
            velX *= friction;
        }
        if (!keyH.upPressed && !keyH.downPressed) {
            velY *= friction;
        }

        //APPLY MOVEMENT IF NO COLLISION

        if (!collisionOn) {
            worldXf += velX;
            worldYf += velY;

            worldX = (int)Math.round(worldXf);
            worldY = (int)Math.round(worldYf);
        }
        else {
            velX *= 0.3;
            velY *= 0.3;
        }

        //SPRITE ANIMATION CONTROL

        spriteCounter++;

        int spriteUpdate;

        if (movingInput) {
            spriteUpdate = 10; //10 klatek
        }
        else {
            spriteUpdate = 18; //idle
        }

        if (spriteCounter > spriteUpdate) {

            if (spriteNum == 1) {
                spriteNum = 2;
            }
            else if (spriteNum == 2) {
                spriteNum = 1;
            }

            spriteCounter = 0;
        }
    }
    public void draw(Graphics2D g2){

//        g2.setColor(Color.white);
//        g2.fillRect(x, y, gp.tileSize, gp.tileSize);

        BufferedImage image = null;

        if(direction.equals("idle")) {
            if(spriteNum == 1) {
                image = idle1;
            }
            if(spriteNum == 2) {
                image = idle2;
            }
        }
        else{
            switch(direction) {
                case "up":
                    if(spriteNum == 1) {
                        image = up1;
                    }
                    if(spriteNum == 2) {
                        image = up2;
                    }
                    break;
                case "left":
                    if(spriteNum == 1) {
                        image = left1;
                    }
                    if(spriteNum == 2) {
                        image = left2;
                    }
                    break;
                case "right":
                    if(spriteNum == 1) {
                        image = right1;
                    }
                    if(spriteNum == 2) {
                        image = right2;
                    }
                    break;
                case "down":
                    if(spriteNum == 1) {
                        image = down1;
                    }
                    if(spriteNum == 2) {
                        image = down2;
                    }
                    break;
            }

        }

        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);


        }
    }

