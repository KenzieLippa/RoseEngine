package components;

import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.Texture;
import rose.Component;
import rose.Transform;

public class SpriteRenderer extends Component {
    //need to take in a color
    //if they change we will know because they are private
    private Vector4f color;
//    private Vector2f[] texCoords;
//    //texture refrence
//    private Texture texture = null;
    private Sprite sprite;
    //keep track of th transform
    private Transform lastTransform;
    private boolean isDirty = false;


    public SpriteRenderer(Vector4f color){

        this.color = color;
        this.sprite = new Sprite(null);
       // this.texture = null;
        this.isDirty = true; //have to set initially so it will draw
    }
    public SpriteRenderer(Sprite sprite){
        this.sprite = sprite;
        this.color = new Vector4f(1,1,1,1);
        this.isDirty = true; //set when created so will be drawn
    }
   //private boolean firstTime = true;
    @Override
    public void start() {
       // System.out.println("I am starting");
        //can access everything we made
        this.lastTransform = gameObject.transform.copy();
        //wouldnt have access in constructor because go doesnt exist then
    }

    @Override
    public void update(float dt) {
//        if (firstTime){
//            System.out.println("I am updating");
//            firstTime = false;
//        }

        if(!this.lastTransform.equals(this.gameObject.transform)){
            //if has changed position or scale then update value
            this.gameObject.transform.copy(this.lastTransform);
            //set flag to dirty because something changed
            isDirty = true;
        }
    }
    public Vector4f getColor(){
        return this.color;
    }
    public Texture getTexture(){
        return sprite.getTexture();
    }

    public Vector2f[] getTexCoords() {
//        Vector2f[] texCoords = {
//                new Vector2f(1,1),
//                new Vector2f(1,0),
//                new Vector2f(0,0),
//                new Vector2f(0,1)
//        };//not th best because being made each time we call this
        return sprite.getTexCoords();
    }
    public void setSprite(Sprite sprite){
        this.sprite = sprite;
        //if this is called we changed the sprite
        //set flag to dirty
        this.isDirty = true;
    }
    public void setColor(Vector4f color){

        //copies directly into it
        //set flag to dirty
        //make sure its actually different
        if(!this.color.equals(color)){
            this.isDirty = true;
            this.color.set(color);
        }

    }
    public boolean isDirty(){
        return isDirty;
    }
    public void setClean(){
        this.isDirty = false;
    }
}
