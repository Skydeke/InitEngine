#version 400

out vec3 fragColour;

in vec2 texCoord;

uniform sampler2D textureSampler;
uniform vec2 center;
uniform int samples;
uniform float factor;

vec2 calculateOffset() {
    return (center - texCoord) / (samples * factor);
}

void main(void) {
    vec2 offset = calculateOffset();

    vec3 finalColour = vec3(0);
    for(int i = 0; i < samples; i++) {
        vec2 coords = clamp(texCoord + i * offset, 0, 1);
        finalColour += texture(textureSampler, coords).rgb / samples;
    }
    fragColour = finalColour;
}
