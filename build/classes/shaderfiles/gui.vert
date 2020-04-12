#version 400 core

in vec3 in_Position;
in vec2 in_Texture;

uniform mat4 transform;

out vec2 texCoord;

void main()
{
    texCoord = in_Texture;
    gl_Position = vec4(in_Position, 1.0);
}
