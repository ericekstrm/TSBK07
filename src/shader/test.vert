#version 400 core

in vec3 in_Position;
in vec2 in_Texture;
in vec3 in_Normal;

uniform mat4 modelToWorld;
uniform mat4 worldToView;
uniform mat4 projection;

//light stuff
uniform vec3 lightSourcesDirPosArr[4];

out vec2 texCoord;
out vec3 normal;
out vec3 lightDirection;

void main()
{
    normal = mat3(modelToWorld) * in_Normal;

    lightDirection = lightSourcesDirPosArr[0] - in_Position;
    texCoord = in_Texture;
    gl_Position = projection * worldToView * modelToWorld * vec4(in_Position, 1.0);
}
