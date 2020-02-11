#version 330 core

in vec3 TexCoords;

uniform samplerCube skybox;

out vec4 outColor;

void main()
{
    outColor = texture(skybox, TexCoords);
}