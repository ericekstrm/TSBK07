#version 400 core

//in vec4 color;
in vec2 texCoord;
in vec3 normal;
in vec3 lightDirection;

uniform sampler2D texUnit;

//all 4 light sources used for now
uniform vec3 lightSourcesDirPosArr[4];
uniform vec3 lightSourcesColorArr[4];
uniform float specularExponent;
uniform bool isDirectional[4];

out vec4 outColor;

void main()
{
    //set the ambient light as the sum of the light from all lightsources (just one for now)
    //with RGB components times a constant.
    vec3 ambientLight = 0.1 * lightSourcesColorArr[0];

    //diffuse lighting
    vec3 lightdir =  normalize(lightDirection);
    vec3 diffuseLight = lightdir * normalize(normal);

    outColor = 0.1 * texture(texUnit, texCoord) + vec4(ambientLight,1) + vec4(diffuseLight, 1) * 0.5;
}
