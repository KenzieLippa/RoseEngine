package components;

import org.joml.Vector2f;
import renderer.Texture;

import java.util.ArrayList;
import java.util.List;

public class Spritesheet {
    //parent tex of the overall image
    private Texture texture;
    private List<Sprite> sprites;

    public Spritesheet(Texture texture, int spriteWidth, int spriteHeight, int numSprites, int spacing){
        this.sprites = new ArrayList<>();

        this.texture = texture;
        int currentX = 0;
        //topleft sprite bottom right corner
        int currentY = texture.getHeight() - spriteHeight;
        for(int i = 0; i < numSprites; i++){
            float topY = (currentY + spriteHeight) / (float)texture.getHeight();
            float rightX = (currentX + spriteWidth) / (float)texture.getWidth();
            float leftX = currentX / (float)texture.getWidth(); //to normalize
            float bottomY = currentY / (float)texture.getHeight();

            Vector2f[] texCoords = {
                    new Vector2f(rightX,topY),
                    new Vector2f(rightX,bottomY),
                    new Vector2f(leftX,bottomY),
                    new Vector2f(leftX,topY)
            };
            Sprite sprite = new Sprite(this.texture, texCoords);
            this.sprites.add(sprite);
            //move over so can access th next one
            currentX += spriteWidth + spacing;
            if(currentX >= texture.getWidth()){
                //then reached th edge and then move y down
                currentX = 0;
                currentY -= spriteHeight + spacing;
            }
        }
    }
    public Sprite getSprite(int index){
        //get th current sprite
        return this.sprites.get(index);
    }
}
