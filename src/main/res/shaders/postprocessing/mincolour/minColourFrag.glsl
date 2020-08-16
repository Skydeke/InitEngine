#version 400

out vec4 fragColour;

in vec2 texCoord;

uniform sampler2D textureSampler;
uniform vec3 minColour;

vec4 getColour() {
    vec4 colour = texture(textureSampler, texCoord);
    bvec3 cmp = greaterThanEqual(colour.rgb, minColour);
    if (cmp == bvec3(true)) {
        return colour;
    } else {
        return vec4(0);
    }
}

void main(void) {

    fragColour = getColour();

}
