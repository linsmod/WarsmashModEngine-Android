
    uniform sampler2D u_texture;
    uniform sampler2D u_shadowMap;
    uniform vec4 u_color;
    uniform bool u_show_lighting;
    varying vec2 v_uv;
    varying vec2 v_suv;
    varying vec3 v_normal;
    varying float a_positionHeight;
    varying vec3 shadeColor;
    void main() {
      if (any(bvec4(lessThan(v_uv, vec2(0.0)), greaterThan(v_uv, vec2(1.0))))) {
        discard;
      }
      vec4 color = texture2D(u_texture, clamp(v_uv, 0.0, 1.0)).rgba * u_color;
      float shadow = texture2D(u_shadowMap, v_suv).r;
      if (a_positionHeight <= 4.0) {;
        color.xyz *= 1.0 - shadow;
      };
      if (u_show_lighting) {;
        color.xyz *= shadeColor;
      };
      gl_FragColor = color;
    }
  