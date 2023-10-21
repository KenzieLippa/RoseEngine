package util;

import components.Sprite;
import components.Spritesheet;
import renderer.Shader;
import renderer.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    //control in this class
    private static Map<String, Shader> shaders =  new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, Spritesheet> spritesheets = new HashMap<>();

//would want to call this in init to load everything
    public static Shader getShader(String resourceName){
        //do we have already
        File file = new File(resourceName);
        if (AssetPool.shaders.containsKey(file.getAbsolutePath())){
            //no confusion of the path
            return AssetPool.shaders.get(file.getAbsolutePath());
        }else{
            Shader shader = new Shader(resourceName);
            //compile it so we dont have to remeber
            shader.compile();
            //store the shader reference in the asset pool
            AssetPool.shaders.put(file.getAbsolutePath(), shader);
            return shader;
        }
    }
    public static Texture getTexture(String resourceName){
        File file = new File(resourceName);
        if (AssetPool.textures.containsKey(file.getAbsolutePath())){
            return AssetPool.textures.get(file.getAbsolutePath());
        }else{
            Texture texture = new Texture(resourceName);
            AssetPool.textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }
    //method to add sprite sheet
    public static void addSpritesheet(String resourceName, Spritesheet spritesheet){
        //want to make sure we only load one of these
        File file = new File(resourceName);
        if(!AssetPool.spritesheets.containsKey(file.getAbsolutePath())){
            AssetPool.spritesheets.put(file.getAbsolutePath(), spritesheet);
        }
    }
    public static Spritesheet getSpritesheet(String resourceName){
        File file = new File(resourceName);
        if(!AssetPool.spritesheets.containsKey(file.getAbsolutePath())){
            assert false : "Error: tried to access spritesheet and didn't add " + resourceName;
        }
        //default is null or get absolute path and return tht
        //th null texture will show up
        return AssetPool.spritesheets.getOrDefault(file.getAbsolutePath(), null);
    }
}
