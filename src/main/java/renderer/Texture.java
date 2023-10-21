package renderer;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    private String filepath;
    private int texID;
    private int width, height;
    //will want to load the textures with a filepath
    public Texture(String filepath){
        //build tex
        this.filepath = filepath;

        //generate texture on GPU
        //like buffers but gens tex id for us
        texID = glGenTextures();
        //next bind texture
        glBindTexture(GL_TEXTURE_2D, texID);

        //set textures parameters
        //repeat image in both directions
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); //basic parameters setting
        //basically wraps from 1 -> 2 then also from 1 ->0 so u for th x then t is y
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        //when stretching image pixelate instead of blur
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        //when shrinking an image we also want to pixelate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        //next load image
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        //load image upside down
        stbi_set_flip_vertically_on_load(true);
        //channels 3 for rgb and 4 for rga
        ByteBuffer image = stbi_load(filepath, width, height, channels, 0);// an image loading lib

        //if image loaded successfully
        if(image != null){
            //already storing it so just getting it up there
            this.width = width.get(0);
            this.height = height.get(0);
            if (channels.get(0) == 3) {
                //this is where we upload to gpu
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0),
                        0, GL_RGB, GL_UNSIGNED_BYTE, image);
            }else if (channels.get(0) ==4){
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0),
                        0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            }else{
                assert false : "ERROR: (Texture) UNKNOWN number of channels: "+channels.get(0);
            }
        }else{
            assert false : "Error: Texture could not load image " + filepath;
        }
        stbi_image_free(image); //not using java garbage collection, if you dont free then you might have mem leak


    }
    public void bind(){
        glBindTexture(GL_TEXTURE_2D, texID);
    }
    public void unbind(){
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getWidth(){
        return this.width;
    }
    public int getHeight(){
        return this.height;
    }
}
