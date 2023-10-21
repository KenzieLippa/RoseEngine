package rose;

import org.joml.Vector2f;

public class Transform {
    public Vector2f position;
    public Vector2f scale;

    public Transform(){
        init(new Vector2f(), new Vector2f());
    }

    public Transform(Vector2f position){
        init(position, new Vector2f());
    }
    public Transform(Vector2f position, Vector2f scale){
        init(position, scale);
    }
    //if func to overload have a main func then several that overload
    public void init(Vector2f position, Vector2f scale){
        this.position = position;
        this.scale = scale;
    }
    public Transform copy(){
        //copies contents into a new vector
        return new Transform(new Vector2f(this.position), new Vector2f(this.scale));
    }
    public void copy(Transform to){
        //copies self to what ever we pass in
        to.position.set(this.position);
        to.scale.set(this.scale);
    }
    @Override
    public boolean equals(Object o){
        if(o == null) return false;
        if (!(o instanceof  Transform)) return false;
        //checks if two transforms are equal
        Transform t = (Transform)o;
        return t.position.equals(this.position) && t.scale.equals(this.scale);
    }
}
