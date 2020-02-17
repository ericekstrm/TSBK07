#version 400 core

in vec3 in_Position;
//in vec4 in_Color;

uniform mat4 modelToWorld;
uniform mat4 worldToView;
uniform mat4 projection;

//out vec4 color;

void main()
{
    //color = in_Color;
    gl_Position = projection * worldToView * modelToWorld * vec4(in_Position, 1.0);
}
