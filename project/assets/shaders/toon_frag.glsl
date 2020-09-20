#version 330 core

uniform vec3 lightDir;
in vec3 norm;



void main()
{
    vec3 normal = norm;
    float intensity;
    vec4 color;
    intensity = dot(lightDir,normal);

    if (intensity > 0.95f)
    color = vec4(1.0f,0.5f,0.5f,1.0f);
    else if (intensity > 0.5f)
    color = vec4(0.6f,0.3f,0.3f,1.0f);
    else if (intensity > 0.25f)
    color = vec4(0.4f,0.2f,0.2f,1.0f);
    else
    color = vec4(0.2f,0.1f,0.1f,1.0f);
    //gl_FragColor = color;

}