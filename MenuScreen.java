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

    private Game game;
    private MainController mainController;

    public MenuScreen(Game game){
        create();
        this.game = game;
    }

    public MenuScreen(){
        create();
    }

    public void create(){
        batch = new SpriteBatch();
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

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
        textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
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

        textButtonPlay.addListener(new ChangeListener(){
            public void changed (ChangeEvent event, Actor actor){
                System.out.println("It's been clicked!!");
                textButtonPlay.setText("Here we go again!!");

            }
        });

        textButtonEditor.addListener(new ChangeListener(){
            public void changed (ChangeEvent event, Actor actor){
                System.out.println("It's been clicked!!");
                textButtonPlay.setText("Pinterest session ivl!");

            }
        });

        textButtonPause.addListener(new ChangeListener(){
            public void changed (ChangeEvent event, Actor actor){
                System.out.println("It's been clicked!!");
                textButtonPlay.setText("So saaaaaaaaaaad");

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
