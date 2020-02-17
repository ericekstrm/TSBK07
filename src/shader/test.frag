#version 400 core

in vec2 texCoord;
in vec3 normal;
in vec3 fragPos;

uniform sampler2D texUnit;

uniform vec3 pointLightPosArr[2];
uniform vec3 pointLightColorArr[2];

uniform vec3 dirLightDirArr[2];
uniform vec3 dirLightColorArr[2];

uniform float specularExponent;

out vec4 outColor;

void main()
{
    float ambientStrength = 0.4;
    vec3 ambientLight = ambientStrength * vec3(1.0, 1.0, 1.0);
    

    vec3 diffuseLight = vec3(0,0,0);
    
    //Point lights
    for(int i = 0; i < pointLightPosArr.length(); i++)
    {
        vec3 lightDir = normalize(pointLightPosArr[i] - fragPos);
        
        float diff = max(0.0, dot(normalize(normal), lightDir));
        diffuseLight += diff * pointLightColorArr[i];
    }

    //Directional lights
    for(int i = 0; i < dirLightDirArr.length(); i++)
    {
        vec3 lightDir = dirLightDirArr[i];
        
        float diff = max(0.0, dot(normalize(normal), lightDir));
        diffuseLight += diff * dirLightColorArr[i];
    }

    vec3 result = (ambientLight + diffuseLight) * vec3(texture(texUnit, texCoord));
    outColor = vec4(result, 0.1);
}
