#version 320 es

uniform bool show_pathing_map;
uniform bool show_lighting;

uniform mediump sampler2DArray sample0;
uniform mediump sampler2DArray sample1;
uniform mediump sampler2DArray sample2;
uniform mediump sampler2DArray sample3;
uniform mediump sampler2DArray sample4;
uniform mediump sampler2DArray sample5;
uniform mediump sampler2DArray sample6;
uniform mediump sampler2DArray sample7;
uniform mediump sampler2DArray sample8;
uniform mediump sampler2DArray sample9;
uniform mediump sampler2DArray sample10;
uniform mediump sampler2DArray sample11;
//uniform mediump sampler2DArray sample12;
//uniform mediump sampler2DArray sample13;
uniform mediump sampler2DArray sample16;

uniform sampler2D shadowMap;
uniform sampler2D fogOfWarMap;

in mediump vec2 UV;
flat in uvec4 texture_indices;
in mediump vec2 pathing_map_uv;
in mediump vec3 position;
in mediump vec3 ShadowCoord;
in mediump vec2 v_suv;
in mediump vec3 shadeColor;

out mediump vec4 color;

vec4 get_fragment(uint id, vec3 uv) {
	mediump vec2 dx = dFdx(uv.xy);
	mediump vec2 dy = dFdy(uv.xy);

	switch(id) {
		case 0u:
			return textureGrad(sample0, uv, dx, dy);
		case 1u:
			return textureGrad(sample1, uv, dx, dy);
		case 2u:
			return textureGrad(sample2, uv, dx, dy);
		case 3u:
			return textureGrad(sample3, uv, dx, dy);
		case 4u:
			return textureGrad(sample4, uv, dx, dy);
		case 5u:
			return textureGrad(sample5, uv, dx, dy);
		case 6u:
			return textureGrad(sample6, uv, dx, dy);
		case 7u:
			return textureGrad(sample7, uv, dx, dy);
		case 8u:
			return textureGrad(sample8, uv, dx, dy);
		case 9u:
			return textureGrad(sample9, uv, dx, dy);
		case 10u:
			return textureGrad(sample10, uv, dx, dy);
		case 11u:
			return textureGrad(sample11, uv, dx, dy);
//		case 12u:
//			return textureGrad(sample12, uv, dx, dy);
//		case 13u:
//			return textureGrad(sample13, uv, dx, dy);
		case 16u:
			return textureGrad(sample16, uv, dx, dy);
		case 17u:
			return vec4(0, 0, 0, 0);
	}
}


void main() {
	color = get_fragment(texture_indices.a & 31u, vec3(UV, texture_indices.a >> 5));
	color = color * color.a + get_fragment(texture_indices.b & 31u, vec3(UV, texture_indices.b >> 5)) * (1.0 - color.a);
	color = color * color.a + get_fragment(texture_indices.g & 31u, vec3(UV, texture_indices.g >> 5)) * (1.0 - color.a);
	color = color * color.a + get_fragment(texture_indices.r & 31u, vec3(UV, texture_indices.r >> 5)) * (1.0 - color.a);
   mediump float shadow = texture(shadowMap, v_suv).r;
	mediump float fogOfWarData = texture(fogOfWarMap, v_suv).r;
   shadow = clamp(shadow + fogOfWarData, 0.0, 1.0);

	if (show_lighting) {
     color = vec4(color.xyz * (1.0 - shadow) * shadeColor, 1.0);
	} else {
     color = vec4(color.xyz * (1.0 - shadow), 1.0);
	}
}