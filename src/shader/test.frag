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
    vec3 ambientLight = 0.1 * lightSourcesColorArr[3];
    //vec3 ambientLight = vec3(0,0,0);

    //diffuse lighting
    vec3 diffuseLight = lightSourcesColorArr[0] * dot(normalize(lightDirection), normalize(normal));
    //vec3 diffuseLight = vec3(0,0,0);

    outColor = texture(texUnit, texCoord) * (vec4(diffuseLight, 1) + vec4(ambientLight,1));
}
