package rose;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

    private String name;
    private List<Component> components;
    public Transform transform;
    private int zIndex;

    //init this
    public GameObject(String name){
        //can add things as we get them
        this.name = name;
        //init components
        this.components = new ArrayList<>();
        this.transform = new Transform();
        this.zIndex = 0;
    }
    public GameObject(String name, Transform transform, int zIndex){
        //can add things as we get them
        this.name = name;
        this.zIndex = zIndex;
        //init components
        this.components = new ArrayList<>();
        this.transform = transform;
    }
    //first get component
    //have an abstract class T that extends component
    //sub class of component then take something in that is like that type
    public <T extends  Component> T getComponent(Class<T> componentClass){
        //for component c in components
        for (Component c : components){
            //if the class we passed in is assignable from the current component we are looking at
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c); //that way we get this as the appropriate class

                }catch(ClassCastException e){
                    e.printStackTrace();
                    assert false : "Error casting component (game object)";
                }
            }
        }
        return null; //if none are found


    }

    public <T extends Component> void removeComponent(Class<T> componentClass){
        for(int i = 0; i < components.size(); i++){
            Component c = components.get(i);
            if (componentClass.isAssignableFrom(c.getClass())){
                components.remove(i);
                return;
            }

        }
    }
    public void addComponent (Component c){
        this.components.add(c);
        c.gameObject = this; //refrence to c's game object to be this
    }

    public void update(float dt){
        for (int i=0; i < components.size(); i++){
            //run update func on each component that object has
            components.get(i).update(dt);
            //System.out.println("am calling update");
        }
    }
    public void start(){
        for(int i=0; i < components.size(); i++){
            components.get(i).start();
        }
    }
    public int zIndex(){
        return this.zIndex;
    }
}
