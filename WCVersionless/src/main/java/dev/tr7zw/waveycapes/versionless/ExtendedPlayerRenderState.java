package dev.tr7zw.waveycapes.versionless;

public interface ExtendedPlayerRenderState {

    CapeHolder getCapeHolder();

    void setCapeHolder(CapeHolder capeHolder);

    boolean isUnderwater();

    void setUnderwater(boolean underwater);
}
