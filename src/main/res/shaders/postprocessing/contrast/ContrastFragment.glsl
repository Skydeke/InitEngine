#version 400

out vec4 fragColour;

in vec2 texCoord;

uniform sampler2D textureSampler;
uniform float factor;

void main(void) {
    fragColour = texture(textureSampler, texCoord);

    fragColour.rgb = (fragColour.rgb - .5) * factor + .5;
}
