#version 400 core

//in vec4 color;

uniform vec3 color;

out vec4 outColor;

void main()
{
    outColor = vec4(color, 1.0);
}
