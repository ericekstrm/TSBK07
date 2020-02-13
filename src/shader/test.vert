#version 400 core

in vec3 in_Position;
in vec2 in_Texture;
in vec3 in_Normal;

uniform mat4 modelToWorld;
uniform mat4 worldToView;
uniform mat4 projection;

out vec2 texCoord;
out vec3 normal;

void main()
{
    normal = mat3(modelToWorld) * in_Normal;
    texCoord = in_Texture;
    gl_Position = projection * worldToView * modelToWorld * vec4(in_Position, 1.0);
}
