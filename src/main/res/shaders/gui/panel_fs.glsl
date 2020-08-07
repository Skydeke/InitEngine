#version 330

layout (location = 0) out vec4 fragColor;
in vec2 uv;

// PANEL UNIFORMS
uniform vec3 color;
uniform sampler2D texture;
uniform int isTexture;

uniform ivec4 rounding;
uniform ivec2 resolution;

uniform int isDepth;
const float zNear = 0.01, zFar = 100;

float linearize_depth(float d)
{
    return (2.0 * zNear) / (zFar + zNear - d * (zFar - zNear));
}

void main(){

    float alpha = 1.0f;

    float x_disp = float(rounding.x)/float(resolution.x);
    float y_disp = float(rounding.x)/float(resolution.y);
    vec2 center = vec2(0,1) + vec2(x_disp, -y_disp);
    if(uv.x < center.x && uv.y > center.y && rounding.x > 0){
    // top left
        vec2 uv_dist = center - uv;
        vec2 pixel_dist = vec2(uv_dist.x*float(resolution.x), uv_dist.y* float(resolution.y));;
        alpha = float(rounding.y) - length(pixel_dist);
        if(alpha < 0) discard;
        alpha = min(alpha, 1);
    }

    x_disp = float(rounding.y)/float(resolution.x);
    y_disp = float(rounding.y)/float(resolution.y);
    center = vec2(1,1) + vec2(-x_disp, -y_disp);
    if(uv.x > center.x && uv.y > center.y &&  rounding.y > 0){
    // top right
        vec2 uv_dist = center - uv;
        vec2 pixel_dist = vec2(uv_dist.x*float(resolution.x), uv_dist.y* float(resolution.y));;
        alpha = float(rounding.y) - length(pixel_dist);
        if(alpha < 0) discard;
        alpha = min(alpha, 1);
    }

    x_disp = float(rounding.z)/float(resolution.x);
    y_disp = float(rounding.z)/float(resolution.y);
    center = vec2(0,0) + vec2(x_disp, y_disp);
    if(uv.x < center.x && uv.y < center.y && rounding.z > 0){
    // bottom left
        vec2 uv_dist = center - uv;
        vec2 pixel_dist = vec2(uv_dist.x*float(resolution.x), uv_dist.y* float(resolution.y));;
        alpha = float(rounding.z) - length(pixel_dist);
        if(alpha < 0) discard;
        alpha = min(alpha, 1);
    }

    x_disp = float(rounding.w)/float(resolution.x);
    y_disp = float(rounding.w)/float(resolution.y);
    center = vec2(1,0) + vec2(-x_disp, y_disp);
    if(uv.x > center.x && uv.y < center.y && rounding.w > 0){
         // bottom right
         vec2 uv_dist = center - uv;
         vec2 pixel_dist = vec2(uv_dist.x*float(resolution.x), uv_dist.y* float(resolution.y));;
         alpha = float(rounding.w) - length(pixel_dist);
         if(alpha < 0) discard;
         alpha = min(alpha, 1);
     }

    if(isTexture == 1){
        vec2 tex = vec2(uv.x, 1 - uv.y);
        if(isDepth == 1){
            fragColor = vec4(vec3(linearize_depth(texture2D(texture, tex).r)),1.0);
        } else {
            fragColor = vec4(texture2D(texture, tex).rgb, 1);
        }

    } else fragColor = vec4(color, alpha);

    ivec2 coord = ivec2(uv.x * resolution.x, uv.y * resolution.y);

//    if(coord.x < 1 || coord.x >= resolution.x - 1 || coord.y < 1 || coord.y >= resolution.y -1)
//        fragColor = vec4(borderColor*1.5, alpha);
//    fragColor = vec4(1, 0, 0, 1);
}