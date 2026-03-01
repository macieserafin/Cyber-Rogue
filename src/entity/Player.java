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
    BufferedImage upAttack1, downAttack1, leftAttack1, rightAttack1, upAttack2, downAttack2, leftAttack2, rightAttack2;

    private int shotCooldownCounter = 0;
    private int shotCooldownFrames = 12; // 12 klatek przy 60 FPS -> 5 strzałów na sekundę

    CombatState combatState = CombatState.RANGE;

    int hasKey = 0;

    // MELEE ATTACK STATE
    private boolean meleeAttacking = false;

    private int meleeFrame = 0;



    private final int MELEE_WINDUP_FRAMES = 6;   // króciutka pauza: attack1
    private final int MELEE_STRIKE_FRAMES = 5;   // szybkie uderzenie: attack2
    private final int MELEE_RECOVER_FRAMES = 10; // ciężar po uderzeniu


    private boolean meleeAttackHeldLastFrame = false;


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

            upShoot    = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("player/combat/player_up_shoot.png")));
            downShoot  = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("player/combat/player_down_shoot.png")));
            leftShoot  = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("player/combat/player_left_shoot.png")));
            rightShoot = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("player/combat/player_right_shoot.png")));

            upAttack1 = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("player/combat/player_up_attack_1.png")));
            upAttack2 = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("player/combat/player_up_attack_2.png")));
            downAttack1 = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("player/combat/player_down_attack_1.png")));
            downAttack2 = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("player/combat/player_down_attack_2.png")));
            leftAttack1 = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("player/combat/player_left_attack_1.png")));
            leftAttack2 = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("player/combat/player_left_attack_2.png")));
            rightAttack1 = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("player/combat/player_right_attack_1.png")));
            rightAttack2 = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("player/combat/player_right_attack_2.png")));



        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void update() {

        boolean movingInput = keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed;

        double currentMaxSpeed;
        double currentAccel;

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


        // OPPOSITE DIRECTION BRAKING

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

        if(keyH.qPressed) {
            combatState = combatState.next();
            keyH.qPressed = false;
            System.out.println("keyH.qPressed");
        }

        if(combatState == CombatState.RANGE) {
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

            if (isShooting) {

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

        }
        if (combatState == CombatState.MELEE) {

            boolean attackHeld =
                    keyH.upShootPressed || keyH.downShootPressed || keyH.leftShootPressed || keyH.rightShootPressed;

            boolean attackJustPressed = attackHeld && !meleeAttackHeldLastFrame;
            meleeAttackHeldLastFrame = attackHeld;

            if (attackJustPressed) {

                if (keyH.upShootPressed) combatDirection = "up";
                else if (keyH.downShootPressed) combatDirection = "down";
                else if (keyH.leftShootPressed) combatDirection = "left";
                else combatDirection = "right";

                if (!meleeAttacking) {
                    startMeleeAttack();
                }
            }
            else if (!meleeAttacking) {
                combatDirection = "idle";
            }

            if (meleeAttacking) {
                updateMeleeAttack();
                currentMaxSpeed = 1.2;
                velX *= 0.85;
                velY *= 0.85;
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

                case "Chest":
                    if(hasKey > 0){
                        gp.obj[i] = null;
                        hasKey--;
                        gp.playSoundEffect(2);
                    }
            }
        }

    }


    public void draw(Graphics2D g2){

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
        } else {
            if(combatState == CombatState.RANGE){
                switch (combatDirection) {
                    case "up" : image = upShoot; break;
                    case "down" : image = downShoot; break;
                    case "left" : image = leftShoot; break;
                    case "right": image = rightShoot ; break;
                }
            }
            if (combatState == CombatState.MELEE) {

                if (!meleeAttacking) {
                } else {

                    boolean useAttack2 = false;

                    int strikeStart = MELEE_WINDUP_FRAMES;
                    int strikeEnd = MELEE_WINDUP_FRAMES + MELEE_STRIKE_FRAMES;

                    if (meleeFrame >= strikeStart && meleeFrame < strikeEnd) {
                        useAttack2 = true;
                    }

                    switch (combatDirection) {
                        case "up":
                            image = useAttack2 ? upAttack2 : upAttack1;
                            break;
                        case "down":
                            image = useAttack2 ? downAttack2 : downAttack1;
                            break;
                        case "left":
                            image = useAttack2 ? leftAttack2 : leftAttack1;
                            break;
                        case "right":
                            image = useAttack2 ? rightAttack2 : rightAttack1;
                            break;
                    }
                }
            }
        }
        int drawW = image.getWidth() * gp.scale;
        int drawH = image.getHeight() * gp.scale;

        Point p = computeDrawPosition(drawW, drawH);
        g2.drawImage(image, p.x, p.y, drawW, drawH, null);

        g2.setFont(new Font("Consolas", Font.PLAIN, 18));
        g2.setColor(Color.WHITE);
        g2.drawString("Combat mode (Q): " + combatState, 30, 30);


    }

    private Point computeDrawPosition(int drawW, int drawH) {

        int anchorX = screenX + gp.tileSize / 2;
        int anchorY = screenY + gp.tileSize;

        int drawX = anchorX - drawW / 2;
        int drawY = anchorY - drawH;

        boolean isMeleeSprite = (combatState == CombatState.MELEE && !"idle".equals(combatDirection));
        if (!isMeleeSprite) {
            return new Point(drawX, drawY);
        }

        int extraW = drawW - gp.tileSize;

        switch (combatDirection) {
            case "right":
                drawX = screenX;
                break;

            case "left":
                drawX = screenX - extraW;
                break;

            case "down":
                drawY = screenY;
                drawX = screenX - extraW / 2;
                break;

            case "up":
            default:
                break;
        }

        return new Point(drawX, drawY);
    }

    private void startMeleeAttack() {
        meleeAttacking = true;
        meleeFrame = 0;
        gp.playSoundEffect(5);

        velX *= 0.4;
        velY *= 0.4;
    }

    private void stopMeleeAttack() {
        meleeAttacking = false;
        meleeFrame = 0;
        combatDirection = "idle";
    }

    private void updateMeleeAttack() {
        meleeFrame++;

        int total = MELEE_WINDUP_FRAMES + MELEE_STRIKE_FRAMES + MELEE_RECOVER_FRAMES;

        // meleeDamage()
        // int strikeStart = MELEE_WINDUP_FRAMES;
        // int strikeEnd = MELEE_WINDUP_FRAMES + MELEE_STRIKE_FRAMES;
        // if (meleeFrame == strikeStart) { meleeDamage(); }

        if (meleeFrame >= total) {
            stopMeleeAttack();
        }
    }
}

