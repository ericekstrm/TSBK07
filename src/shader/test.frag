#version 400 core

//in vec4 color;
in vec2 texCoord;

uniform sampler2D texUnit;

//all 4 light sources used for now
uniform vec3 lightSourcesDirPosArr[4];
uniform vec3 lightSourcesColorArr[4];
uniform float specularExponent;
uniform bool isDirectional[4];

out vec4 outColor;

void main()
{
    outColor = texture(texUnit, texCoord) + 0.5*vec4(lightSourcesColorArr[0], 1);
}
