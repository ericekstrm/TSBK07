#version 400 core

in vec2 texCoord;
in vec3 normal;
in vec3 fragPos;
in vec4 viewSpace;

uniform sampler2D texUnit;

//light properties
uniform vec3 pointLightPosArr[2];
uniform vec3 pointLightColorArr[2];
uniform float Kc;
uniform float Kl;
uniform float Kq;

uniform vec3 dirLightDirArr[2];
uniform vec3 dirLightColorArr[2];

//material properties
uniform float Ka;
uniform float Kd;
uniform float Ks;
uniform float specularExponent;

uniform vec3 viewPos;

out vec4 outColor;

void main()
{
    vec3 ambientLight = vec3(0,0,0);
    vec3 diffuseLight = vec3(0,0,0);
    vec3 specularLight = vec3(0,0,0);
    
    //======================================================
    //Point lights
    for(int i = 0; i < pointLightPosArr.length(); i++)
    {
        float Kc = 1;
        float Kl = 0.045;
        float Kq = 0.0075;
        float distance = length(pointLightPosArr[i] - fragPos);
        float attenuation = 1.0 / (Kc + Kl * distance + Kq * (distance * distance)); 

        //ambient light
        ambientLight += attenuation * Ka * pointLightColorArr[i];

    	//diffuse lighting
        vec3 lightDir = normalize(pointLightPosArr[i] - fragPos);
        
        float diff = max(0.0, dot(normalize(normal), lightDir));
        diffuseLight += attenuation * Kd * diff * pointLightColorArr[i];

        //specular lighting
        vec3 viewDir = normalize(viewPos - fragPos);
        vec3 reflectDir = reflect(-lightDir, normalize(normal));

        float spec = pow(max(0.0, dot(viewDir, reflectDir)), specularExponent); //128 ska va nåt som bestäms för varje objekt
        specularLight += attenuation * Ks * spec * pointLightColorArr[i];
    }

    //===================================================
    //Directional lights
    for(int i = 0; i < dirLightDirArr.length(); i++)
    {
        vec3 lightDir = dirLightDirArr[i];
        
        //diffuse lighting
        float diff = max(0.0, dot(normalize(normal), lightDir));
        diffuseLight += Kd * diff * dirLightColorArr[i];
    }
    
    vec3 lightColor = (ambientLight + diffuseLight + specularLight) * vec3(texture(texUnit, texCoord));
 
    //=====================================================
    //compute distance used in fog equations
    float dist = abs(viewSpace.z);
    
    //linear fog
    // 20 - fog starts; 80 - fog ends
    float fogStart = 120;
    float fogEnd = 500;
    float fogFactor = (fogEnd - dist)/(fogEnd - fogStart);
    fogFactor = clamp( fogFactor, 0.0, 1.0 );
    vec3 fogColor = vec3(91.0/255.0, 142/255.0, 194.0/255.0);
 
    //if you inverse color in glsl mix function you have to
    //put 1.0 - fogFactor
    vec3 finalColor = mix(fogColor, lightColor, fogFactor);

    outColor = vec4(finalColor, 1);
}
