package com.cs117.oursweeper;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.cs117.oursweeper.GameState;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class OurSweeper extends ApplicationAdapter {
	private Stage stage;
    private Texture backgroundImg;
    private Board mineGrid;
    private GameState gameState;
    private ImageButton playButton;
    private Texture playTexture;
    private TextureRegion playTextureRegion;
    private TextureRegionDrawable playTextureRegionDrawable;

    @Override
	public void create () {
		backgroundImg = new Texture("logo.jpg");
		mineGrid = new Board();
		gameState = new GameState();

		// Example of a button
        playTexture = new Texture(Gdx.files.internal("play.png"));
        playTextureRegion = new TextureRegion(playTexture);
        playTextureRegionDrawable = new TextureRegionDrawable(playTextureRegion);
		playButton = new ImageButton(playTextureRegionDrawable);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                char[][] board = gameState.getBoard();
                for (int i = 0; i < 30; i++) {
                    for (int j = 0; j < 24; j++) {
                        System.out.print(board[i][j]);
                    }
                    System.out.print('\n');
                }
            }
        });

		stage = new Stage(new ScreenViewport());
		stage.addActor(playButton);
		Gdx.input.setInputProcessor(stage);

	}

	@Override
	public void render () {
        int state = gameState.getState();

        // Menu
	    if (state == 0) {

	        // Example UI Rendering
            // Will be removed when project is finished
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            stage.act(Gdx.graphics.getDeltaTime());
            stage.draw();

	        // TODO: Minsoo - Design Menu Screen
            // Include a "Start Game" button that calls gameState.goPlayerCustomizationRoom()
        }

        // Player Customization Room
        else if (state == 1) {
	        // TODO: Minsoo - Design Player Customization Screen
            // Include a text bar to enter Team Name, 4 colored buttons to choose from, and a
            // text bar to enter player name, and a submit button to call:
            // call gameState.setTeamName(..); gameState.setClientPlayerName(...); and
            // gameState.setClientColor(char c) accordingly. Pass char of 'A', 'B', 'C', 'D' to
            // setClientColor() only. Call public void goWaitingRoom() after
            // For the 4 colored buttons to choose from, call gameState.isColorAvailable('A') or
            // 'B' or 'C' or 'D' - if false, disable the button for pressing.
        }

        // Waiting for players room
        else if (state == 2) {
            gameState.checkReady();
	        // TODO: Carol - Design Player Waiting Room Screen
            // Call gameState.getPlayers() to receive an array of Player objects. Get their
            // Color, Name, and ReadyState and display them. Have a "Ready" button that calls
            // GameState.setReady()
        }

        // Game Room, Gameover, and Leaderboard,
        else {
            // TODO: Carol and Minsoo - Design Game Board. Board is 30x24 (and Phone is vertical)
            // 1. Call gameState.getPlayers() to receive an array of Player objects. Get their
            // color, name, and score and display on top of screen
            // Otherwise, display the string from gameState.getElapsedTime() on the top
            // 2. Call gameState.getBoard() to receive a 30x24 char[][] object representing
            // the board, and you would need to display the board as follows:
            // '?' - Undiscovered
            // '*' - Mine That was stepped on
            // '0' - Discovered Tile with nothing in it
            // '1' to '8' - Discovered Tile with the corresponding number in it.
            // 'A' to 'D' - Display flag of color.
            // 3. Decide among yourselves whether to use the button approach or the png approach
            //      - Button: 720 buttons variables. Easy but very messy and tedious
            //      - png: 15 different pictures. Need to consider position in terms of scaling
            //        as different phones have different dimensions, but clean and would be
            //        visibly faster than button approach (recommended)
            //      - Call gameState.reveal(x, y) accordingly. Don't need to worry about it's
            //        revealed or not.
            // 4. Display the remaining number of bombs returned by gameState.getRemainingBombs()
            //    on top.

	        // Game Room
	        if (state == 3 && gameState.getElapsedTime() == "0") {
	            String countdown = gameState.getCountdownTime();
                // TODO: Display a big countdown number with black Background that's at 10% opacity
            }
            else if (state == 4) {
                // TODO: Display a big "Cleared" with black Background that's at 10% opacity
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        gameState.goLeaderBoard();
                    }
                }, 3);
            }
            else if (state == 5) {
	            gameState.checkReady();
	            // TODO: Display a leaderboard box with information from
                // gameState.getLeaderBoard(), and two buttons below:
                // 1. Return to Menu - call gameState.goMenu();
                // 2. Ready - Call client gameState.setReady();
            }
        }

	}

    @Override
	public void dispose () {
		stage.dispose();
        playTexture.dispose();
        backgroundImg.dispose();
	}
}
