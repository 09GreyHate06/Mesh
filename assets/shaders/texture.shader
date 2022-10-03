#type vertex

#version 330 core

layout(location = 0) in vec3 a_pos;
layout(location = 1) in vec2 a_texCoord;

out VS_OUT{
    vec2 texCoord;
} vs_out;

layout (std140) uniform Camera{
    mat4 u_view;
    mat4 u_projection;
};

uniform mat4 u_transform;

void main(){
    gl_Position = u_projection * u_view * u_transform * vec4(a_pos, 1.0f);
    vs_out.texCoord = a_texCoord;
}

#type fragment

#version 330 core

in VS_OUT{
    vec2 texCoord;
} fs_in;

out vec4 o_fragColor;

uniform sampler2D u_texture;
uniform vec2 u_tiling = { 1.0f, 1.0f };
uniform vec4 u_color = { 1.0f, 1.0f, 1.0f, 1.0f };

void main(){
    o_fragColor = texture(u_texture, fs_in.texCoord * u_tiling) * u_color;
}