#version 400 core

in vec3 in_Position;
in vec2 in_Texture;
layout (location = 11) in vec3 offset;

uniform mat4 modelToView;
uniform mat4 projection;

out vec2 texCoord;

void main()
{
    texCoord = in_Texture;
    
    gl_Position = projection * modelToView * vec4(in_Position + offset, 1.0);
}
