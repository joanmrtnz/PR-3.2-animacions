package com.badlogic.joanMartinez;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture spriteSheet;
    private Texture background;
    private Music backgroundMusic;
    private Animation<TextureRegion>[] playerAnimations;
    private float stateTime;
    private float speed = 400f;
    private int direction = 3; // 0 = abajo, 1 = izquierda, 2 = derecha, 3 = arriba
    private float posX, posY;
    private float scale = 4.5f; // Tamaño del personaje aumentado

    // Rectángulos para el joystick virtual
    Rectangle up, down, left, right;
    final int DOWN = 0, LEFT = 1, RIGHT = 2, UP = 3, IDLE = 4;

    @Override
    public void create() {
        batch = new SpriteBatch();
        spriteSheet = new Texture(Gdx.files.internal("player.png"));
        background = new Texture(Gdx.files.internal("background.png"));

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background_music.mp3"));

        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        int frameWidth = spriteSheet.getWidth() / 3;
        int frameHeight = spriteSheet.getHeight() / 4;

        playerAnimations = new Animation[4];

        for (int i = 0; i < 4; i++) {
            TextureRegion[] frames = new TextureRegion[3];
            for (int j = 0; j < 3; j++) {
                frames[j] = new TextureRegion(spriteSheet, j * frameWidth, i * frameHeight, frameWidth, frameHeight);
            }
            playerAnimations[i] = new Animation<>(0.25f, frames);
        }

        stateTime = 0f;

        // Posición inicial centrada
        posX = (Gdx.graphics.getWidth() - frameWidth * scale) / 2;
        posY = (Gdx.graphics.getHeight() - frameHeight * scale) / 2;

        up = new Rectangle(0, Gdx.graphics.getHeight() * 2 / 3, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 3);
        down = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 3);
        left = new Rectangle(0, 0, Gdx.graphics.getWidth() / 3, Gdx.graphics.getHeight());
        right = new Rectangle(Gdx.graphics.getWidth() * 2 / 3, 0, Gdx.graphics.getWidth() / 3, Gdx.graphics.getHeight());
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        stateTime += delta;

        int touchDirection = virtual_joystick_control();
        if (touchDirection != IDLE) {
            direction = touchDirection;
            switch (touchDirection) {
                case UP:
                    posY += speed * delta;
                    break;
                case DOWN:
                    posY -= speed * delta;
                    break;
                case LEFT:
                    posX -= speed * delta;
                    break;
                case RIGHT:
                    posX += speed * delta;
                    break;
            }
        }


        TextureRegion frame = playerAnimations[direction].getKeyFrame(stateTime, true);

        ScreenUtils.clear(0, 0, 0, 1);

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(frame, posX, posY, frame.getRegionWidth() * scale, frame.getRegionHeight() * scale); // Dibujar personaje más grande
        batch.end();
    }

    protected int virtual_joystick_control() {
        for (int i = 0; i < 10; i++) {
            if (Gdx.input.isTouched(i)) {
                Vector3 touchPos = new Vector3();
                touchPos.set(Gdx.input.getX(i), Gdx.input.getY(i), 0);

                touchPos.y = Gdx.graphics.getHeight() - touchPos.y;

                if (up.contains(touchPos.x, touchPos.y)) return UP;
                if (down.contains(touchPos.x, touchPos.y)) return DOWN;
                if (left.contains(touchPos.x, touchPos.y)) return LEFT;
                if (right.contains(touchPos.x, touchPos.y)) return RIGHT;
            }
        }
        return IDLE;
    }

    @Override
    public void dispose() {
        batch.dispose();
        spriteSheet.dispose();
        background.dispose();
    }
}
