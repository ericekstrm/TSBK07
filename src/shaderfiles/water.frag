#version 400 core

in vec4 clipSpace;
in vec2 texCoords;
in vec3 toCameraVector;
in vec3 fromLightVector;
in vec4 viewSpace;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudvMap;
uniform sampler2D normalMap;
uniform sampler2D depthMap;
uniform vec3 lightColor;

out vec4 outColor;

uniform float moveFactor;
float tilingFactor = 30;
float waveStrength = 0.02;
float specularExponent = 20;
float reflectivity = 0.6;

//forward declaration of functions
vec3 applyFog(in vec3,in float);

void main()
{
    vec2 ndc = (clipSpace.xy/clipSpace.w)/2 + 0.5; 

    vec2 reflectionTexCoord = vec2(ndc.x, -ndc.y);
    vec2 refractionTexCoord = ndc;

    float near = 1;
    float far = 1000;
    float depth = texture(depthMap, refractionTexCoord).r;
    float floorDistance = 2 * near * far / (far + near - (2 * depth - 1) * (far - near));
    depth = gl_FragCoord.z;
    float waterDistance = 2 * near * far / (far + near - (2 * depth - 1) * (far - near));
    float waterDepth = floorDistance - waterDistance;

    vec2 shiftedTexCoords = (texCoords * tilingFactor) * 2 - 1;
    vec2 distortedTexCoords = texture(dudvMap, vec2(shiftedTexCoords.x + moveFactor, shiftedTexCoords.y)).rg * 0.1;
    distortedTexCoords = texCoords + vec2(distortedTexCoords.x + distortedTexCoords.y + moveFactor);
    vec2 totalOffset = (texture(dudvMap, distortedTexCoords).rg * 2 - 1) * waveStrength * clamp(waterDepth/20, 0, 1);

    vec4 normalMapColor = texture(normalMap, distortedTexCoords);
    vec3 normal = vec3(normalMapColor.r * 2 - 1, normalMapColor.b, normalMapColor.g * 2 - 1);
    normal = normalize(normal);

    reflectionTexCoord += totalOffset;
    reflectionTexCoord.x = clamp(reflectionTexCoord.x, 0.001, 0.999);
    reflectionTexCoord.y = clamp(reflectionTexCoord.y, -0.999, -0.001);
    
    refractionTexCoord = clamp(refractionTexCoord + totalOffset, 0.001, 0.999);

    vec4 reflection = texture(reflectionTexture, reflectionTexCoord);
    vec4 refraction = texture(refractionTexture, refractionTexCoord);

    float refractiveFactor = dot(normalize(toCameraVector), vec3(0, 1, 0));

    vec3 reflectedLight = reflect(normalize(fromLightVector), normal);
    float specular = max(dot(reflectedLight, normalize(toCameraVector)), 0.0);
    specular = pow(specular, specularExponent);
    vec3 specularLight = lightColor * specular * reflectivity * clamp(waterDepth/5, 0, 1);;

    outColor = mix(reflection, refraction, refractiveFactor);
    outColor = mix(outColor, vec4(0.0, 0.2, 0.5, 0.1), 0.2) + vec4(specularLight, 0);
    outColor.a = clamp(waterDepth/5, 0, 1);

    //fog
    float dist = abs(viewSpace.z);
    outColor.xyz = applyFog(outColor.xyz, dist);
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