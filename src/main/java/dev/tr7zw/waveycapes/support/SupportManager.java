package dev.tr7zw.waveycapes.support;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

import lombok.Getter;
import lombok.Setter;

public class SupportManager {

    public static Set<ModSupport> mods = new TreeSet<>(Comparator.comparingInt(ModSupport::priority)
            .reversed().thenComparing(support -> support.getClass().getName())
            .thenComparingInt(System::identityHashCode));

    public static Set<AnimationSupport> animationSupport = new HashSet<>();

    @Getter
    @Setter
    public static Supplier<Float> alphaSupplier = () -> 1f;

    public static Set<ModSupport> getSupportedMods() {
        return mods;
    }

}
