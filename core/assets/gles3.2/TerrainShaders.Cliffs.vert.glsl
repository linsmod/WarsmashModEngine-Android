#version 320 es

in vec3 vPosition;
in vec2 vUV;
in vec3 vNormal;
in vec4 vOffset;

uniform mat4 MVP;

uniform sampler2D height_texture;
uniform sampler2D shadowMap;
uniform float centerOffsetX;
uniform float centerOffsetY;
uniform sampler2D lightTexture;
uniform float lightCount;
uniform float lightTextureHeight;

out vec3 UV;
out vec3 Normal;
out vec2 pathing_map_uv;
out vec3 position;
out vec2 v_suv;
out vec3 shadeColor;

void main() {
	pathing_map_uv = (vec2(vPosition.y, -vPosition.x) / 128.0 + vOffset.xy) * 4.0;
 
	ivec2 size = textureSize(height_texture, 0);
   ivec2 shadowSize = textureSize(shadowMap, 0);
	v_suv = pathing_map_uv / vec2(shadowSize);
	float value = texture(height_texture, (vOffset.xy + vec2(vPosition.y + 64.0, -vPosition.x + 64.0) / 128.0) / vec2(size)).r;

   position = (vec3(vPosition.y, -vPosition.x, vPosition.z) + vec3(vOffset.xy, vOffset.z + value) * 128.0 );
   vec4 myposition = vec4(position, 1);
   myposition.x += centerOffsetX;
   myposition.y += centerOffsetY;
   position.x /= (float(size.x) * 128.0);
   position.y /= (float(size.y) * 128.0);
	gl_Position = MVP * myposition;
	UV = vec3(vUV, vOffset.a);

	ivec2 height_pos = ivec2(vOffset.xy + vec2(vPosition.y, -vPosition.x) / 128.0);
	ivec3 off = ivec3(1, 1, 0);
	float hL = texelFetch(height_texture, height_pos - off.xz, 0).r;
	float hR = texelFetch(height_texture, height_pos + off.xz, 0).r;
	float hD = texelFetch(height_texture, height_pos - off.zy, 0).r;
	float hU = texelFetch(height_texture, height_pos + off.zy, 0).r;
	bool edgeX = (vPosition.y) == float((int(vPosition.y))/128*128);
	bool edgeY = (vPosition.x) == float((int(vPosition.x))/128*128);
	bool edgeZ = (vPosition.z) == float((int(vPosition.z))/128*128);
	vec3 terrain_normal = vec3(vNormal.y, -vNormal.x, vNormal.z);
	if(edgeX) {
	  terrain_normal.x = hL - hR;
	}
	if(edgeY) {
	  terrain_normal.y = hD - hU;
	}
	if(edgeZ) {
	  terrain_normal.z = 2.0;
	}
	terrain_normal = normalize(terrain_normal);

	Normal = terrain_normal;
        vec3 lightFactor = vec3(0.0,0.0,0.0);
        for(float lightIndex = 0.5; lightIndex < lightCount; lightIndex += 1.0) {
          float rowPos = (lightIndex) / lightTextureHeight;
          vec4 lightPosition = texture(lightTexture, vec2(0.125, rowPos));
          vec3 lightExtra = texture(lightTexture, vec2(0.375, rowPos)).xyz;
          vec4 lightColor = texture(lightTexture, vec2(0.625, rowPos));
          vec4 lightAmbColor = texture(lightTexture, vec2(0.875, rowPos));
          if(lightExtra.x > 1.5) {
            // Ambient light;
            float dist = length(myposition.xyz - vec3(lightPosition.xyw));
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
            vec3 lightFactorContribution = lightColor.a * lightColor.rgb * clamp(dot(terrain_normal, lightDirection), 0.0, 1.0);
            if(lightFactorContribution.r > 1.0 || lightFactorContribution.g > 1.0 || lightFactorContribution.b > 1.0) {
              lightFactorContribution = clamp(lightFactorContribution, 0.0, 1.0);
            }
            lightFactor += lightFactorContribution + lightAmbColor.a * lightAmbColor.rgb;
          } else {
            // Omnidirectional light;
            vec3 deltaBtwn = myposition.xyz - lightPosition.xyz;
            float dist = length(myposition.xyz - vec3(lightPosition.xyz)) / 64.0 + 1.0;
            vec3 lightDirection = normalize(-deltaBtwn);
            vec3 lightFactorContribution = (lightColor.a/(pow(dist, 2.0))) * lightColor.rgb * clamp(dot(terrain_normal, lightDirection), 0.0, 1.0);
            if(lightFactorContribution.r > 1.0 || lightFactorContribution.g > 1.0 || lightFactorContribution.b > 1.0) {
              lightFactorContribution = clamp(lightFactorContribution, 0.0, 1.0);
            }
            lightFactor += lightFactorContribution + (lightAmbColor.a/(pow(dist, 2.0))) * lightAmbColor.rgb;
          }
        }
        vec4 sRGB = vec4(lightFactor, 1.0);        bvec4 cutoff = lessThan(sRGB, vec4(0.04045));        vec4 higher = pow((sRGB + vec4(0.055))/vec4(1.055), vec4(2.4));        vec4 lower = sRGB/vec4(12.92);        lightFactor = (higher * (vec4(1.0) - vec4(cutoff)) + lower * vec4(cutoff)).xyz;
        shadeColor = clamp(lightFactor, 0.0, 1.0);
}