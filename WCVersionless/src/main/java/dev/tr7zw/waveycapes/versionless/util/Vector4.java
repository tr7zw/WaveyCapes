package dev.tr7zw.waveycapes.versionless.util;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Vector4 {
    public float x, y, z, w;

    public Vector4 clone() {
        return new Vector4(x, y, z, w);
    }

    public Vector3 toVec3() {
        return new Vector3(x, y, z);
    }
}
