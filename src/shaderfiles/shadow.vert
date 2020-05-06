#version 330 core

in vec3 in_Position;

uniform mat4 modelToView;
uniform mat4 projection;

void main()
{
    gl_Position = projection * modelToView * vec4(in_Position, 1.0);
}
