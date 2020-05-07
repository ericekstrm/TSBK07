#version 400 core

in vec3 in_Position;
in vec2 in_Texture;
in vec3 in_Normal;

uniform mat4 modelToWorld;
uniform mat4 worldToView;
uniform mat4 projection;
uniform mat4 lightSpaceMatrix;

out vec2 texCoord;
out vec3 inNormal;
out vec3 fragPos;
out vec4 lightSpaceFragPos;
out vec4 viewSpace;

uniform vec4 clippingPlane;

void main()
{
    inNormal = mat3(transpose(inverse(modelToWorld))) * in_Normal;
    fragPos = vec3(modelToWorld * vec4(in_Position, 1));
    lightSpaceFragPos = lightSpaceMatrix * vec4(fragPos, 1);
    viewSpace = worldToView * modelToWorld * vec4(in_Position,1);

    gl_ClipDistance[0] = dot(vec4(fragPos, 1), clippingPlane);

    texCoord = in_Texture;
    gl_Position = projection * worldToView * modelToWorld * vec4(in_Position, 1);
}
