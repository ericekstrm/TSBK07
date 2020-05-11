#version 400 core

in vec2 texCoord;
in vec3 inNormal;
in vec3 fragPos;
in vec4 lightSpaceFragPos;
in vec4 viewSpace;

//textures
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendmap;

uniform sampler2D shadowMap;

//light properties
uniform vec3 pointLightPosArr[4];
uniform vec3 pointLightColorArr[4];
uniform float r[4];
uniform float intensity[4];

uniform vec3 dirLightDirArr[4];
uniform vec3 dirLightColorArr[4];

//material properties
uniform vec3 Ka; //ambient color
uniform vec3 Kd; //diffuse color
uniform vec3 Ks; //specular color
uniform float specularExponent;

uniform vec3 viewPos;

out vec4 outColor;

//forward declaration of functions
vec3 calcLight(vec3 , vec3 , vec3 , vec3 , vec3, vec3 );
vec3 applyFog(in vec3,in float);
float calcShadow(vec4);

void main()
{
    //blend the correct texture for the fragment
    vec4 blend = texture(blendmap, texCoord);
    vec2 tiledTexCoord = texCoord * 150;
    vec4 texColor = texture(rTexture, tiledTexCoord) * blend.r + 
                    texture(gTexture, tiledTexCoord) * blend.g +
                    texture(bTexture, tiledTexCoord) * blend.b;

    vec3 normal = normalize(inNormal);
    vec3 viewDir = normalize(viewPos - fragPos);
    vec3 matDiffuse = texColor.xyz;
    vec3 matSpecular = Ks;

    vec3 output = vec3(0, 0, 0);

    //ambient light
    output += vec3(0.2,0.2,0.2) * matDiffuse;

    //==================
    //Directional lights
    for(int i = 0; i < dirLightDirArr.length(); i++)
    {
        vec3 lightDir = -dirLightDirArr[i];
        
        float shadow = calcShadow(lightSpaceFragPos);

        output += (1 - shadow) * calcLight(matDiffuse, matSpecular, normal, lightDir, dirLightColorArr[i], viewDir);
    }

    //==================
    //Positional Lights
    for (int i = 0; i < pointLightPosArr.length(); i++)
    {
        vec3 lightDir = normalize(pointLightPosArr[i] - fragPos);

        float Kc = 1;
	float Kl = 2 / r[i];
    	float Kq = 1 / (r[i] * r[i]);
        float dis = length(pointLightPosArr[i] - fragPos);
        float attenuation = 1.0 / (Kc + Kl * dis + 
  			     Kq * (dis * dis));  

        output += attenuation * calcLight(matDiffuse, matSpecular, normal, lightDir, pointLightColorArr[i], viewDir);
    }
 
    //==================

    float dist = abs(viewSpace.z);
    //output = applyFog(output, dist, viewPos, viewDir);
    output = applyFog(output, dist);
    
    outColor = vec4(output, 1);
}

//==========================| Calculate Light |=================================
vec3 calcLight(vec3 matDiffuse, vec3 matSpecular, vec3 normal, vec3 lightDir, vec3 lightColor, vec3 viewDir)
{
    //diffuse lighting
    float diff = max(0.0, dot(normal, lightDir));
    
    //specular lighting
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(0.0, dot(viewDir, reflectDir)), specularExponent);

    vec3 diffuse = lightColor * diff * matDiffuse;
    vec3 specular = lightColor * spec * matSpecular;
    return (diffuse + specular);
}

//==========================| Calculate Shadow |================================
float calcShadow(vec4 lightSpaceFragPos)
{
    float offset = 0.0004;

    // perform perspective divide
    vec3 projCoords = lightSpaceFragPos.xyz / lightSpaceFragPos.w;

    // transform to [0,1] range
    projCoords = projCoords * 0.5 + 0.5;

    // get closest depth value from light's perspective (using [0,1] range fragPosLight as coords)
    float closestDepth = texture(shadowMap, projCoords.xy).r; 

    // get depth of current fragment from light's perspective
    float currentDepth = projCoords.z;

    // check whether current frag pos is in shadow
    float shadow = 0.0;
    vec2 texelSize = 1.0 / textureSize(shadowMap, 0);
    for(int x = -1; x <= 1; ++x)
    {
        for(int y = -1; y <= 1; ++y)
        {
            float pcfDepth = texture(shadowMap, projCoords.xy + vec2(x, y) * texelSize).r; 
            shadow += currentDepth - offset > pcfDepth ? 1.0 : 0.0;        
        }
    }
    shadow /= 9.0;

    if(projCoords.z > 1.0)
        shadow = 0.0;

    return shadow;
}

//===========================| apply fog |======================================
vec3 applyFog( in vec3  rgb,       // original color of the pixel
               in float dist ) // camera to point distance
{
    //float b = 0.002;
    //float fogAmount = 1.0 - exp( -dist*b );
    //vec3  fogColor  = vec3(0.5,0.6,0.7);
    //return mix( rgb, fogColor, fogAmount );

    //linear fog
    // 20 - fog starts; 80 - fog ends
    float fogStart = 100;
    float fogEnd = 800;
    float fogFactor = (fogEnd - dist)/(fogEnd - fogStart);
    fogFactor = clamp( fogFactor, 0.0, 1.0 );
    vec3 fogColor = vec3(0.5,0.6,0.7);
 
    //if you inverse color in glsl mix function you have to
    //put 1.0 - fogFactor
    return mix(fogColor, rgb, fogFactor);
}

/*vec3 applyFog( in vec3  rgb,      // original color of the pixel
               in float distance, // camera to point distance
               in vec3  rayOri,   // camera position
               in vec3  rayDir )  // camera to point vector
{
    float a = 1;
    float b = 10;
    float c = a/b;

    float fogAmount = c * exp(-rayOri.y*b) * (1.0-exp( -distance*rayDir.y*b ))/rayDir.y;
    vec3  fogColor  = vec3(0.5,0.6,0.7);
    return mix( rgb, fogColor, fogAmount );
}*/