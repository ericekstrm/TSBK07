#version 330 core

in vec3 in_Position;
in vec2 in_Texture;

uniform mat4 modelToView;
uniform mat4 projection;

out vec2 texCoords;

void main()
{
    gl_Position = projection * modelToView * vec4(in_Position, 1.0);
    texCoords = in_Texture;
}
