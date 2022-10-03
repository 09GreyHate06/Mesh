#type vertex

#version 330 core

layout(location = 0) in vec3 a_pos;
layout(location = 1) in vec2 a_texCoord;
layout(location = 2) in vec3 a_normal;
layout(location = 3) in vec3 a_tangent;
layout(location = 4) in vec3 a_bitangent;

out VS_OUT{
    vec2 texCoord;
    vec3 normal;
    vec3 fragPosition;
    vec3 viewPosition;
    vec3 wsTangent;
    vec3 wsBitangent;
} vs_out;

layout (std140) uniform Camera{
	mat4 u_view;
	mat4 u_projection;
	vec3 u_viewPos;
};

uniform mat4 u_transform;
uniform mat3 u_normalMatrix;

void main(){
    mat3 wsTransform = mat3(u_transform);
    vs_out.texCoord = a_texCoord;
    vs_out.normal = u_normalMatrix * a_normal;
    vs_out.fragPosition = vec3(u_transform * vec4(a_pos, 1.0f));
    vs_out.viewPosition = u_viewPos;
    vs_out.wsTangent = wsTransform * a_tangent;
    vs_out.wsBitangent = wsTransform * a_bitangent;

    gl_Position = u_projection * u_view * u_transform * vec4(a_pos, 1.0f);
}

#type fragment

#version 330 core

in VS_OUT{
    vec2 texCoord;
    vec3 normal;
    vec3 fragPosition;
    vec3 viewPosition;
    vec3 wsTangent;
    vec3 wsBitangent;
} fs_in;

struct Material
{
    vec4 color;
    vec2 tiling;
    float shininess;
    bool enableNormalMap;

    sampler2D diffuseMap;
    sampler2D specularMap;
    sampler2D normalMap;
};

struct DirectionalLight
{
    vec3 direction;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

struct PointLight
{
    vec3 position;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;

    float constant;
    float linear;
    float quadratic;
};

struct SpotLight
{
    vec3 position;
    vec3 direction;

    float constant;
    float linear;
    float quadratic;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;

    float innerCutOff; // cos
    float outerCutOff; // cos
};

out vec4 o_color;

const int s_lightMaxCount = 32;

uniform DirectionalLight u_dirLights[32];
uniform PointLight u_pointLights[32];
uniform SpotLight u_spotLights[32];

uniform int u_activeDirectionalLights = 0;
uniform int u_activePointLights = 0;
uniform int u_activeSpotLights = 0;

uniform Material u_material;

vec3 Phong(vec3 fragToLight, vec3 fragToView, vec3 normal, vec3 lAmbient, vec3 lDiffuse, vec3 lSpecular, vec3 mDiffuse, vec3 mSpecular);
float CalcAttenuation(float distance, float constant, float linear, float quadratic);

void main(){
    vec3 fragToView = normalize(fs_in.viewPosition - fs_in.fragPosition);
    vec3 normal = normalize(fs_in.normal);
    if(u_material.enableNormalMap){
        vec3 t = normalize(fs_in.wsTangent - dot(fs_in.wsTangent, normal) * normal);
        vec3 b = cross(normal, t);
        mat3 tanToWorld;
        tanToWorld[0] = normalize(t);
        tanToWorld[1] = normalize(b);
        tanToWorld[2] = normalize(normal);

        normal = texture(u_material.normalMap, fs_in.texCoord * u_material.tiling).xyz;
        normal = normal * 2.0f - 1.0f;
        normal = normalize(tanToWorld * normal);
    }

    vec4 mDiffuse = texture(u_material.diffuseMap, fs_in.texCoord * u_material.tiling) * u_material.color;
    vec4 mSpecular = texture(u_material.specularMap, fs_in.texCoord * u_material.tiling) * u_material.color;

    vec3 dirLightPhong = vec3(0.0f, 0.0f, 0.0f);
    vec3 pointLightPhong = vec3(0.0f, 0.0f, 0.0f);
    vec3 spotLightPhong = vec3(0.0f, 0.0f, 0.0f);

    for(int i = 0; i < u_activeDirectionalLights; i++){
        DirectionalLight light = u_dirLights[i];
        vec3 fragToLight = normalize(-light.direction);
        dirLightPhong += Phong(fragToLight, fragToView, normal, light.ambient, light.diffuse, light.specular, mDiffuse.rgb, mSpecular.rgb);
    }

    for(int i = 0; i < u_activePointLights; i++){
        PointLight light = u_pointLights[i];
        vec3 fragToLight = normalize(light.position - fs_in.fragPosition);
        float attenuation = CalcAttenuation(length(light.position - fs_in.fragPosition), light.constant, light.linear, light.quadratic);
        vec3 phong = Phong(fragToLight, fragToView, normal, light.ambient, light.diffuse, light.specular, mDiffuse.rgb, mSpecular.rgb);
        pointLightPhong += phong * attenuation;
    }

    for(int i = 0; i < u_activeSpotLights; i++){
        SpotLight light = u_spotLights[i];
        vec3 fragToLight = normalize(light.position - fs_in.fragPosition);
        float attenuation = CalcAttenuation(length(light.position - fs_in.fragPosition), light.constant, light.linear, light.quadratic);

        float theta = dot(fragToLight, normalize(-light.direction));
        float epsilon = light.innerCutOff - light.outerCutOff;
        float intensity = clamp((theta - light.outerCutOff) / epsilon, 0.0f, 1.0f);

        vec3 phong = Phong(fragToLight, fragToView, normal, light.ambient, light.diffuse, light.specular, mDiffuse.rgb, mSpecular.rgb);
        spotLightPhong += phong * attenuation * intensity;
    }

    o_color = vec4(dirLightPhong + pointLightPhong + spotLightPhong, mDiffuse.a);
}

vec3 Phong(vec3 fragToLight, vec3 fragToView, vec3 normal, vec3 lAmbient, vec3 lDiffuse, vec3 lSpecular, vec3 mDiffuse, vec3 mSpecular){
    vec3 ambient = lAmbient * mDiffuse;

    float diffuseFactor = max(dot(normal, fragToLight), 0.0f);
    vec3 diffuse = lDiffuse * mDiffuse * diffuseFactor;

    vec3 halfwayDir = normalize(fragToLight + fragToView);
    float specularFactor = pow(max(dot(normal, halfwayDir), 0.0f), u_material.shininess);
    vec3 specular = lSpecular * mSpecular * specularFactor;

    return ambient + diffuse + specular;
}

float CalcAttenuation(float distance, float constant, float linear, float quadratic){
    return 1.0f / (constant + linear * distance + quadratic * (distance * distance));
}