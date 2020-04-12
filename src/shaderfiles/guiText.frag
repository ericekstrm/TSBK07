#version 400 core

in vec2 texCoord;
in vec3 varying_normal;
in vec3 fragPos;
in vec4 viewSpace;

uniform sampler2D texUnit;

uniform vec3 textColor;

out vec4 outColor;

void main()
{
    outColor = texture(texUnit, texCoord) * vec4(textColor, 1);
}
