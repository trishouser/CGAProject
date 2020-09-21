#version 330 core

layout(location = 0) in vec3 vertex;
layout(location = 1) in vec3 vertex_normal;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

uniform vec3 lightPos;
out vec3 Normal, LightDir;

void main() {
    vec4 v = vec4(vertex, 1.0);
    gl_Position = projection * view * model * v;

    vec4 n = vec4(vertex_normal, 0.0);
    mat4 normalMat = transpose(inverse(view * model));
    Normal = (normalMat * n).xyz;

    vec4 lp = view * vec4(lightPos, 1.0);
    vec4 P = (view * model * v);
    LightDir = (lp - P).xyz;
}

/*layout(location = 0) in vec3 position;
layout(location = 2) in vec3 normal;
layout(location = 1) in vec2 texcoords;

uniform mat4 model_matrix;
uniform mat4 view_matrix;
uniform mat4 projection_matrix;
uniform vec2 tcMultiplier;
uniform vec3 spotlight_pos;
uniform vec3 coinpointlight_pos;

out vec3 norm;
out vec3 lightdir;
out vec3 viewdir;

void main()
{

    mat4 modelview = view_matrix * model_matrix;
    vec4 pos = projection_matrix * modelview * vec4(position, 1.0f);

    vec4 p = (view_matrix * model_matrix * vec4(position,1.0f));
    lightdir =(p).xyz;

    norm =mat3(transpose(inverse(modelview))) * normal;

    viewdir = normalize(-p.xyz);

    gl_Position = pos;

}
*/