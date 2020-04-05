#version 400 core

in vec3 in_Position;
in vec2 in_Texture;

uniform mat4 modelToWorld;
uniform mat4 worldToView;
uniform mat4 projection;
uniform vec3 cameraPos;
uniform vec3 lightPosition;

out vec4 clipSpace;
out vec2 texCoords;
out vec3 toCameraVector;
out vec3 fromLightVector;

void main()
{
    vec4 worldPos = modelToWorld * vec4(in_Position, 1.0);
    texCoords = in_Texture;
    gl_Position = projection * worldToView * worldPos;
    clipSpace = gl_Position;
    toCameraVector = cameraPos - worldPos.xyz;
    fromLightVector = worldPos.xyz - lightPosition;
}
