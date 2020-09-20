#version 330 core

layout(location = 0) in vec3 position;
layout(location = 2) in vec3 normal;
layout(location = 1) in vec2 texcoords;

uniform mat4 model_matrix;
uniform mat4 view_matrix;
uniform mat4 projection_matrix;
uniform vec2 tcMultiplier;
uniform vec3 spotlight_pos;
uniform vec3 coinpointlight_pos;

out vec3 norm;

void main()
{

    mat4 modelview = view_matrix * model_matrix;
    vec4 pos = projection_matrix * modelview * vec4(position, 1.0f);

    norm =mat3(transpose(inverse(modelview))) * normal;

    gl_Position = pos;

}