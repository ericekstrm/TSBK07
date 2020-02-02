#version 400 core

in vec3 in_Position;
in vec4 in_Color;

uniform mat4 rotation;

out vec4 color;

void main()
{
    gl_Position = rotation*vec4(in_Position, 1.0);
    color = in_Color;
}
