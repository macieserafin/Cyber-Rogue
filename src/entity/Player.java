package entity;

import main.GamePanel;
import main.KeyHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import entity.Bullet;

public class Player extends Entity {

    GamePanel gp;
    KeyHandler keyH;

    public final int screenX;
    public final int screenY;

    private double velX = 0.0;
    private double velY = 0.0;

    private double accel = 0.5;
    private double maxSpeed = 4;
    private double friction = 0.5; // im bliżej 1.0 tym dłużej się ślizga

    private double worldXf;
    private double worldYf;

    public String combatDirection;
    BufferedImage upShoot, downShoot, leftShoot, rightShoot;

    private int shotCooldownCounter = 0;
    private int shotCooldownFrames = 12; // 12 klatek przy 60 FPS -> 5 strzałów na sekundę

    int hasKey = 0;


    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;

        screenX = gp.screenWidth/2 - (gp.tileSize/2);
        screenY = gp.screenHeight/2- (gp.tileSize/2);

        solidArea = new Rectangle(8 , 16, 32, 32); //hitbox

        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

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
        combatDirection = "idle";
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

        double currentMaxSpeed;
        double currentAccel;
        combatDirection = "idle";

        if (shotCooldownCounter > 0) {
            shotCooldownCounter--;
        }


        if (keyH.shiftPressed) {
            currentMaxSpeed = maxSpeed + 2.2; // sprint speed
            currentAccel = accel + 0.5;   // sprint acceleration
        }
        else {
            currentMaxSpeed = maxSpeed;
            currentAccel = accel;
        }


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

        int objIndex = gp.cChecker.checkObject(this, true);
        pickUpObject(objIndex);


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
            velX -= currentAccel;
        }
        else if (keyH.rightPressed && !keyH.leftPressed) {
            velX += currentAccel;
        }
        if (keyH.upPressed && !keyH.downPressed) {
            velY -= currentAccel;
        }
        else if (keyH.downPressed && !keyH.upPressed) {
            velY += currentAccel;
        }


        //CLAMP MAXIMUM VELOCITY

        if (velX > currentMaxSpeed) {
            velX = currentMaxSpeed;
        }
        if (velX < -currentMaxSpeed) {
            velX = -currentMaxSpeed;
        }
        if (velY > currentMaxSpeed) {
            velY = currentMaxSpeed;
        }
        if (velY < -currentMaxSpeed) {
            velY = -currentMaxSpeed;
        }

        //COMBAT

        boolean isShooting = keyH.upShootPressed || keyH.downShootPressed || keyH.leftShootPressed || keyH.rightShootPressed;

        if (keyH.upShootPressed) {
            combatDirection = "up";
            currentMaxSpeed = 2.5;
        }
        else if (keyH.downShootPressed) {
            combatDirection = "down";
            currentMaxSpeed = 2.5;
        }
        else if (keyH.leftShootPressed) {
            combatDirection = "left";
            currentMaxSpeed = 2.5;
        }
        else if (keyH.rightShootPressed) {
            combatDirection = "right";
            currentMaxSpeed = 2.5;
        }
        else {
            combatDirection = "idle";
        }

        if (isShooting && !combatDirection.equals("idle")) {

            if (shotCooldownCounter == 0) {

                double startX = worldX + (gp.tileSize / 2.0);
                double startY = worldY + (gp.tileSize / 2.0);

                Bullet bullet = new Bullet.Builder(gp.tileSize)
                        .startPosition(startX, startY)
                        .direction(combatDirection)
                        .speed(10)
                        .rangeTiles(5)
                        .damage(1)
                        .build();

                gp.bulletManager.addBullet(bullet);
                gp.playSoundEffect(1);

                shotCooldownCounter = shotCooldownFrames;
            }
        }


        // NORMALIZE DIAGONAL MOVEMENT



        double speedLength = Math.sqrt(velX * velX + velY * velY);

        if (speedLength > currentMaxSpeed) {
            velX = (velX / speedLength) * currentMaxSpeed;
            velY = (velY / speedLength) * currentMaxSpeed;}

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


    public void pickUpObject(int i) {
        if(i != 999){
            String objectName = gp.obj[i].name;

            switch (objectName) {
                case "Key":
                    hasKey++;
                    gp.obj[i] = null;
                    gp.playSoundEffect(3);
                    break;

                case "Door":
                        if(hasKey > 0){
                            gp.obj[i] = null;
                            hasKey--;
                            gp.playSoundEffect(2);
                        }
                        break;

                case "Boots":
                    maxSpeed += 3;
                    gp.obj[i] = null;
                    gp.playSoundEffect(4);

            }
        }

    }


    public void draw(Graphics2D g2){

//        g2.setColor(Color.white);
//        g2.fillRect(x, y, gp.tileSize, gp.tileSize);

        BufferedImage image = null;

        if(combatDirection.equals("idle")) {
            if (direction.equals("idle")) {
                if (spriteNum == 1) {
                    image = idle1;
                }
                if (spriteNum == 2) {
                    image = idle2;
                }
            } else {
                switch (direction) {
                    case "up":
                        if (spriteNum == 1) {
                            image = up1;
                        }
                        if (spriteNum == 2) {
                            image = up2;
                        }
                        break;
                    case "left":
                        if (spriteNum == 1) {
                            image = left1;
                        }
                        if (spriteNum == 2) {
                            image = left2;
                        }
                        break;
                    case "right":
                        if (spriteNum == 1) {
                            image = right1;
                        }
                        if (spriteNum == 2) {
                            image = right2;
                        }
                        break;
                    case "down":
                        if (spriteNum == 1) {
                            image = down1;
                        }
                        if (spriteNum == 2) {
                            image = down2;
                        }
                        break;
                }

            }
        }else {
        switch (combatDirection) {
            case "up" : image = upShoot; break;
            case "down" : image = downShoot; break;
            case "left" : image = leftShoot; break;
            case "right": image = rightShoot ; break;
        }
        }

        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);


        }
    }

