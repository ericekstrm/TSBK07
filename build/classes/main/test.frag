#version 400 core

//in vec4 color;
in vec2 texCoord;

out vec4 outColor;

void main()
{
    outColor = vec4(texCoord, 0, 1);
}
