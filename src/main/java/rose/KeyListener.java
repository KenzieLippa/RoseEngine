package rose;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {
    private static KeyListener instance; //make a private instance
    private boolean keyPressed[] = new boolean[350]; //number of keybinds glfw might have
    private KeyListener(){
        //makes sure none of the other classes can call the constructor
    }
    public static KeyListener get(){
        //the getter func for the instance key listener
        if(KeyListener.instance == null){
            KeyListener.instance = new KeyListener();
        }
        return KeyListener.instance; //returns the key listener so we can access it
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods){
        if (action ==GLFW_PRESS){
            //passes in a key
            get().keyPressed[key] = true;
        }else if (action == GLFW_RELEASE){
            get().keyPressed[key] = false;
            //when called if the key is released then it returns false
        }
    }
    public static boolean isKeyPressed(int keyCode){
        return get().keyPressed[keyCode];
        //might actually wanna know if th user passed in junk because they shldnt be able to
//        if(keyCode < get().keyPressed.length){
//            //if the user passes in valid key
//            return get().keyPressed[keyCode];
//        }else{
//            return false; //user passed in junk
//        }
    }

}
