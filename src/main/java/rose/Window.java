package rose;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    //member variables
    private int width,height;
    private String title;
    private long glfwWindow;

    //this is our singleton so we only have one instance

    //testing thing
    public float r,g,b,a;
    private boolean fadeToBlack = false;
    private static Window window = null;

    //private static int currentSceneIndex = -1; //where we are currently
    private static Scene currentScene;

    //private constructor
    //dont want outside things to make it
    //only one window class, singleton only window can create
    private Window(){
        this.width = 1920;
        this.height = 1080;
        this.title = "Mario";
        r=1;
        b=1;
        g=1;
        a=1;
    }

    public static void changeScene(int newScene){
        //switch on new scene
        switch(newScene){
            case 0:
                currentScene = new LevelEditorScene();
                currentScene.init();
                //now start it
                currentScene.start();
                break;
            case 1:
                currentScene = new LevelScene();
                currentScene.init();
                currentScene.start();
                break;
            default:
                assert false : "unknown scene " + newScene;
                break;
        }
    }

    public static Window get(){
        if (Window.window == null){
            Window.window = new Window();

        }
        return Window.window;
        //only have one window this way
    }

    public static Scene getScene(){
        return  get().currentScene;
    }

    //create the run func
    public void run(){
        System.out.println("Hello LWGJL " + Version.getVersion());
        init();
        loop();

        //we allocate memory in the init func to store the window
        //when we are done we shld free it
        //make sure no more callbacks
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        //terminate GLFW and free th err callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
        //free the memory

    }
    public void init(){
        //set up an error callback
        //put bellow

        GLFWErrorCallback.createPrint(System.err).set();
        //init glfw
        if(!glfwInit()){
            throw new IllegalStateException("Unable to init glfw");

        }
        //config glfw
        glfwDefaultWindowHints();
        //im assuming the window properties
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); //ddnt have to import?
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        //now we create the window after setting the hints
        //this returns a fucking long for some reason
        //does this because its telling us where the window is in the memory space
        //dont care about the last two parameters
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        //make sure this worked
        if(glfwWindow == NULL){
            throw new IllegalStateException("Failed to create GLFW window");
        }

        //set up callbacks but we need a window before we can set a callback
        // :: is a java short hand for lambda, forwards stuff to a function
        //tells you to call them from the docs
        //can query for call event
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        //make the opengl context current
        glfwMakeContextCurrent(glfwWindow);

        //enable v-sync no wait time in between frames, goes from the computer
        glfwSwapInterval(1); //buffer swapping

        //make the window visible
        glfwShowWindow(glfwWindow);

        GL.createCapabilities(); //makes sure that we can use the stuff
        //CANNOT FORGET OR IT WILL BREAK

        //blends alpha
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        //make sure we are in a scene
        Window.changeScene(0);

    }
    public void loop(){
        //when frame began
        float beginTime = (float)glfwGetTime();
        float endTime;
        float dt = -1.0f;
        while(!glfwWindowShouldClose(glfwWindow)){
            //poll events, gets the key events
            glfwPollEvents();

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT); //flush th color to the whole screen
            //if greater than 0 we will update, otherwise not
            //gets initialized lower so cant run here unless we've had at least one frame
            if(dt >= 0){
                currentScene.update(dt); //pass in the delta time to the current scene
            }

//            if (fadeToBlack){
//               r = Math.max(r-0.01f, 0);
//               g = Math.max(g -0.01f,0);
//               b = Math.max(b-0.01f,0);
//            }
           //testing our input
//            if(KeyListener.isKeyPressed(GLFW_KEY_SPACE)){
//                //System.out.println("Space was pressed and that means key's are listening");
//                fadeToBlack = true;
//            }
            glfwSwapBuffers(glfwWindow); //will be handled and we dont have to worry about it
            //get time at end of frame
            endTime = (float)glfwGetTime();
            dt = endTime - beginTime; //gets the time it took to complete the frame
            beginTime = endTime; //set our current time as the new next time
        }
    }
}
