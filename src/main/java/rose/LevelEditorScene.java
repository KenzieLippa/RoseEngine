package rose;

import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

public class LevelEditorScene extends Scene{
    //we will be making the level here

    private GameObject obj1;
    private Spritesheet sprites;
    public LevelEditorScene(){

        //System.out.println("Inside level editor");
    }

    @Override
    public void init(){
        loadResources();
        this.camera = new Camera(new Vector2f(-250, 0));
        sprites = AssetPool.getSpritesheet("assets/images/spritesheet.png");
        obj1 = new GameObject("Object 1",
                new Transform(new Vector2f(200,100), new Vector2f(256, 256)), 2);
        obj1.addComponent(new SpriteRenderer(
                new Sprite(
                      AssetPool.getTexture("assets/images/blendImage1.png")
                )
        ));
        this.addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400,100), new Vector2f(256, 256)), 1);
        obj2.addComponent(new SpriteRenderer(new Sprite(
                AssetPool.getTexture("assets/images/blendImage2.png")
        )));
        this.addGameObjectToScene(obj2);

        //cld inside asset pool make sure something has something with an assertion and a func check
    }
    private void loadResources(){

        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpritesheet("assets/images/spritesheet.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheet.png"),
                        16, 16, 26, 0));
    }
    private int spriteIndex = 0;
    private  float spriteFlipTime = 0.2f;
    private float spriteFlipTimeLeft = 0.0f;
    @Override
    public void update(float dt) {
        //System.out.println("FPS: "+ (1.0f/dt));
        //obj1.transform.position.x += 10 * dt;

        for (GameObject go : this.gameObjects){
           // System.out.println("called the update in game object");
            go.update(dt);
        }
        this.renderer.render();
    }
}
