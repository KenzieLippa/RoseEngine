package renderer;

import components.SpriteRenderer;
import rose.GameObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;

    public Renderer(){
        this.batches = new ArrayList<>();
    }

    public void add(GameObject go){
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if(spr != null) {
            add(spr);
        }
    }
//need to sort all the batches
    private void add(SpriteRenderer sprite){
        //loop through current batches
        boolean added = false;
        for (RenderBatch batch : batches){
            //check if theres room and if the current zindex is the same as the sprites z index
            if(batch.hasRoom() && batch.zIndex() == sprite.gameObject.zIndex()){
                //will have a problem with possibly changing this in th future
                Texture tex = sprite.getTexture();
                if(tex == null || (batch.hasTexture(tex) || batch.hasTextureRoom())) {
                    batch.addSprite(sprite);
                    added = true;
                    break;
                }
            }
        }
//if no room
        if(!added){
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, sprite.gameObject.zIndex());
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);
            //makes sure they are always in the correct order
            Collections.sort(batches); //can also sort in reverse order
        }

    }
    public void render(){
        for(RenderBatch batch : batches){
            //draw the batches in sorted order
            batch.render();
        }
    }
}
