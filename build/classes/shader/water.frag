#version 400 core

in vec2 texCoord;

uniform sampler2D texUnit;

out vec4 outColor;

void main()
{
    outColor = texture(texUnit, texCoord);
}

