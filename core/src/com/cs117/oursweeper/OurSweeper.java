package com.cs117.oursweeper;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.CharArray;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.cs117.oursweeper.GameState;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.event.FocusEvent;
import java.util.ArrayList;

public class OurSweeper extends ApplicationAdapter implements InputProcessor {
	private Stage stage;
    private Board mineGrid;
    private int gameState;
    private int selectedTextBar;
    private ArrayList<Player> players;
    private TextureRegion playTextureRegion;
    private TextureRegionDrawable playTextureRegionDrawable;
    private BitmapFont font;
    private char playerColor;

    private int width;
    private int height;

    // Menu Stage Assets

    private Stage menuStage;
    private Image menuBg;
    private Image playButton;

    // Reg State Assets
    private Stage regStage;
    private Image regBg;
    private Label clientName;
    private String clientNameString;
    private Label teamName;
    private String teamNameString;
    private Label.LabelStyle regFieldStyle;
    // private SimpleCharSequence txtBuffer;
    private String txtBuffer;

    // Wait Room Assets
    private Stage waitStage;
    private int yCoord;
    private Image waitBg;
    private Image redBtn;
    private Image yellowBtn;
    private Image greenBtn;
    private Image whiteBtn;
    private Image redBtnR;
    private Image yellowBtnR;
    private Image greenBtnR;
    private Image whiteBtnR;
    private Image redChar;
    private Image yellowChar;
    private Image greenChar;
    private Image whiteChar;
    private TextureRegion unsetChar;
    private Label[] pNames;
    private Label tName;

    // Game Room Assets
    private Stage gameStage;
    private Image gameBg;
    private TextureRegion[] textures;
    private boolean isFlagSelected;
    private Image flagUnselected;
    private Image flagSelected;
    private Image bombsLeft;
    private Label bombCount;
    private Label timer;
    private String timerString;
    private Label[] pScores;
    private int cd;
    private Label.LabelStyle timerStyle;
    private int startX;
    private int startY;
    private int increment;
    private char[][] cs;
    private Image c3;
    private Image c2;
    private Image c1;
    private Image c0;

    // Leaderboard Assets
    private Stage leaderStage;
    private Image leaderBg;
    private String[][] leaderboard;
    private Label[] topNames;
    private Label[] topScores;

