
    // A 2D quaternion*vector.
    // q is the zw components of the original quaternion.
vec2 quat_transform(vec2 q, vec2 v) {
  vec2 uv = vec2(-q.x * v.y, q.x * v.x);
  vec2 uuv = vec2(-q.x * uv.y, q.x * uv.x);
  return v + 2.0 * (uv * q.y + uuv);
}
    // A 2D quaternion*vector.
    // q is the zw components of the original quaternion.
vec3 quat_transform(vec2 q, vec3 v) {
  return vec3(quat_transform(q, v.xy), v.z);
}

uniform sampler2D u_texture;
uniform vec4 u_vertexColor;
uniform float u_filterMode;
varying vec2 v_uv;
varying vec4 v_color;
varying vec4 v_uvTransRot;
varying float v_uvScale;
void main() {
  vec2 uv = v_uv;
      // Translation animation
  uv += v_uvTransRot.xy;
      // Rotation animation
  uv = quat_transform(v_uvTransRot.zw, uv - 0.5) + 0.5;
      // Scale animation
  uv = v_uvScale * (uv - 0.5) + 0.5;
  vec4 texel = texture2D(u_texture, uv);
  vec4 color = texel * v_color;
      // 1bit Alpha
  if(u_vertexColor.a == 1.0 && u_filterMode == 1.0 && color.a < 0.75) {
    discard;
  }
      // "Close to 0 alpha"
  if(u_filterMode >= 5.0 && color.a < 0.02) {
    discard;
  }
  gl_FragColor = color;
}