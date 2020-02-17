#version 330 core

in vec2 TexCoords;

uniform sampler2D texUnit;

out vec4 outColor;

void main()
{
    outColor = texture(texUnit, TexCoords);
}