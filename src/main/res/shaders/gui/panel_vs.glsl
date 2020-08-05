#version 330

struct Box {
    float x;
    float y;
    float width;
    float height;
};

layout (location = 0) in vec2 pos;
out vec2 uv;
uniform Box box;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform int isBuffer;

void main() {

    vec3 posOnScreen = vec3((box.width * pos.x) + box.x, (box.height * pos.y) + box.y, 0);
    vec4 finalPos = vec4(posOnScreen.x*2-1,posOnScreen.y*2-1, posOnScreen.z*2-1, 1);
    gl_Position = finalPos;

    if(isBuffer == 1){
        uv = vec2(pos.x, -pos.y);
    }else{
        uv = vec2(pos.x, pos.y);
    }
}