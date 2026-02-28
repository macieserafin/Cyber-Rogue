package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayDeque;
import java.util.Deque;

public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean upShootPressed, downShootPressed, leftShootPressed, rightShootPressed;



    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) {
            upPressed = true;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = true;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = true;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = true;
        }

        if (code == KeyEvent.VK_UP) {
            upShootPressed = true;
        }
        if (code == KeyEvent.VK_DOWN) {
            downShootPressed = true;
        }
        if (code == KeyEvent.VK_LEFT) {
            leftShootPressed = true;
        }
        if (code == KeyEvent.VK_RIGHT) {
            rightShootPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = false;
        }

        if (code == KeyEvent.VK_UP) {
            upShootPressed = false;
        }
        if (code == KeyEvent.VK_DOWN) {
            downShootPressed = false;
        }
        if (code == KeyEvent.VK_LEFT) {
            leftShootPressed = false;
        }
        if (code == KeyEvent.VK_RIGHT) {
            rightShootPressed = false;
        }
    }
}
