#version 320 es

uniform mediump sampler2DArray water_textures;
uniform sampler2D water_exists_texture;
uniform sampler2D fogOfWarMap;


uniform int current_texture;
uniform mediump vec4 mapBounds;

in mediump vec2 UV;
in mediump vec4 Color;
in mediump vec2 position;
in mediump vec3 shadeColor;
in mediump vec2 v_suv;

out mediump vec4 outColor;

void main() {
    mediump vec2 d2 = min(position - mapBounds.xy, mapBounds.zw - position);
   mediump float d1 = clamp(min(d2.x, d2.y) / 64.0 + 1.0, 0.0, 1.0) * 0.8 + 0.2;;
   mediump float fogOfWarData = texture(fogOfWarMap, v_suv).r;
	outColor = texture(water_textures, vec3(UV, current_texture)) * vec4(Color.rgb * d1 * shadeColor, Color.a) * (1.0 - fogOfWarData);
}