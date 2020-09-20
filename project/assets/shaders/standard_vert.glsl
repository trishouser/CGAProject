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

out struct VertexData
{
//  float laengesl;
// float laengepl;
    vec3 norm;
    vec2 tc;
    vec3 lightdir;
    vec3 surfaceNormal;
    vec3 lightdirsp;
    vec3 viewdir;
} vertexData;

out vec3 col;

void main(){

    //gl_Position = vec4(position.x, position.y, -position.z, 1.0f);
    //col =  color;

    mat4 modelview = view_matrix * model_matrix;
    vec4 pos = projection_matrix * modelview * vec4(position, 1.0f);


    gl_Position = pos;

    vertexData.norm =mat3(transpose(inverse(modelview))) * normal;
    vertexData.tc = texcoords * tcMultiplier;

    vec4 lpos = view_matrix * vec4(coinpointlight_pos,1.0f);
    vec4 p = (view_matrix * model_matrix * vec4(position,0.1f));
    vertexData.lightdir =(lpos-p).xyz;

    vec4 slpos =view_matrix *vec4(spotlight_pos,0.0f);
    vertexData.lightdirsp = normalize((slpos-p ).xyz);

    vertexData.viewdir=normalize(-p.xyz);

    // vertexData.laengesl = length(lpos.xyz - p.xyz);
    //vertexData.laengepl = length(slpos.xyz - p.xyz);
}



