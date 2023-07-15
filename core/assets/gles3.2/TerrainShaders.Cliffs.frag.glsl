#version 320 es

uniform mediump sampler2DArray cliff_textures;
uniform sampler2D shadowMap;
uniform sampler2D fogOfWarMap;

uniform bool show_lighting;

in mediump vec3 UV;
in mediump vec3 Normal;
in mediump vec2 pathing_map_uv;
in mediump vec3 position;
in mediump vec2 v_suv;
in mediump vec3 shadeColor;

out mediump vec4 color;

void main() {
	color = texture(cliff_textures, UV);

   mediump float shadow = texture(shadowMap, v_suv).r;
    mediump float fogOfWarData = texture(fogOfWarMap, v_suv).r;
   shadow = clamp(shadow + fogOfWarData, 0.0, 1.0);
   color.rgb *= (1.0 - shadow);
	if (show_lighting) {
		color.rgb *=  shadeColor;
	}

}