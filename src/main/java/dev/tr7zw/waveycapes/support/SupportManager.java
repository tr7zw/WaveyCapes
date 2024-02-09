package dev.tr7zw.waveycapes.support;

import java.util.HashSet;
import java.util.Set;

public class SupportManager {

    public static Set<ModSupport> mods = new HashSet<>();

    public static Set<AnimationSupport> animationSupport = new HashSet<>();

    public static Set<ModSupport> getSupportedMods() {
        return mods;
    }

}
