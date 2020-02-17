#version 400 core

in vec3 in_Position;
in vec2 in_Texture;
in vec3 in_Normal;

uniform mat4 modelToWorld;
uniform mat4 worldToView;
uniform mat4 projection;

out vec2 texCoord;
out vec3 normal;
out vec3 fragPos;

void main()
{
    normal = mat3(transpose(inverse(modelToWorld))) * in_Normal;
    fragPos = vec3(modelToWorld * vec4(in_Position, 1.0));

    texCoord = in_Texture;
    gl_Position = projection * worldToView * modelToWorld * vec4(in_Position, 1.0);
}
