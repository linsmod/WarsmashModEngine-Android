#version 330 core

uniform sampler2DArray water_textures;
uniform sampler2D water_exists_texture;
uniform sampler2D fogOfWarMap;


uniform int current_texture;
uniform vec4 mapBounds;

in vec2 UV;
in vec4 Color;
in vec2 position;
in vec3 shadeColor;
in vec2 v_suv;

out vec4 outColor;

void main() {
   vec2 d2 = min(position - mapBounds.xy, mapBounds.zw - position);
   float d1 = clamp(min(d2.x, d2.y) / 64.0 + 1.0, 0.0, 1.0) * 0.8 + 0.2;;
   float fogOfWarData = texture2D(fogOfWarMap, v_suv).r;
	outColor = texture(water_textures, vec3(UV, current_texture)) * vec4(Color.rgb * d1 * shadeColor, Color.a) * (1.0 - fogOfWarData);
}