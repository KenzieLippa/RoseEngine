package rose;

public abstract class Component {
    public GameObject gameObject = null; // in case we arent added

    //concrete method that can be overridden if needed
    public void start(){
        //makes it so it doesnt have to be defined in order to inherit from here
    }
    public abstract void update(float dt);
}
