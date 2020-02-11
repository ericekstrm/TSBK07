#version 400 core

//in vec4 color;
in vec2 texCoord;

uniform sampler2D texUnit;

out vec4 outColor;

void main()
{
    outColor = texture(texUnit, texCoord);
    //outColor = vec4(texCoord, 0, 1);
}
