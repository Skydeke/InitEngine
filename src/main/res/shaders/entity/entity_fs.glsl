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

uniform int map_normal;
uniform sampler2D normalMap;

uniform vec3 basis = normalize(vec3(.5,.5,.5));


void main(){
    vec3 norm = vs.norm;
    vec2 uv = vec2(vs.uv.x,1-vs.uv.y);

    if(isAlbedoMapped == 1){
        albedo_vbo =  texture(albedoTex, uv);
        if (albedo_vbo.a < 0.99)
            discard;
    } else {
        albedo_vbo = vec4(albedoConst, 1);
    }

    vec3 norm_sample = normalize(2*texture(normalMap, uv).rgb - 1);
    if(map_normal == 1){
        // gram-schmidt process to transform normals from tangent space to world space
        vec3 tangent   = normalize(basis - norm * dot(basis, norm));
        vec3 bitangent = cross(norm, tangent);
        mat3 TBN       = mat3(tangent, bitangent, norm);

        norm = normalize(TBN * norm_sample);
    }
    else {
        // just use model normal
        norm = normalize(norm);
    }
    norm_vbo = vec4(norm, 1);
    pos_vbo = vec4(vs.pos, 1);
}