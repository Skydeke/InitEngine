# version 430

layout (location = 0) out vec4 pos_vbo;
layout (location = 1) out vec4 norm_vbo;
layout (location = 2) out vec4 albedo_vbo;

in VS_DATA {
    vec2 uv;
    vec3 norm;
    vec3 pos;
} vs;

// material data

uniform vec3 albedoConst;
uniform sampler2D albedoTex;
uniform int isAlbedoMapped;


uniform vec3 basis = normalize(vec3(.5,.5,.5));


void main(){


    vec2 uv = vec2(vs.uv.x,1-vs.uv.y);
    vec3 norm = normalize(vs.norm);
    norm_vbo = vec4(norm, 1);
    pos_vbo = vec4(vs.pos, 1);

    if(isAlbedoMapped == 1){
        albedo_vbo =  texture(albedoTex, uv);
        if (albedo_vbo.a < 0.99)
            discard;
    } else {
        albedo_vbo = vec4(albedoConst, 1);
    }
}