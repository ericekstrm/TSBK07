#version 330 core

in vec3 in_Position;
in vec2 texCoord;

out vec2 TexCoords;

uniform mat4 worldToView;
uniform mat4 projection;

void main()
{
    TexCoords = texCoord;
    gl_Position = projection * worldToView * vec4(in_Position, 1.0);
}
