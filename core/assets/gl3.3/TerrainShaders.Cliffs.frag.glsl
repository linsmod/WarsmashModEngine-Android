#version 330 core

uniform sampler2DArray cliff_textures;
uniform sampler2D shadowMap;
uniform sampler2D fogOfWarMap;

uniform bool show_lighting;

in vec3 UV;
in vec3 Normal;
in vec2 pathing_map_uv;
in vec3 position;
in vec2 v_suv;
in vec3 shadeColor;

out vec4 color;

void main() {
	color = texture(cliff_textures, UV);

   float shadow = texture2D(shadowMap, v_suv).r;
   float fogOfWarData = texture2D(fogOfWarMap, v_suv).r;
   shadow = clamp(shadow + fogOfWarData, 0.0, 1.0);
   color.rgb *= (1.0 - shadow);
	if (show_lighting) {
		color.rgb *=  shadeColor;
	}

}