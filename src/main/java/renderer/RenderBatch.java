package renderer;

import components.SpriteRenderer;
import org.joml.Vector2f;
import org.joml.Vector4f;
import rose.Window;
import util.AssetPool;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch implements Comparable<RenderBatch>{
    //wld do structs but java doesnt have it
    //Vertex
    //======
    //pos                   color                        tex Coords      tex id
    //float, float,         float, float, float, float   float,float     float
    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORDS_SIZE = 2;
    private final int TEX_ID_SIZE = 1;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int TEX_COORD_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEX_ID_OFFSET = TEX_COORD_OFFSET + TEX_COORDS_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = 9; //we have more now
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    //we will know exactly how many sprites one batch shld take in
    private SpriteRenderer[] sprites;
    //num of sprites we have in array
    private int numSprites;
    private boolean hasRoom; //do we have room
    private float[] vertices; //all th verticies in an array
    private int[] texSlots = {0,1,2,3,4,5,6,7}; //indexing into the array which indexes to the proper texs

    private List<Texture> textures; // list of all texture sheets
    private int vaoID, vboID;
    private int maxBatchSize;
    private Shader shader;
    private int zIndex;

    public RenderBatch(int maxBatchSize, int zIndex){
        this.zIndex = zIndex;
        //was creating a new shader each time we have a batch
        //fixed to now be refrencing one shader that was made a bit ago in assetPool
       shader = AssetPool.getShader("assets/shaders/default.glsl");
       shader.compile();
       this.sprites = new SpriteRenderer[maxBatchSize];
       //max amount of sprites we can hold in renderer//
        this.maxBatchSize = maxBatchSize;

        //4 verticies quads
        //four vert per quads, max batch number of quads and vertex sides
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numSprites = 0;
        this.hasRoom = true; //no sprites in here yet
        this.textures = new ArrayList<>();
    }
    public void start(){
        //allocate room on gpu and give us enough space
        //generate and bind a vertex array object
        vaoID = glGenVertexArrays(); //generates vao id for us we can use
        //now bind the object because we will be using it
        glBindVertexArray(vaoID); //binds to gpu

        //allocate space for verticies
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        //make sure you allocate enough space using the float size
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW); //might change so make sure
        // we can read and write efficiently

        //create and upload indicies buffer
        int eboID = glGenBuffers(); //reduce vertex duplication
        int[] indices = generateIndicies();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        //indicies will stay the same so doesnt matter where they are
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        //enable buffer attrib pointers, tells gpu what vertex looks like
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORD_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);
    }
    //add data to th array
    public void addSprite(SpriteRenderer spr){
        //get index and add renderobj
        //gets us the next open sprite
        int index = this.numSprites;
        this.sprites[index] = spr;
        this.numSprites++;

        if (spr.getTexture() != null){
            //if we have tex and hasnt been used then add it to th list
            if(!textures.contains(spr.getTexture())){
                textures.add(spr.getTexture());
            }
        }

        //add all properties to loval verticies array
        loadVertexProperties(index);

        //set full if needed
        if(numSprites >= this.maxBatchSize){
            this.hasRoom = false;
        }

    }
    public void render(){
        boolean rebufferData = false;
        for(int i =0; i < numSprites; i++){
            //loop through all th sprites
            SpriteRenderer spr = sprites[i];
            if(spr.isDirty()){
                loadVertexProperties(i);
                spr.setClean();
                rebufferData = true;
            }
        }
        if(rebufferData) {
            //for now we will rebuffer all data every frame
            //buffer data into vbo
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            //upload all verticies from 0
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }
        //should draw everything in vertex buffer
        //use shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());

        //bind all the textures
        for(int i = 0; i <textures.size(); i++){
            //get right slot
            glActiveTexture(GL_TEXTURE0 + i + 1); //activiate in appropriate slot
            textures.get(i).bind(); //bind that texture
        }

        //upload an int array
        //tell open gl that this is all th tex slots we are uploading
        shader.uploadIntArray("uTextures", texSlots);
        //bind the vertex array
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        //then draw
        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

        //then disable
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        //unbind all texs
        for(int i = 0; i <textures.size(); i++){
            textures.get(i).unbind(); //unbind that texture
        }

        shader.detach();
    }
    private void loadVertexProperties(int index){
        //create 4 verticies per quad
        //grab th sprite we just passed in
        SpriteRenderer sprite = this.sprites[index];

        //find offset within array (4 verticies per sprite)
        //locates the position of the vertex coords
        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();
        //get texture coords
        Vector2f[] texCoords = sprite.getTexCoords();


        int texID = 0;
        //only do if actually has a texture
        if(sprite.getTexture() != null) {
            for (int i = 0; i < textures.size(); i++) {
                //loop thro and match tex
                if(textures.get(i) == sprite.getTexture()){
                    texID = i + 1; //if no tex use 0
                    break; //mapped to the texture in th list
                }
            }
        }

        //add verticies with th appropriate properties
        //Adding four per quad so this is why we are looping
        float xAdd = 1.0f;
        float yAdd = 1.0f;

        for (int i=0; i<4; i++){
            //position from bottom left corner
            if(i == 1){
                yAdd = 0.0f;
            }else if(i == 2){
                xAdd = 0.0f;
            }else if(i == 3){
                yAdd = 1.0f;
            }
            //from where we position we find the appropriate coords

            //load position
            vertices[offset] = sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x);
            vertices[offset+1] = sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y);

            //load color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            //load texture coords
            vertices[offset + 6] = texCoords[i].x;
            vertices[offset + 7] = texCoords[i].y;

            //load tex id
            vertices[offset + 8] = texID;

            offset += VERTEX_SIZE;


        }

    }
    private int[] generateIndicies(){
        //6 indicies per quad because 3 per triangle
        int[] elements = new int[6*maxBatchSize];
        for(int i=0; i <maxBatchSize; i++){
            loadElementIndicies(elements, i);

        }
        //after loading th quads we return it
        return elements;
    }

    private void loadElementIndicies(int[] elements, int index){
        //find offsets
        int offsetArrayIndex = 6*index;
        int offset = 4 * index;
        //3,2,0,0,2,1       7,6,4,4,6,5

        //create triangle 1
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;

        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }

    public boolean hasRoom(){
        return this.hasRoom;
    }
    public boolean hasTextureRoom(){return this.textures.size() < 8;}

    //check to see if true
    public boolean hasTexture(Texture tex){
        return this.textures.contains(tex);
    }
    public int zIndex(){
        return this.zIndex;
    }

    @Override
    public int compareTo(RenderBatch o) {
        //if less then returns -1, same is 0 greater than is 1
        return Integer.compare(this.zIndex, o.zIndex);
    }
}
