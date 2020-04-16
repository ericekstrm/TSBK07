#version 330 core

in vec3 TexCoords;

uniform samplerCube texUnit;

out vec4 outColor;

void main()
{
    outColor = texture(texUnit, TexCoords);
}
