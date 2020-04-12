#version 400 core

in vec2 texCoord;
in vec3 varying_normal;
in vec3 fragPos;
in vec4 viewSpace;

uniform sampler2D texUnit;

out vec4 outColor;

void main()
{
    outColor = vec4(1, 0, 0, 1);
    outColor = texture(texUnit, texCoord);
}
