package components;

import rose.Component;

public class FontRenderer extends Component {

    //cant access gameobject in constructor because its null
    //cant add while being constructor

    @Override
    public void start(){
        if(gameObject.getComponent(SpriteRenderer.class) != null){
            System.out.println("Found Font renderer");
        }
    }

    @Override
    public void update(float dt) {

    }
}
