package renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {
    //id that specific shader prog is dealing with
    private int shaderProgramID;
    private boolean beingUsed = false;

    private String vertexSource;
    private String fragmentSource;
    //when do they get values
    private String filepath;
    public Shader(String filepath){
        this.filepath = filepath;
        try{
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            //System.out.println(source + "done!");
            //seems to be error here
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");
            //System.out.println(splitString);
            //find the first pattern after #type
            int index = source.indexOf("#type") + 6; //immediately after the type part
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, eol).trim(); //takes off any whitespace added
            //System.out.println(firstPattern); works
            //finding the end pattern after #type
            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();
            //System.out.println(secondPattern); was a problem now fixed but still broken
            if(firstPattern.equals("vertex")){
                //first val has garbage
                //causing a crash
                vertexSource = splitString[1];
                //System.out.println("made it past here");
            }else if(firstPattern.equals("fragment")){
                fragmentSource = splitString[1];
            }else {
                throw new IOException("unexpected token "+firstPattern + " in " +filepath);
            }

            if(secondPattern.equals("vertex")){
                //first val has garbage
                vertexSource = splitString[2];
            }else if(secondPattern.equals("fragment")){
                fragmentSource = splitString[2];
            }else {
                throw new IOException("unexpected token "+secondPattern + " in " +filepath);
            }
        }catch(IOException e){
            e.printStackTrace();
            assert false : "ERROR could not open file for shader: "+filepath;
        }
        //might need to fix assertions if they arent working but idk if they are or not
//        System.out.println(vertexSource);
//        System.out.println(fragmentSource);
    }
    public void compile(){
        //=========================================
        //Compile and link shaders
        //=========================================
        int vertexID, fragmentID;
        //compile and link shaders
        //first load and compile vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        //pass to gpu for compilation
        //pass src to vertexid
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);

        //check for errors
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if(success == GL_FALSE) {
            //first get length of strength
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            //then print out error message
            System.out.println("ERROR: defaultShader.glsl, \n\tVertex shader compilation failed");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : ""; //break out of program
        }
        //same ish for fragment shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        //pass to gpu for compilation
        //pass src to vertexid
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);

        //check for errors
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if(success == GL_FALSE) {
            //first get length of strength
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            //then print out error message
            System.out.println("ERROR: '" + filepath+"', \n\tFragment shader compilation failed");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : ""; //break out of program
        }
        //now create actual linker
        //link shaders and check for errors
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        //link the prog
        glLinkProgram(shaderProgramID);

        //check for errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if(success == GL_FALSE){
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '"+filepath+"' , \n\tlinking of shaders failed");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
            assert false : ""; //break out of program

        }
    }
    public void use(){
        //can draw it
        //bind shader prog
        if (!beingUsed) {
            //not binding if already bound to gpu
            glUseProgram(shaderProgramID);
            beingUsed = true;
        }
    }
    public void detach(){
        glUseProgram(0);
        beingUsed = false; //set back to false
    }

    public void uploadMat4f(String varName, Matrix4f mat4){
        //first have to find the variable location
        int varLocation = glGetUniformLocation(shaderProgramID, varName); //pass it what we are looking for
        //make sure we use the shader so this all works
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        //flatten so in a 1d array, create a buffer 16 var wide and then stuff everythign in there
        mat4.get(matBuffer); //have to upload a buffer to work
        //transpose is linear algebraic func
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }
    public void uploadMat3f(String varName, Matrix3f mat3){
        //first have to find the variable location
        int varLocation = glGetUniformLocation(shaderProgramID, varName); //pass it what we are looking for
        //make sure we use the shader so this all works
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        //flatten so in a 1d array, create a buffer 16 var wide and then stuff everythign in there
        mat3.get(matBuffer); //have to upload a buffer to work
        //transpose is linear algebraic func
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }
    public void uploadVec4f(String varName, Vector4f vec){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }
    public void uploadVec3f(String varName, Vector3f vec){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }
    public void uploadVec2f(String varName, Vector2f vec){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform2f(varLocation, vec.x, vec.y);
    }
    public void uploadFloat(String varName, float val){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation, val);
    }
    public void uploadInt(String varName, int val){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, val);
    }
    public void uploadTexture(String varName, int slot){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, slot);
    }
    public void uploadIntArray(String varName, int[] array){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        //this is a value pointer
        glUniform1iv(varLocation, array);
    }
}
