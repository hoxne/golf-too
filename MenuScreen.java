/**
 * Created by Jade on 23-05-16.
 */



import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import org.lwjgl.opengl.GL11;

public class MenuScreen implements Screen {

    private Skin skin;
    private Stage stage;
    private SpriteBatch batch;
    private int numberOfPlayer = 0;
    private int numberOfAiPlayer = 0;

    private MainController mainController;

    public MenuScreen(MainController mainController){
        this.mainController = mainController;
        create();
    }

    public void create(){
        batch = new SpriteBatch();
        stage = new Stage();

        skin = new Skin();
        Pixmap pixmap = new Pixmap(300, 100, Format.RGBA8888);
        pixmap.setColor(new Color(0f, 1f, 1f, 1));
        pixmap.fill();

        skin.add("white", new Texture(pixmap));

        BitmapFont bitFont = new BitmapFont();
        skin.add("default", bitFont);

        TextButtonStyle textButtonStyle = new TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", Color.LIGHT_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.LIGHT_GRAY);
        textButtonStyle.checked = skin.newDrawable("white", Color.LIGHT_GRAY);
        textButtonStyle.over = skin.newDrawable("white", Color.DARK_GRAY);

        textButtonStyle.font = skin.getFont("default");

        skin.add("default", textButtonStyle);

        final TextButton textButtonPlay = new TextButton("Let's Hit Some Balls!", textButtonStyle);
        textButtonPlay.setPosition(500, 500);
        stage.addActor(textButtonPlay);
        stage.addActor(textButtonPlay);
        stage.addActor(textButtonPlay);

        final TextButton textButtonEditor= new TextButton("My Course DIY", textButtonStyle);
        textButtonEditor.setPosition(500, 350);
        stage.addActor(textButtonEditor);
        stage.addActor(textButtonEditor);
        stage.addActor(textButtonEditor);

        final TextButton textButtonPause = new TextButton("I'm Tired", textButtonStyle);
        textButtonPause.setPosition(500, 200);
        stage.addActor(textButtonPause);
        stage.addActor(textButtonPause);
        stage.addActor(textButtonPause);

        final TextButton textButtonAI = new TextButton("Lazy Mood", textButtonStyle);
        textButtonAI.setPosition(820, 50);
        stage.addActor(textButtonAI);
        stage.addActor(textButtonAI);
        stage.addActor(textButtonAI);

        final TextButton textButtonPlayer = new TextButton("Active mood", textButtonStyle);
        textButtonPlayer.setPosition(180, 50);
        stage.addActor(textButtonPlayer);
        stage.addActor(textButtonPlayer);
        stage.addActor(textButtonPlayer);

        textButtonPlay.addListener(new ChangeListener(){
            public void changed (ChangeEvent event, Actor actor){
                textButtonPlay.setText("Here we go again!!");
                mainController.showGameScreen();
            }
        });

        textButtonEditor.addListener(new ChangeListener(){
            public void changed (ChangeEvent event, Actor actor){
                textButtonEditor.setText("Pinterest session ivl!");
                mainController.showEditor();
            }
        });

        textButtonPause.addListener(new ChangeListener(){
            public void changed (ChangeEvent event, Actor actor){
                textButtonPlay.setText("So saaaaaaaaaaad");
                Gdx.app.exit();
            }
        });

        textButtonAI.addListener(new ChangeListener(){
            public void changed (ChangeEvent event, Actor actor){
                textButtonAI.setText("Lazy mood activated");
                numberOfAiPlayer++;
                mainController.getGameController().setNumberOfAiPlayers(numberOfAiPlayer);

            }
        });

        textButtonPlayer.addListener(new ChangeListener(){
            public void changed (ChangeEvent event, Actor actor){
                textButtonPlayer.setText("Active Mood Activated");
                numberOfPlayer++;
                mainController.getGameController().setNumberOfPlayers(numberOfPlayer);
            }
        });


    }

    public void render (float delta){
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1/30f));
        stage.draw();

    }

    @Override
    public void resize(int width, int height){}

    @Override
    public void dispose(){}

    @Override
    public void show(){
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide(){

    }

    @Override
    public void pause(){

    }

    @Override
    public void resume(){

    }

}
