#version 330 core

in vec2 vPosition;
uniform mat4 MVP;
uniform mat4 DepthBiasMVP;

uniform sampler2D height_texture;
uniform sampler2D height_cliff_texture;
uniform usampler2D terrain_texture_list;
uniform float centerOffsetX;
uniform float centerOffsetY;
uniform sampler2D lightTexture;
uniform float lightCount;
uniform float lightTextureHeight;

out vec2 UV;
flat out uvec4 texture_indices;
out vec2 pathing_map_uv;
out vec3 position;
out vec3 ShadowCoord;
out vec2 v_suv;
out vec3 shadeColor;

void main() { 
	ivec2 size = textureSize(terrain_texture_list, 0);
	ivec2 pos = ivec2(gl_InstanceID % size.x, gl_InstanceID / size.x);

	ivec2 height_pos = ivec2(vPosition + pos);
	vec4 height = texelFetch(height_cliff_texture, height_pos, 0);

	ivec3 off = ivec3(1, 1, 0);
	float hL = texelFetch(height_texture, height_pos - off.xz, 0).r;
	float hR = texelFetch(height_texture, height_pos + off.xz, 0).r;
	float hD = texelFetch(height_texture, height_pos - off.zy, 0).r;
	float hU = texelFetch(height_texture, height_pos + off.zy, 0).r;
	vec3 normal = normalize(vec3(hL - hR, hD - hU, 2.0));

 UV = vec2(vPosition.x, 1 - vPosition.y);
	texture_indices = texelFetch(terrain_texture_list, pos, 0);
	pathing_map_uv = (vPosition + pos) * 4;	

	// Cliff culling
	vec3 positionWorld = vec3((vPosition.x + pos.x)*128.0 + centerOffsetX, (vPosition.y + pos.y)*128.0 + centerOffsetY, height.r*128.0);
	position = positionWorld;
	gl_Position = ((texture_indices.a & 32768u) == 0u) ? MVP * vec4(position.xyz, 1) : vec4(2.0, 0.0, 0.0, 1.0);
	ShadowCoord = (((texture_indices.a & 32768u) == 0u) ? DepthBiasMVP * vec4(position.xyz, 1) : vec4(2.0, 0.0, 0.0, 1.0)).xyz;
   v_suv = (vPosition + pos) / size;
	position.x = (position.x - centerOffsetX) / (size.x * 128.0);
	position.y = (position.y - centerOffsetY) / (size.y * 128.0);
        vec3 lightFactor = vec3(0.0,0.0,0.0);
        for(float lightIndex = 0.5; lightIndex < lightCount; lightIndex += 1.0) {
          float rowPos = (lightIndex) / lightTextureHeight;
          vec4 lightPosition = texture2D(lightTexture, vec2(0.125, rowPos));
          vec3 lightExtra = texture2D(lightTexture, vec2(0.375, rowPos)).xyz;
          vec4 lightColor = texture2D(lightTexture, vec2(0.625, rowPos));
          vec4 lightAmbColor = texture2D(lightTexture, vec2(0.875, rowPos));
          if(lightExtra.x > 1.5) {
            // Ambient light;
            float dist = length(positionWorld - vec3(lightPosition.xyw));
            float attenuationStart = lightExtra.y;
            float attenuationEnd = lightExtra.z;
            if( dist <= attenuationEnd ) {
              float attenuationDist = clamp((dist-attenuationStart), 0.001, (attenuationEnd-attenuationStart));
              float attenuationFactor = 1.0/(attenuationDist);
              lightFactor += attenuationFactor * lightAmbColor.a * lightAmbColor.rgb;
              
            }
          } else if(lightExtra.x > 0.5) {
            // Directional (sun) light;
            vec3 lightDirection = vec3(lightPosition.xyz);
            vec3 lightFactorContribution = lightColor.a * lightColor.rgb * clamp(dot(normal, lightDirection), 0.0, 1.0);
            if(lightFactorContribution.r > 1.0 || lightFactorContribution.g > 1.0 || lightFactorContribution.b > 1.0) {
              lightFactorContribution = clamp(lightFactorContribution, 0.0, 1.0);
            }
            lightFactor += lightFactorContribution + lightAmbColor.a * lightAmbColor.rgb;
          } else {
            // Omnidirectional light;
            vec3 deltaBtwn = positionWorld - lightPosition.xyz;
            float dist = length(positionWorld - vec3(lightPosition.xyz)) / 64.0 + 1.0;
            vec3 lightDirection = normalize(-deltaBtwn);
            vec3 lightFactorContribution = (lightColor.a/(pow(dist, 2.0))) * lightColor.rgb * clamp(dot(normal, lightDirection), 0.0, 1.0);
            if(lightFactorContribution.r > 1.0 || lightFactorContribution.g > 1.0 || lightFactorContribution.b > 1.0) {
              lightFactorContribution = clamp(lightFactorContribution, 0.0, 1.0);
            }
            lightFactor += lightFactorContribution + (lightAmbColor.a/(pow(dist, 2.0))) * lightAmbColor.rgb;
          }
        }
        vec4 sRGB = vec4(lightFactor, 1.0);        bvec4 cutoff = lessThan(sRGB, vec4(0.04045));        vec4 higher = pow((sRGB + vec4(0.055))/vec4(1.055), vec4(2.4));        vec4 lower = sRGB/vec4(12.92);        lightFactor = (higher * (vec4(1.0) - vec4(cutoff)) + lower * vec4(cutoff)).xyz;
        shadeColor = clamp(lightFactor, 0.0, 1.0);
}