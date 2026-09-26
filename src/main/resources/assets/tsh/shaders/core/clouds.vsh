#version 150

in vec3 Position;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 CloudOffset;
uniform vec4 CloudColor;
uniform vec4 CloudFade;

out float vertexDistance;
out vec4 vertexColor;

void main() {
    vec3 pos = Position + CloudOffset;
    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);
    vertexDistance = length(pos);

    float alpha = CloudColor.a;
    if (CloudFade.x > 0.0) {
        float height = clamp(Position.y / CloudFade.z, 0.0, 1.0);
        float side = clamp(CloudFade.w / CloudFade.y, -1.0, 1.0);
        float fadeBelow = mix(1.0, CloudFade.x, height);
        float fadeAbove = mix(1.0, CloudFade.x, 1.0 - height);
        alpha *= 1.0 - mix(fadeBelow, fadeAbove, (side + 1.0) * 0.5);
    }
    vertexColor = vec4(CloudColor.rgb, alpha);
}
