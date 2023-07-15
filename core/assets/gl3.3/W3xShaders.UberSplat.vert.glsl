

    uniform mat4 u_mvp;
    uniform sampler2D u_heightMap;
    uniform vec2 u_pixel;
    uniform vec2 u_size;
    uniform vec2 u_shadowPixel;
    uniform vec2 u_centerOffset;
    uniform sampler2D u_lightTexture;
    uniform sampler2D u_waterHeightsMap;
    uniform float u_lightCount;
    uniform float u_lightTextureHeight;
    uniform bool u_aboveWater;
    uniform float u_waterHeightOffset;
    attribute vec3 a_position;
    attribute vec2 a_uv;
    attribute float a_absoluteHeight;
    varying vec2 v_uv;
    varying vec2 v_suv;
    varying vec3 v_normal;
    varying float a_positionHeight;
    varying vec3 shadeColor;
    const float normalDist = 0.25;
    void main() {
      vec2 halfPixel = u_pixel * 0.5;
      vec2 base = (a_position.xy - u_centerOffset) / 128.0;
      float height;
      float hL;
      float hR;
      float hD;
      float hU;
      if (a_absoluteHeight < -256.0) {
        height = texture2D(u_heightMap, base * u_pixel + halfPixel).r * 128.0;
        hL = texture2D(u_heightMap, vec2(base - vec2(normalDist, 0.0)) * u_pixel + halfPixel).r;
        hR = texture2D(u_heightMap, vec2(base + vec2(normalDist, 0.0)) * u_pixel + halfPixel).r;
        hD = texture2D(u_heightMap, vec2(base - vec2(0.0, normalDist)) * u_pixel + halfPixel).r;
        hU = texture2D(u_heightMap, vec2(base + vec2(0.0, normalDist)) * u_pixel + halfPixel).r;
        if (u_aboveWater) {
          height = max(height, (texture2D(u_waterHeightsMap, base * u_pixel + halfPixel).r + u_waterHeightOffset) * 128.0);
          hL = max(hL, (texture2D(u_heightMap, vec2(base - vec2(normalDist, 0.0)) * u_pixel + halfPixel).r + u_waterHeightOffset));
          hR = max(hR, (texture2D(u_heightMap, vec2(base + vec2(normalDist, 0.0)) * u_pixel + halfPixel).r + u_waterHeightOffset));
          hD = max(hD, (texture2D(u_heightMap, vec2(base - vec2(0.0, normalDist)) * u_pixel + halfPixel).r + u_waterHeightOffset));
          hU = max(hU, (texture2D(u_heightMap, vec2(base + vec2(0.0, normalDist)) * u_pixel + halfPixel).r + u_waterHeightOffset));
        }
      } else {
        height = a_absoluteHeight;
        hL = a_absoluteHeight;
        hR = a_absoluteHeight;
        hD = a_absoluteHeight;
        hU = a_absoluteHeight;
      }
      v_normal = normalize(vec3(hL - hR, hD - hU, normalDist * 2.0));
      v_uv = a_uv;
      v_suv = base / u_size;
      vec3 myposition = vec3(a_position.xy, height + a_position.z);
      gl_Position = u_mvp * vec4(myposition.xyz, 1.0);
      a_positionHeight = a_position.z;
        vec3 lightFactor = vec3(0.0,0.0,0.0);
        for(float lightIndex = 0.5; lightIndex < u_lightCount; lightIndex += 1.0) {
          float rowPos = (lightIndex) / u_lightTextureHeight;
          vec4 lightPosition = texture2D(u_lightTexture, vec2(0.125, rowPos));
          vec3 lightExtra = texture2D(u_lightTexture, vec2(0.375, rowPos)).xyz;
          vec4 lightColor = texture2D(u_lightTexture, vec2(0.625, rowPos));
          vec4 lightAmbColor = texture2D(u_lightTexture, vec2(0.875, rowPos));
          if(lightExtra.x > 1.5) {
            // Ambient light;
            float dist = length(myposition - vec3(lightPosition.xyw));
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
            vec3 lightFactorContribution = lightColor.a * lightColor.rgb * clamp(dot(v_normal, lightDirection), 0.0, 1.0);
            if(lightFactorContribution.r > 1.0 || lightFactorContribution.g > 1.0 || lightFactorContribution.b > 1.0) {
              lightFactorContribution = clamp(lightFactorContribution, 0.0, 1.0);
            }
            lightFactor += lightFactorContribution + lightAmbColor.a * lightAmbColor.rgb;
          } else {
            // Omnidirectional light;
            vec3 deltaBtwn = myposition - lightPosition.xyz;
            float dist = length(myposition - vec3(lightPosition.xyz)) / 64.0 + 1.0;
            vec3 lightDirection = normalize(-deltaBtwn);
            vec3 lightFactorContribution = (lightColor.a/(pow(dist, 2.0))) * lightColor.rgb * clamp(dot(v_normal, lightDirection), 0.0, 1.0);
            if(lightFactorContribution.r > 1.0 || lightFactorContribution.g > 1.0 || lightFactorContribution.b > 1.0) {
              lightFactorContribution = clamp(lightFactorContribution, 0.0, 1.0);
            }
            lightFactor += lightFactorContribution + (lightAmbColor.a/(pow(dist, 2.0))) * lightAmbColor.rgb;
          }
        }
        vec4 sRGB = vec4(lightFactor, 1.0);        bvec4 cutoff = lessThan(sRGB, vec4(0.04045));        vec4 higher = pow((sRGB + vec4(0.055))/vec4(1.055), vec4(2.4));        vec4 lower = sRGB/vec4(12.92);        lightFactor = (higher * (vec4(1.0) - vec4(cutoff)) + lower * vec4(cutoff)).xyz;
        shadeColor = clamp(lightFactor, 0.0, 1.0);
    }
 