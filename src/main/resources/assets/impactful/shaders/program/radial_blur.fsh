#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 center;
uniform float intensity;
uniform int samples;
uniform vec4 ColorModulate;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 dir   = texCoord - center;
    float dist = length(dir);
    vec2 step  = dir * intensity * dist;

    vec4 color = texture(DiffuseSampler, texCoord);
    for (int i = 1; i <= samples; ++i) {
        float scale = float(i) / float(samples);
        color += texture(DiffuseSampler, texCoord - step * scale);
        color += texture(DiffuseSampler, texCoord + step * scale);
    }
    fragColor = color / float(2 * samples + 1);
}