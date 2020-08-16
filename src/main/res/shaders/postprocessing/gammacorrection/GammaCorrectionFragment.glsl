#version 400

out vec3 fragColour;

in vec2 texCoord;

uniform sampler2D textureSampler;
uniform float factor;

void main(void) {
    fragColour = texture(textureSampler, texCoord).rgb;

    fragColour = pow(fragColour, vec3(1 / factor));
}
