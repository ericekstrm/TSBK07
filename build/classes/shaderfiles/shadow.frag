#version 330 core

in vec2 texCoords;

uniform sampler2D texUnit;
uniform bool hasTexture;

void main()
{
    float a = texture(texUnit, texCoords).a;
    if (a < 0.5 && hasTexture)
    {
        discard;
    }
}  
