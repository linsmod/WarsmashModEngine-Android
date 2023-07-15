

    uniform mat4 u_mvp;
    uniform vec4 u_vertexColor;
    uniform vec4 u_geosetColor;
    uniform float u_layerAlpha;
    uniform vec2 u_uvTrans;
    uniform vec2 u_uvRot;
    uniform float u_uvScale;
    uniform bool u_hasBones;
    uniform bool u_unshaded;
    attribute vec3 a_position;
    attribute vec3 a_normal;
    attribute vec2 a_uv;
    attribute vec4 a_bones;
    #ifdef EXTENDED_BONES
    attribute vec4 a_extendedBones;
    #endif
    attribute float a_boneNumber;
    varying vec2 v_uv;
    varying vec4 v_color;
    varying vec4 v_uvTransRot;
    varying float v_uvScale;
    uniform sampler2D u_lightTexture;
    uniform float u_lightCount;
    uniform float u_lightTextureHeight;
    uniform sampler2D u_boneMap;
    uniform float u_vectorSize;
    uniform float u_rowSize;
    mat4 fetchMatrix(float column, float row) {
      column *= u_vectorSize * 4.0;
      row *= u_rowSize;
      // Add in half texel to sample in the middle of the texel.
      // Otherwise, since the sample is directly on the boundry, small floating point errors can cause the sample to get the wrong pixel.
      // This is mostly noticable with NPOT textures, which the bone maps are.
      column += 0.5 * u_vectorSize;
      row += 0.5 * u_rowSize;
      return mat4(texture2D(u_boneMap, vec2(column, row)),
                  texture2D(u_boneMap, vec2(column + u_vectorSize, row)),
                  texture2D(u_boneMap, vec2(column + u_vectorSize * 2.0, row)),
                  texture2D(u_boneMap, vec2(column + u_vectorSize * 3.0, row)));
    }
    void transform(inout vec3 position, inout vec3 normal) {
      // For the broken models out there, since the game supports this.
      if (a_boneNumber > 0.0) {
        vec4 position4 = vec4(position, 1.0);
        vec4 normal4 = vec4(normal, 0.0);
        mat4 bone;
        vec4 p = vec4(0.0,0.0,0.0,0.0);
        vec4 n = vec4(0.0,0.0,0.0,0.0);
        for (int i = 0; i < 4; i++) {
          if (a_bones[i] > 0.0) {
            bone = fetchMatrix(a_bones[i] - 1.0, 0.0);
            p += bone * position4;
            n += bone * normal4;
          }
        }
        #ifdef EXTENDED_BONES
          for (int i = 0; i < 4; i++) {
            if (a_extendedBones[i] > 0.0) {
              bone = fetchMatrix(a_extendedBones[i] - 1.0, 0.0);
              p += bone * position4;
              n += bone * normal4;
            }
          }
        #endif
        position = p.xyz / a_boneNumber;
        normal = normalize(n.xyz);
      } else {
        position.x += 100.0;
      }

    }
    void main() {
      vec3 position = a_position;
      vec3 normal = a_normal;
      if (u_hasBones) {
        transform(position, normal);
      }
      v_uv = a_uv;
      v_color = u_vertexColor * u_geosetColor.bgra * vec4(1.0, 1.0, 1.0, u_layerAlpha);
      v_uvTransRot = vec4(u_uvTrans, u_uvRot);
      v_uvScale = u_uvScale;
      gl_Position = u_mvp * vec4(position, 1.0);
      if(!u_unshaded) {
        vec3 lightFactor = vec3(0.0,0.0,0.0);
        for(float lightIndex = 0.5; lightIndex < u_lightCount; lightIndex += 1.0) {
          float rowPos = (lightIndex) / u_lightTextureHeight;
          vec4 lightPosition = texture2D(u_lightTexture, vec2(0.125, rowPos));
          vec3 lightExtra = texture2D(u_lightTexture, vec2(0.375, rowPos)).xyz;
          vec4 lightColor = texture2D(u_lightTexture, vec2(0.625, rowPos));
          vec4 lightAmbColor = texture2D(u_lightTexture, vec2(0.875, rowPos));
          if(lightExtra.x > 1.5) {
            // Ambient light;
            float dist = length(position - vec3(lightPosition.xyz));
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
            vec3 deltaBtwn = position - lightPosition.xyz;
            float dist = length(position - vec3(lightPosition.xyz)) / 64.0 + 1.0;
            vec3 lightDirection = normalize(-deltaBtwn);
            vec3 lightFactorContribution = (lightColor.a/(pow(dist, 2.0))) * lightColor.rgb * clamp(dot(normal, lightDirection), 0.0, 1.0);
            if(lightFactorContribution.r > 1.0 || lightFactorContribution.g > 1.0 || lightFactorContribution.b > 1.0) {
              lightFactorContribution = clamp(lightFactorContribution, 0.0, 1.0);
            }
            lightFactor += lightFactorContribution + (lightAmbColor.a/(pow(dist, 2.0))) * lightAmbColor.rgb;
          }
        }
        vec4 sRGB = vec4(lightFactor, 1.0);        bvec4 cutoff = lessThan(sRGB, vec4(0.04045));        vec4 higher = pow((sRGB + vec4(0.055))/vec4(1.055), vec4(2.4));        vec4 lower = sRGB/vec4(12.92);        lightFactor = (higher * (vec4(1.0) - vec4(cutoff)) + lower * vec4(cutoff)).xyz;
        v_color.xyz *= clamp(lightFactor, 0.0, 1.0);
      }
    }