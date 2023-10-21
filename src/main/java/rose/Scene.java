package rose;

import renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {
    protected Renderer renderer = new Renderer();
    //impliments a couple methods we would like it to
    protected Camera camera; //used only by the scene
    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();
    public Scene(){

    }
    public void init(){

    }
    //lots of things will be in here eventually
    public void start(){
        //called by window when scene has finished initializing
        for(GameObject go : gameObjects){
            //start all game objects when scene starts
            go.start();
            this.renderer.add(go); //add all go to render
        }
        //it is now running
        isRunning = true;
    }
    public void addGameObjectToScene(GameObject go){
        if(!isRunning){
            //if not running add to the list
            gameObjects.add(go);
        }else{
            gameObjects.add(go);
            go.start(); // call immediately so it can get all required refrences
            this.renderer.add(go);
        }

    }

    public abstract void update(float dt);
    public Camera camera(){
        return this.camera;
    }
}
