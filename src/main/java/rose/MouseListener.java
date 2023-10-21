package rose;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    //private singleton so this is the instance
    //all these things are there to capture every possible thing the mouse could do
    private static MouseListener instance;
    private double scrollX, scrollY;
    private double xPos, yPos, lastY, lastX;
    //an array to catch the three boolean values of mouse but in case theres more you'll need a catch
    private boolean mouseButtonPressed[] = new boolean[3];
    private boolean isDragging;

    //will need more when theres projections
    private MouseListener(){
        //private constructor to ensure that only this class can make this class
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;

    }
    public static MouseListener get(){
        //if no mouse listener then make one
        //mouselistener.instance used for extra specifications
        if(MouseListener.instance == null){
            //cannot be instantiated elsewhere
            MouseListener.instance = new MouseListener(); //instantiate it here
        }
        return MouseListener.instance;
    }
    //implimenting mouse listener methods found on GLFW website
    //need to match the callback or it wont work
    public static void mousePosCallback(long window, double xpos, double ypos){
        //setting the last x and last y before changing
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        //now setting the variables
        get().xPos = xpos;
        get().yPos = ypos;
        //figure out if dragging, if mouse is down and moves then dragging
        //if any true then the whole thing is true
        get().isDragging = get().mouseButtonPressed[0] || get().mouseButtonPressed[1] || get().mouseButtonPressed[2];

    }
    public static void mouseButtonCallback(long window, int button, int action, int mods){
        //mods would be keyboard modifiers like hitting control click or alt click
        //get function gets the instance and therefore the current instance values
        if(action == GLFW_PRESS){
            //check stupid mouses
            if(button < get().mouseButtonPressed.length){
                get().mouseButtonPressed[button] = true;
            }

        }else if (action == GLFW_RELEASE){
            if(button < get().mouseButtonPressed.length){
                get().mouseButtonPressed[button] = false;
                get().isDragging = false; //cant be dragging if its released
            }
        }


    }
    public static void mouseScrollCallback(long window, double xOffset, double yOffset){
        //making all static so we dont have to type as much ;P
        //can call all statically
        //is easier if they are static when calling
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }
    public static void endFrame(){
        //to end the frame and reset
        //reset the scroll
        get().scrollX = 0;
        get().scrollY = 0;
        //reset the delta
        //when we sub where we are from where we used to be we shld get 0 this way
        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }
    //getter funcs for the x and y pos
    public static float getx(){
       return(float)get().xPos;
    }

    public static float gety(){
        return(float)get().yPos;
    }

    public static float getDx(){
        //amount of elapsed x pos in th current frame
        return(float)(get().lastX- get().xPos);
    }
    public static float getDy(){
        //amount of elapsed x pos in th current frame
        return(float)(get().lastY- get().yPos);
    }
    public static float getScrollX(){
        //scroll index on x
        return (float)get().scrollX;
    }
    public static float getScrollY(){
        //scroll index on x
        return (float)get().scrollY;
    }
    public static boolean isDragging(){
        return get().isDragging;
    }
    //mouse button down
    public static boolean mouseButtonDown(int button){
        //take in int for button bc glfw gives us this
        //make sure the button is in the range here too
        if (button<get().mouseButtonPressed.length){
            return get().mouseButtonPressed[button];
        }else{
            return false; //button outside range of possible buttons
        }
    }
    //calls button and mouse position every time mouse is moved
}
