#version 320 es

in vec2 vPosition;

uniform sampler2D water_height_texture;
uniform sampler2D ground_height_texture;
uniform sampler2D water_exists_texture;
uniform mediump float centerOffsetX;
uniform mediump float centerOffsetY;

uniform mat4 MVP;
uniform mediump vec4 shallow_color_min;
uniform mediump vec4 shallow_color_max;
uniform mediump vec4 deep_color_min;
uniform mediump vec4 deep_color_max;
uniform mediump float water_offset;
uniform sampler2D lightTexture;
uniform mediump float lightCount;
uniform mediump float lightTextureHeight;

out mediump vec2 UV;
out mediump vec4 Color;
out mediump vec2 position;
out mediump vec3 shadeColor;
out mediump vec2 v_suv;

const mediump float min_depth = 10.f / 128.0;
const mediump float deeplevel = 64.f / 128.0;
const mediump float maxdepth = 72.f / 128.0;

void main() { 
	ivec2 size = textureSize(water_height_texture, 0) - 1;
	ivec2 pos = ivec2(gl_InstanceID % size.x, gl_InstanceID / size.x);
	ivec2 height_pos = ivec2(vPosition + vec2(pos));
	float water_height = texelFetch(water_height_texture, height_pos, 0).r + water_offset;

	bool is_water = texelFetch(water_exists_texture, pos, 0).r > 0.0
	 || texelFetch(water_exists_texture, pos + ivec2(1, 0), 0).r > 0.0
	 || texelFetch(water_exists_texture, pos + ivec2(1, 1), 0).r > 0.0
	 || texelFetch(water_exists_texture, pos + ivec2(0, 1), 0).r > 0.0;

   position = vec2((vPosition.x + float(pos.x))*128.0 + centerOffsetX, (vPosition.y + float(pos.y))*128.0 + centerOffsetY);
   vec4 myposition = vec4(position.xy, water_height*128.0, 1);
   vec3 Normal = vec3(0,0,1);
	gl_Position = is_water ? MVP * myposition : vec4(2.0, 0.0, 0.0, 1.0);

	UV = vec2(float(int(vPosition.x) + pos.x%2)/2.0, float(int(vPosition.y) + pos.y%2)/2.0);

	float ground_height = texelFetch(ground_height_texture, height_pos, 0).r;
	float value = clamp(water_height - ground_height, 0.f, 1.f);
	if (value <= deeplevel) {
		value = max(0.f, value - min_depth) / (deeplevel - min_depth);
		Color = shallow_color_min * (1.f - value) + shallow_color_max * value;
	} else {
		value = clamp(value - deeplevel, 0.f, maxdepth - deeplevel) / (maxdepth - deeplevel);
		Color = deep_color_min * (1.f - value) + deep_color_max * value;
	}
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
            vec3 lightFactorContribution = lightColor.a * lightColor.rgb * clamp(dot(Normal, lightDirection), 0.0, 1.0);
            if(lightFactorContribution.r > 1.0 || lightFactorContribution.g > 1.0 || lightFactorContribution.b > 1.0) {
              lightFactorContribution = clamp(lightFactorContribution, 0.0, 1.0);
            }
            lightFactor += lightFactorContribution + lightAmbColor.a * lightAmbColor.rgb;
          } else {
            // Omnidirectional light;
            vec3 deltaBtwn = myposition.xyz - lightPosition.xyz;
            float dist = length(myposition.xyz - vec3(lightPosition.xyz)) / 64.0 + 1.0;
            vec3 lightDirection = normalize(-deltaBtwn);
            vec3 lightFactorContribution = (lightColor.a/(pow(dist, 2.0))) * lightColor.rgb * clamp(dot(Normal, lightDirection), 0.0, 1.0);
            if(lightFactorContribution.r > 1.0 || lightFactorContribution.g > 1.0 || lightFactorContribution.b > 1.0) {
              lightFactorContribution = clamp(lightFactorContribution, 0.0, 1.0);
            }
            lightFactor += lightFactorContribution + (lightAmbColor.a/(pow(dist, 2.0))) * lightAmbColor.rgb;
          }
        }
        vec4 sRGB = vec4(lightFactor, 1.0);        bvec4 cutoff = lessThan(sRGB, vec4(0.04045));        vec4 higher = pow((sRGB + vec4(0.055))/vec4(1.055), vec4(2.4));        vec4 lower = sRGB/vec4(12.92);        lightFactor = (higher * (vec4(1.0) - vec4(cutoff)) + lower * vec4(cutoff)).xyz;
        shadeColor = clamp(lightFactor, 0.0, 1.0);
        v_suv = (vPosition + vec2(pos)) / vec2(size);
 }