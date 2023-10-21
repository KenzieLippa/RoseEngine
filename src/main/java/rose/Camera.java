package rose;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
    private Matrix4f projectionMatrix, viewMatrix;
    public Vector2f position; //for testing purposes

    //the constructor, takes in a position
    public Camera(Vector2f position){
        this.position = position;

        //initialize the matricies
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        //adjust projection so when we have it then we set it up
        adjustProjection();
    }

    //how far do we want to be and what type of view

    //if the window size changes and we want to update
    //define how many units we want our world space to be
    public void adjustProjection(){

        //can change just by calling functions on it
        //have to be careful because mlut and things can change this value
        //gives us an identity matrix, get itself if you mult by it
        projectionMatrix.identity();
        //grid tiles are 32 units
        //viewing up to 100 with these dimmensions
        projectionMatrix.ortho(0.0f, 32.0f * 40.0f, 0.0f, 32.0f * 21.0f, 0.0f, 100.0f);

    }
    //where camera is in world space
    //where is th front of th camera
    //defines how the camera is looking
    public Matrix4f getViewMatrix(){
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f); //make it look a certain direction for up
        this.viewMatrix.identity();
        //where is it looking at, what is it looking towards
        viewMatrix = viewMatrix.lookAt(new Vector3f(position.x, position.y, 20.0f),
                cameraFront.add(position.x, position.y, 0.0f),
                cameraUp); //creates a view matrix, have up to 20 z coords to work with
        return this.viewMatrix; //return th view  matrix tht we made
    }
public Matrix4f getProjectionMatrix(){
        return this.projectionMatrix;
}
}
