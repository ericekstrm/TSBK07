#version 400 core

in vec2 texCoord;
in vec3 inNormal;
in vec3 fragPos;
in vec4 viewSpace;

//textures
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendmap;

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

void main()
{
    //blend the correct texture for the fragment
    vec4 blend = texture(blendmap, texCoord);
    vec2 tiledTexCoord = texCoord * 50;
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

    //=====================================================
    //Directional lights
    for(int i = 0; i < dirLightDirArr.length(); i++)
    {
        vec3 lightDir = dirLightDirArr[i];

        output += calcLight(matDiffuse, matSpecular, normal, lightDir, dirLightColorArr[i], viewDir);
    }

    //=====================================================
    //Positional Lights
    for (int i = 0; i < pointLightPosArr.length(); i++)
    {
        vec3 lightDir = normalize(fragPos - pointLightPosArr[i]);

        float Kc = 1;
	float Kl = 2 / r[i];
    	float Kq = 1 / (r[i] * r[i]);
        float dis = length(pointLightPosArr[i] - fragPos);
        float attenuation = 1.0 / (Kc + Kl * dis + 
  			     Kq * (dis * dis));  

        output += attenuation * calcLight(matDiffuse, matSpecular, normal, lightDir, pointLightColorArr[i], viewDir);
    }
 
    //=====================================================

    float dist = abs(viewSpace.z);
    //output = applyFog(output, dist, viewPos, viewDir);
    output = applyFog(output, dist);
    
    outColor = vec4(output, 1);
}

vec3 calcLight(vec3 matDiffuse, vec3 matSpecular, vec3 normal, vec3 lightDir, vec3 lightColor, vec3 viewDir)
{
    //diffuse lighting
    float diff = max(0.0, dot(normal, -lightDir));
    
    //specular lighting
    vec3 reflectDir = reflect(lightDir, normal);
    float spec = pow(max(0.0, dot(viewDir, reflectDir)), specularExponent);

    vec3 diffuse = lightColor * diff * matDiffuse;
    vec3 specular = lightColor * spec * matSpecular;
    return (diffuse + specular);
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