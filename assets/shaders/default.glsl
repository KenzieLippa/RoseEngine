#type vertex
//need to find the end of th line to see what they have typed it as
#version 330 core
//anything with a is attribute
//pos to send to shader
layout (location=0) in vec3 aPos;
//color to send to shader
layout (location=1) in vec4 aColor;
//matches th info in render batching
layout(location=2) in vec2 aTexCoords;
layout(location=3) in float aTexId;

//layout (location=2) in vec2 aTexCoords;

uniform mat4 uProjection; //upload the two variables
uniform mat4 uView;

//var to pass to shader
out vec4 fColor; //f to shader
out vec2 fTexCoords;
out float fTexId;
//out vec2 fTexCoords;

//all have a main
void main(){
    //pass along
    fColor = aColor;
    fTexCoords = aTexCoords;
    fTexId = aTexId;
    //adjust from world coords (vec4) to normalized device coords
    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}
//pass from vertex to the fragment
#type fragment
#version 330 core

//uniform float uTime;
//uniform sampler2D TEX_SAMPLER;

in vec4 fColor; //have an out above so we need an in here
in vec2 fTexCoords;
in float fTexId;
//in vec2 fTexCoords;
uniform sampler2D uTextures[8]; //dont have to worry about running out of tex slots
//color we output
out vec4 color;
void main(){
//    float avg = (fColor.r + fColor.g + fColor.b) /3; //for black and white
//    color = vec4(avg, avg, avg, 1); //auto converts to black and white
    //fancy shit with noise
    //clamps between certain amount th 43758 num is a modulator and the two in th vec2 are the seeds
//    float noise = fract(sin(dot(fColor.xy, vec2(12.9898, 78.233)))*43758.5453);
//    color = fColor * noise;
    //color = texture(TEX_SAMPLER, fTexCoords); //samples what is on at tex coords
   //make sure theres an actual tex
    if(fTexId > 0){
        int id = int(fTexId);//cast tex coords to an int
        //allows for tinting
        color = fColor * texture(uTextures[id], fTexCoords);
        //color = vec4(fTexCoords, 0, 0);
    } else{
        color = fColor;
    }
}