    @Override
	public void create () {

        this.stage = new Stage();
        this.gameState = -1;
        this.selectedTextBar = -1;
        Gdx.input.setInputProcessor(this);
		GameState.getGameStateInstance().goMenu();

        this.width = Gdx.graphics.getWidth();
        this.height = Gdx.graphics.getHeight();
        FreeTypeFontGenerator.setMaxTextureSize(FreeTypeFontGenerator.NO_MAXIMUM);
        FreeTypeFontGenerator g = new FreeTypeFontGenerator(Gdx.files.internal("fonts/cour.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.size = 320;
        p.genMipMaps = true;
        this.font = g.generateFont(p);
        g.dispose();
        this.font.setColor(0,0,0,1);
        this.regFieldStyle = new Label.LabelStyle();
        this.regFieldStyle.font = this.font;
        this.regFieldStyle.fontColor = Color.BLACK;
        this.font.setColor(1,1,1,1);
        this.timerStyle = new Label.LabelStyle();
        this.timerStyle.font = this.font;
        this.timerStyle.fontColor = Color.WHITE;
        // Menu
        menuStage = new Stage();
        menuBg = new Image(new TextureRegion(new Texture(Gdx.files.internal("main_menu.png"))));
        playButton = new Image(new TextureRegion(new Texture(Gdx.files.internal("play.png"))));
	    playButton.setPosition(this.width / 2 - 950 * (this.width / 2560) / 2, 200 * (this.height / 1440));
	    // playButton.setSize(950 * width / 2560, 200 * height / 1440);
        playButton.setScale(width / 2560, height / 1440);
        menuStage.addActor(menuBg);
        menuStage.addActor(playButton);

	    // Registration State
        regStage = new Stage();
        regBg = new Image(new TextureRegion(new Texture(Gdx.files.internal("reg_state.png"))));
        clientName = new Label("", regFieldStyle);
        clientNameString = "";
        clientName.setBounds(1635 * width / 2560, height - 495 * height / 1440, 820 * width / 2560, 130 * height / 2560);
        clientName.setFontScale(0.3f, 0.3f);
        teamName = new Label("", regFieldStyle);
        clientNameString = "";
        teamName.setBounds(1635 * width / 2560, height - 695 * height / 1440, 820 * width / 2560, 130 * height / 2560);
        teamName.setFontScale(0.3f, 0.3f);
        txtBuffer = "";
        regStage.addActor(regBg);
        regStage.addActor(clientName);
        regStage.addActor(teamName);

        // Waiting Room State
        waitStage = new Stage();
        yCoord = 660 * height / 1440;
        waitBg = new Image(new TextureRegion(new Texture(Gdx.files.internal("wait_state.png"))));
        redBtn = new Image(new TextureRegion(new Texture(Gdx.files.internal("red_us.png"))));
        redBtn.setPosition(472 * width / 2560, 120 * height / 1440);
        redBtn.setScale(width / 2560, height/1440);
        yellowBtn = new Image(new TextureRegion(new Texture(Gdx.files.internal("yellow_us.png"))));
        yellowBtn.setScale(width / 2560, height/1440);
        yellowBtn.setPosition(1768 * width / 2560, 120 * height / 1440);
        greenBtn = new Image(new TextureRegion(new Texture(Gdx.files.internal("green_us.png"))));
        greenBtn.setScale(width / 2560, height/1440);
        greenBtn.setPosition(904 * width / 2560, 120 * height / 1440);
        whiteBtn = new Image(new TextureRegion(new Texture(Gdx.files.internal("white_us.png"))));
        whiteBtn.setScale(width / 2560, height/1440);
        whiteBtn.setPosition(1336 * width / 2560, 120 * height / 1440);
        redBtnR = new Image(new TextureRegion(new Texture(Gdx.files.internal("red_s.png"))));
        redBtnR.setScale(width / 2560, height/1440);
        redBtnR.setPosition(472 * width / 2560, 120 * height / 1440);
        yellowBtnR = new Image(new TextureRegion(new Texture(Gdx.files.internal("yellow_s.png"))));
        yellowBtnR.setScale(width / 2560, height/1440);
        yellowBtnR.setPosition(1768 * width / 2560, 120 * height / 1440);
        greenBtnR = new Image(new TextureRegion(new Texture(Gdx.files.internal("green_s.png"))));
        greenBtnR.setScale(width / 2560, height/1440);
        greenBtnR.setPosition(904 * width / 2560, 120 * height / 1440);
        whiteBtnR = new Image(new TextureRegion(new Texture(Gdx.files.internal("white_s.png"))));
        whiteBtnR.setScale(width / 2560, height/1440);
        whiteBtnR.setPosition(1336 * width / 2560, 120 * height / 1440);
        redChar = new Image(new TextureRegion(new Texture(Gdx.files.internal("red.png"))));
        redChar.setScale(width / 2560, height/1440);
        yellowChar = new Image(new TextureRegion(new Texture(Gdx.files.internal("yellow.png"))));
        yellowChar.setScale(width / 2560, height/1440);
        greenChar = new Image(new TextureRegion(new Texture(Gdx.files.internal("green.png"))));
        greenChar.setScale(width / 2560, height/1440);
        whiteChar = new Image(new TextureRegion(new Texture(Gdx.files.internal("white.png"))));
        whiteChar.setScale(width / 2560, height/1440);
        unsetChar = new TextureRegion(new Texture(Gdx.files.internal("char.png")));
        pNames = new Label[4];
        for (int i = 0; i < 4; i++) {
            pNames[i] = new Label("", regFieldStyle);
            pNames[i].setFontScale(0.3f, 0.3f);
        }
        tName = new Label("", regFieldStyle);
        tName.setFontScale(0.4f, 0.4f);

        // Game Room State
        gameStage = new Stage();
        gameBg = new Image(new TextureRegion(new Texture(Gdx.files.internal("game_state.png"))));
        gameStage.addActor(gameBg);
        textures = new TextureRegion[15];
        textures[0] = new TextureRegion(new Texture(Gdx.files.internal("0.png")));
        textures[1] = new TextureRegion(new Texture(Gdx.files.internal("1.png")));
        textures[2] = new TextureRegion(new Texture(Gdx.files.internal("2.png")));
        textures[3] = new TextureRegion(new Texture(Gdx.files.internal("3.png")));
        textures[4] = new TextureRegion(new Texture(Gdx.files.internal("4.png")));
        textures[5] = new TextureRegion(new Texture(Gdx.files.internal("5.png")));
        textures[6] = new TextureRegion(new Texture(Gdx.files.internal("6.png")));
        textures[7] = new TextureRegion(new Texture(Gdx.files.internal("7.png")));
        textures[8] = new TextureRegion(new Texture(Gdx.files.internal("8.png")));
        textures[9] = new TextureRegion(new Texture(Gdx.files.internal("x.png")));
        textures[10] = new TextureRegion(new Texture(Gdx.files.internal("rf.png")));
        textures[11] = new TextureRegion(new Texture(Gdx.files.internal("gf.png")));
        textures[12] = new TextureRegion(new Texture(Gdx.files.internal("wf.png")));
        textures[13] = new TextureRegion(new Texture(Gdx.files.internal("yf.png")));
        textures[14] = new TextureRegion(new Texture(Gdx.files.internal("o.png")));
        isFlagSelected = false;
        flagSelected = new Image(new TextureRegion(new Texture(Gdx.files.internal("flags.png"))));
        flagSelected.setScale(width / 2560, height/1440);
        flagSelected.setPosition(width - 500 * width / 2560, 600 * height / 1440);
        flagUnselected = new Image(new TextureRegion(new Texture(Gdx.files.internal("flag.png"))));
        flagUnselected.setScale(width / 2560, height/1440);
        flagUnselected.setPosition(width - 500 * width / 2560, 600 * height / 1440);
        bombsLeft = new Image(new TextureRegion(new Texture(Gdx.files.internal("bombs_left.png"))));
        bombsLeft.setPosition(0, 600 * height / 1440);
        bombsLeft.setScale(width / 2560, height/1440);
        bombCount = new Label("", regFieldStyle);
        timer = new Label("", timerStyle);
        timer.setFontScale(0.45f);
        bombCount.setFontScale(0.6f);
        bombCount.setPosition(150, 500);
        pScores = new Label[4];
        for (int i = 0; i < 4; i++) {
            pScores[i] = new Label("", regFieldStyle);
            pScores[i].setFontScale(0.25f, 0.25f);
        }
        startX = (width - 30 * 51 * (width / 2560))/2;
        startY = height - (height - 23 * 51 * (height / 1440));
        increment = 51 * width / 2560;
        c3 = new Image(new TextureRegion(new Texture(Gdx.files.internal("c3.png"))));
        c3.setScale(width / 2560, height/1440);
        c3.setPosition(width / 2 - 600 * width / 2560, height / 2 - 600 * height / 1440);
        c2 = new Image(new TextureRegion(new Texture(Gdx.files.internal("c2.png"))));
        c2.setScale(width / 2560, height/1440);
        c2.setPosition(width / 2 - 600 * width / 2560, height / 2 - 600 * height / 1440);
        c1 = new Image(new TextureRegion(new Texture(Gdx.files.internal("c1.png"))));
        c1.setScale(width / 2560, height/1440);
        c1.setPosition(width / 2 - 600 * width / 2560, height / 2 - 600 * height / 1440);
        c0 = new Image(new TextureRegion(new Texture(Gdx.files.internal("c0.png"))));
        c0.setScale(width / 2560, height/1440);
        c0.setPosition(width / 2 - 600 * width / 2560, height / 2 - 600 * height / 1440);

        // Leaderboard
        leaderStage = new Stage();
        leaderBg = new Image(new TextureRegion(new Texture(Gdx.files.internal("leader_state.png"))));
        leaderStage.addActor(leaderBg);
        leaderboard = new String[4][3];
        topNames = new Label[4];
        topScores = new Label[4];
        for (int i = 0; i < 4; i++) {
            topNames[i] = new Label("",  regFieldStyle);
            topNames[i].setPosition(1070, 730 + i * 250);
            topNames[i].setFontScale(0.4f);
            topScores[i] = new Label("",  regFieldStyle);
            topScores[i].setPosition(1335, 730 + i * 250);
            topScores[i].setFontScale(0.4f);
        }
    }

	@Override
	public void render () {
        int state = GameState.getGameStateInstance().getState();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	    if (state == 0) {
	        stage = menuStage;
        }

        // Player Customization Room
        else if (state == 1) {
	        stage = regStage;
        }

        // Waiting for players room
        else if (state == 2) {
	        GameState.getGameStateInstance().checkReady();
	        if (GameState.getGameStateInstance().playerUpdated()) {
	            waitStage = new Stage();
                waitStage.addActor(waitBg);

                ArrayList<Player> players = GameState.getGameStateInstance().getPlayers();

                for (int i = 0; i < players.size(); i++) {
                    char color = players.get(i).getColor();
                    String name = players.get(i).getName();
                    int xCoord = (width / (players.size() + 1)) * (i + 1) - 150 * (width / 2560);
                    pNames[i].setText(name);
                    pNames[i].setPosition((width / (players.size() + 1)) * (i + 1) - name.length() * 23 * (width / 2560), yCoord - (50 * height / 1440));
                    waitStage.addActor(pNames[i]);
                    Image ch;
                    if (color == ' ') {
                        ch = new Image(unsetChar);
                        ch.setPosition(xCoord, yCoord);
                        waitStage.addActor(ch);
                    } else if (color == 'A') {
                        redChar.setPosition(xCoord, yCoord);
                        waitStage.addActor(redChar);
                    } else if (color == 'B') {
                        greenChar.setPosition(xCoord, yCoord);
                        waitStage.addActor(greenChar);
                    } else if (color == 'C') {
                        whiteChar.setPosition(xCoord, yCoord);
                        waitStage.addActor(whiteChar);
                    } else if (color == 'D') {
                        yellowChar.setPosition(xCoord, yCoord);
                        waitStage.addActor(yellowChar);
                    }
                }
                if (GameState.getGameStateInstance().isColorAvailable('A')) {
                    waitStage.addActor(redBtn);
                } else {
                    waitStage.addActor(redBtnR);
                }
                if (GameState.getGameStateInstance().isColorAvailable('B')) {
                    waitStage.addActor(greenBtn);
                } else {
                    waitStage.addActor(greenBtnR);
                }
                if (GameState.getGameStateInstance().isColorAvailable('C')) {
                    waitStage.addActor(whiteBtn);
                } else {
                    waitStage.addActor(whiteBtnR);
                }
                if (GameState.getGameStateInstance().isColorAvailable('D')) {
                    waitStage.addActor(yellowBtn);
                } else {
                    waitStage.addActor(yellowBtnR);
                }
                tName.setText("Team " + this.teamNameString);
                tName.setPosition(1280 * width / 2560 - (this.teamNameString.length() + 5) * 28, 1080 * height / 1440);
                waitStage.addActor(tName);
            }
            stage = waitStage;
        }

        else if (state == 3) {
	        if (GameState.getGameStateInstance().isDisabled()) {
                cd = GameState.getGameStateInstance().getCountdownTime();
            }
            this.timerString = GameState.getGameStateInstance().getElapsedTime();
	        if (GameState.getGameStateInstance().playerUpdated()) {
	            gameStage = new Stage();
	            gameStage.addActor(gameBg);
	            cs = GameState.getGameStateInstance().getCurrentBoard();
                for (int i = 0; i < cs.length; i++) {
                    for (int j = 0; j < cs[i].length; j++) {
                        Image grid;
                        char c = cs[i][j];
                        if (c < '9' && c >= '0') {
                            grid = new Image(textures[c - '0']);
                        }
                        else if (c == '?') {
                            grid = new Image(textures[9]);
                        }
                        else if (c == 'A') {
                            grid = new Image(textures[10]);
                        }
                        else if (c == 'B') {
                            grid = new Image(textures[11]);
                        }
                        else if (c == 'C') {
                            grid = new Image(textures[12]);
                        }
                        else if (c == 'D') {
                            grid = new Image(textures[13]);
                        }
                        else {
                            grid = new Image(textures[14]);
                        }
                        grid.setScale(width / 2560, height/1440);
                        grid.setPosition(startX + increment * j, startY - increment * i);
                        gameStage.addActor(grid);
                    }
                    if (isFlagSelected) {
                        gameStage.addActor(flagSelected);
                    }
                    else {
                        gameStage.addActor(flagUnselected);
                    }
                }
                players = GameState.getGameStateInstance().getPlayers();
                if (players.get(0).getColor() == 'A') {
                    redChar.setPosition(50 * width / 2560, height - 200 * height / 1440);
                    redChar.setScale(0.5f);
                    gameStage.addActor(redChar);
                }
                else if (players.get(0).getColor() == 'B') {
                    greenChar.setPosition(50 * width / 2560, height - 180 * height / 1440);
                    greenChar.setScale(0.5f);
                    gameStage.addActor(greenChar);
                }
                else if (players.get(0).getColor() == 'C') {
                    whiteChar.setPosition(50 * width / 2560, height - 180 * height / 1440);
                    whiteChar.setScale(0.5f);
                    gameStage.addActor(whiteChar);
                }
                else if (players.get(0).getColor() == 'D') {
                    yellowChar.setPosition(50 * width / 2560, height - 180 * height / 1440);
                    yellowChar.setScale(0.5f);
                    gameStage.addActor(yellowChar);
                }

                if (players.get(1).getColor() == 'A') {
                    redChar.setPosition(450 * width / 2560, height - 180 * height / 1440);
                    redChar.setScale(0.5f);
                    gameStage.addActor(redChar);
                }
                else if (players.get(1).getColor() == 'B') {
                    greenChar.setPosition(450 * width / 2560, height - 180 * height / 1440);
                    greenChar.setScale(0.5f);
                    gameStage.addActor(greenChar);
                }
                else if (players.get(1).getColor() == 'C') {
                    whiteChar.setPosition(450 * width / 2560, height - 180 * height / 1440);
                    whiteChar.setScale(0.5f);
                    gameStage.addActor(whiteChar);
                }
                else if (players.get(1).getColor() == 'D') {
                    yellowChar.setPosition(450 * width / 2560, height - 180 * height / 1440);
                    yellowChar.setScale(0.5f);
                    gameStage.addActor(yellowChar);
                }
                pNames[0].setPosition(200 * width / 2560, height - 80 * height / 1440);
                pNames[0].setFontScale(0.18f);
                gameStage.addActor(pNames[0]);
                pScores[0].setText("Score: " + String.valueOf(players.get(0).getScore()));
                pScores[0].setFontScale(0.10f);
                pScores[0].setPosition(200 * width / 2560, height- 130 * height / 1440);
                gameStage.addActor(pScores[0]);
                pNames[1].setPosition(600 * width / 2560, height- 80 * height / 1440);
                pNames[1].setFontScale(0.18f);
                gameStage.addActor(pNames[1]);
                pScores[1].setPosition(600 * width / 2560, height - 130 * height / 1440);
                pScores[1].setText("Score: " + String.valueOf(players.get(1).getScore()));
                pScores[1].setFontScale(0.10f);
                gameStage.addActor(pScores[1]);
                if (players.size() == 3 || players.size() == 4) {
                    if (players.get(2).getColor() == 'A') {
                        redChar.setPosition(1600 * width / 2560, height - 180 * height / 1440);
                        redChar.setScale(0.5f);
                        gameStage.addActor(redChar);
                    }
                    else if (players.get(2).getColor() == 'B') {
                        greenChar.setPosition(1600 * width / 2560, height - 180 * height / 1440);
                        greenChar.setScale(0.5f);
                        gameStage.addActor(greenChar);
                    }
                    else if (players.get(2).getColor() == 'C') {
                        whiteChar.setPosition(1600 * width / 2560, height - 180 * height / 1440);
                        whiteChar.setScale(0.5f);
                        gameStage.addActor(whiteChar);
                    }
                    else if (players.get(2).getColor() == 'D') {
                        yellowChar.setPosition(1600 * width / 2560, height - 180 * height / 1440);
                        yellowChar.setScale(0.5f);
                        gameStage.addActor(yellowChar);
                    }
                    pNames[2].setPosition(1750 * width / 2560, height- 80 * height / 1440);
                    pNames[2].setFontScale(0.18f);
                    gameStage.addActor(pNames[2]);
                    pScores[2].setPosition(1750 * width / 2560, height - 130 * height / 1440);
                    pScores[2].setText("Score: " + String.valueOf(players.get(2).getScore()));
                    pScores[2].setFontScale(0.10f);
                    gameStage.addActor(pScores[2]);
                }
                if (players.size() == 4) {
                    if (players.get(3).getColor() == 'A') {
                        redChar.setPosition(2000 * width / 2560, height - 180 * height / 1440);
                        redChar.setScale(0.5f);
                        gameStage.addActor(redChar);
                    }
                    else if (players.get(3).getColor() == 'B') {
                        greenChar.setPosition(2000 * width / 2560, height - 180 * height / 1440);
                        greenChar.setScale(0.5f);
                        gameStage.addActor(greenChar);
                    }
                    else if (players.get(3).getColor() == 'C') {
                        whiteChar.setPosition(2000 * width / 2560, height - 180 * height / 1440);
                        whiteChar.setScale(0.5f);
                        gameStage.addActor(whiteChar);
                    }
                    else if (players.get(3).getColor() == 'D') {
                        yellowChar.setPosition(2000 * width / 2560, height - 180 * height / 1440);
                        yellowChar.setScale(0.5f);
                        gameStage.addActor(yellowChar);
                    }
                    pNames[3].setPosition(2150 * width / 2560, height- 80 * height / 1440);
                    pNames[3].setFontScale(0.18f);
                    gameStage.addActor(pNames[3]);
                    pScores[3].setPosition(2150 * width / 2560, height - 130 * height / 1440);
                    pScores[3].setText("Score: " + String.valueOf(players.get(3).getScore()));
                    pScores[3].setFontScale(0.10f);
                    gameStage.addActor(pScores[3]);
                }
                gameStage.addActor(bombsLeft);
                bombCount.setText(GameState.getGameStateInstance().getBombsLeft());
                gameStage.addActor(bombCount);
                timer.setText(this.timerString);
                timer.setPosition(width / 2 - this.timerString.length() * 38, height - 100 * height/1440);
                gameStage.addActor(timer);
                if (GameState.getGameStateInstance().isDisabled()) {
                    if (cd == 3) {
                        gameStage.addActor(c3);
                    }
                    else if (cd == 2) {
                        gameStage.addActor(c2);
                    }
                    else if (cd == 1) {
                        gameStage.addActor(c1);
                    }
                    else if (cd == 0) {
                        gameStage.addActor(c0);
                    }
                }
            }
            stage = gameStage;
        }
        else if (state == 4) {
	        if (GameState.getGameStateInstance().playerUpdated()) {
	            leaderboard = GameState.getGameStateInstance().getLeaderBoard();
	            for (int i = 0; i < 4; i++) {
	                topNames[i].setText(leaderboard[i][1]);
                    leaderStage.addActor(topNames[i]);
	                topScores[i].setText(leaderboard[i][2]);
                    leaderStage.addActor(topScores[i]);
                }
                timer.setText(GameState.getGameStateInstance().getFinalTime());
	            leaderStage.addActor(timer);
            }
	        stage = leaderStage;
        }
        stage.draw();
	}

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        int state = GameState.getGameStateInstance().getState();
        if (state == 0) {
            Vector2 coords = new Vector2(playButton.getX(), playButton.getY());
            playButton.localToStageCoordinates(coords);
            playButton.getStage().stageToScreenCoordinates(coords);
            if (screenX > coords.x - playButton.getWidth() && screenX < coords.x  &&
                    screenY > coords.y && screenY < coords.y + playButton.getHeight()) {
                GameState.getGameStateInstance().goPlayerCustomizationRoom();
            }
        }
        else if (state == 1) {
            if (screenY < 515 * (height / 1440) && screenY > 385 * (height / 1440) &&
                    screenX > 1615 * (width / 2560) && screenX < 2435 * (width / 2560)) {
                Gdx.input.setOnscreenKeyboardVisible(true);
                this.selectedTextBar = 0;
                this.clientNameString = "";
                this.clientName.setText("");
                this.txtBuffer = "";
            }
            else if (screenY < 705 * (height / 1440) && screenY > 595 * (height / 1440) &&
                    screenX > 1615 * (width / 2560) && screenX < 2435 * (width / 2560)) {
                Gdx.input.setOnscreenKeyboardVisible(true);
                this.selectedTextBar = 1;
                this.teamNameString = "";
                this.teamName.setText("");
                this.txtBuffer = "";
            }
            else {
                Gdx.input.setOnscreenKeyboardVisible(false);
                this.selectedTextBar = -1;
                this.txtBuffer = "";
            }
            if (screenY < 0.25 * height && screenX < 0.125 * width) {
                GameState.getGameStateInstance().goMenu();
            }
            else if (screenY > 0.8 * height && screenX > 0.66 * width &&
                    !teamNameString.equals("") && !clientNameString.equals("")) {
                System.out.print("Team Name: " + teamNameString + "\n");
                System.out.print("My Name: " + clientNameString + "\n");
                GameState.getGameStateInstance().setClientInfo(this.teamNameString, this.clientNameString);
                GameState.getGameStateInstance().goWaitingRoom();
            }
        }
        else if (state == 2) {
            if (screenY < 0.25 * height && screenX < 0.125 * width) {
                GameState.getGameStateInstance().goPlayerCustomizationRoom();
                this.clientNameString = "";
                this.clientName.setText("");
                this.teamNameString = "";
                this.teamName.setText("");
            }
            else if (screenY < 1310 * (height / 1440) && screenY > 1200 * (height / 1440) &&
                    screenX > 467 * (width / 2560) && screenX < 578 * (width / 2560)) {
                GameState.getGameStateInstance().setClientColor('A');
                this.playerColor = 'A';
                GameState.getGameStateInstance().setUpdated();
            }
            else if (screenY < 1310 * (height / 1440) && screenY > 1200 * (height / 1440) &&
                    screenX > 905 * (width / 2560) && screenX < 1015 * (width / 2560)) {
                GameState.getGameStateInstance().setClientColor('B');
                this.playerColor = 'B';
                GameState.getGameStateInstance().setUpdated();
            }
            else if (screenY < 1310 * (height / 1440) && screenY > 1200 * (height / 1440) &&
                    screenX > 1330 * (width / 2560) && screenX < 1450 * (width / 2560)) {
                GameState.getGameStateInstance().setClientColor('C');
                this.playerColor = 'C';
                GameState.getGameStateInstance().setUpdated();
            }
            else if (screenY < 1310 * (height / 1440) && screenY > 1200 * (height / 1440) &&
                    screenX > 1760 * (width / 2560) && screenX < 1885 * (width / 2560)) {
                GameState.getGameStateInstance().setClientColor('D');
                this.playerColor = 'D';
                GameState.getGameStateInstance().setUpdated();
            }
        }
        else if (state == 3) {
            screenY = height - screenY;
            int startX = (width - 30 * 51 * (width / 2560)) / 2;
            if (!GameState.getGameStateInstance().isDisabled()) {
                if (screenX > startX && screenX < width - startX && screenY < 24 * 51 * (width / 2560)) {
                    int tileX = (int) Math.floor((screenX - startX) / (51 * (width / 2560)));
                    int tileY = 23 - (int) Math.floor((screenY) / (51 * (width / 2560)));
                    if (isFlagSelected) {
                        GameState.getGameStateInstance().flag(this.clientNameString, tileX, tileY);
                        GameState.getGameStateInstance().setUpdated();
                    } else {
                        GameState.getGameStateInstance().reveal(this.clientNameString, tileX, tileY);
                        GameState.getGameStateInstance().setUpdated();
                    }
                } else if (screenX > width - 500 * width / 2560 && screenY > 600 * height / 1440 && screenY < 1100 * height / 1440) {
                    this.isFlagSelected = !this.isFlagSelected;
                    GameState.getGameStateInstance().setUpdated();
                }
            }
        }
        else if (state == 4) {
            System.out.println(screenX);
            System.out.println(screenY);
            if (screenY < 1149 * (height / 1440) && screenY > 949 * (height / 1440) &&
                    screenX > 1838 * (width / 2560) && screenX < 2473 * (width / 2560)) {
                GameState.getGameStateInstance().goPlayerCustomizationRoom();
            }
        }
        return true;
    }

    @Override public boolean mouseMoved (int screenX, int screenY) {
        // we can also handle mouse movement without anything pressed
//		camera.unproject(tp.set(screenX, screenY, 0));
        return false;
    }

    @Override public boolean touchDragged (int screenX, int screenY, int pointer) {
        return true;
    }

    @Override public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        return true;
    }

    @Override public void resize (int width, int height) {
    }

    @Override public boolean keyDown (int keycode) {
        return false;
    }

    @Override public boolean keyUp (int keycode) {
        return false;
    }

    @Override public boolean keyTyped (char character) {
        if (txtBuffer.length() > 15) return false;
            if (character == '\r') {
                Gdx.input.setOnscreenKeyboardVisible(false);
                return true;
            }

        if (character == '\n') {
            Gdx.input.setOnscreenKeyboardVisible(false);
            return true;
        }

        if (character == '\b') {
            if (txtBuffer.length() > 0)
                txtBuffer = txtBuffer.substring(0, txtBuffer.length() - 1);
            else {
                txtBuffer = "";
            }
        }

        else {
            txtBuffer += character;
        }
        if (this.selectedTextBar == 0) {
            clientName.setText(txtBuffer);
            clientNameString = txtBuffer;
        }
        else if (this.selectedTextBar == 1) {
            teamName.setText(txtBuffer);
            teamNameString = txtBuffer;
        }

        return true;
    }

    @Override public boolean scrolled (int amount) {
        return false;
    }

    @Override
	public void dispose () {
		stage.dispose();
        GameState.getGameStateInstance().goPlayerCustomizationRoom();
	}
}
