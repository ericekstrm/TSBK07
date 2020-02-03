#version 400 core

in vec3 in_Position;
//in vec4 in_Color;
in vec2 in_Texture;

uniform mat4 rotation;
uniform mat4 projection;
uniform mat4 translation;
uniform mat4 worldToView;

//out vec4 color;
out vec2 texCoord;

void main()
{
    texCoord = in_Texture;
    gl_Position = projection * worldToView * translation * rotation * vec4(in_Position, 1.0);
    //color = in_Color;
}
