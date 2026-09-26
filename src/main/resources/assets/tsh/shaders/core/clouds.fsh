#version 150

uniform vec2 CloudFog;

in float vertexDistance;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    float fog = clamp((vertexDistance - CloudFog.x) / (CloudFog.y - CloudFog.x), 0.0, 1.0);
    fragColor = vec4(vertexColor.rgb, vertexColor.a * (1.0 - fog));
}
