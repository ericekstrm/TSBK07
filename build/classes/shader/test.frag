#version 400 core

in vec2 texCoord;
in vec3 normal;
in vec3 fragPos;

uniform sampler2D texUnit;

//all 4 light sources used for now
uniform vec3 lightSourcesDirPosArr[4];
uniform vec3 lightSourcesColorArr[4];
uniform float specularExponent;
uniform bool isDirectional[4];

out vec4 outColor;

void main()
{
    float ambientStrength = 0.1;
    vec3 ambientLight = ambientStrength * lightSourcesColorArr[3];
    
    vec3 diffuseLight;
    for(int i = 0; i < 4; i++)
    {
        vec3 lightDir;
        if (isDirectional[i])
        {
            lightDir = lightSourcesDirPosArr[i];
        } else 
        {
           lightDir = normalize(lightSourcesDirPosArr[i] - fragPos);
        }
        float diff = max(0.0, dot(normalize(normal), lightDir));
        diffuseLight += diff * lightSourcesColorArr[i];
    }

    vec3 result = (ambientLight + diffuseLight) * vec3(texture(texUnit, texCoord));
    outColor = vec4(result, 0.1);

    //vec3 diffuseLight = lightSourcesColorArr[0] * dot(normalize(lightDirection), normalize(normal));
    //outColor = texture(texUnit, texCoord) * (vec4(diffuseLight, 1) + vec4(ambientLight,1));
}
