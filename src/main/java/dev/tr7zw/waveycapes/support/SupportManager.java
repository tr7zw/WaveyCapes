package dev.tr7zw.waveycapes.support;

import java.util.HashSet;
import java.util.Set;

public class SupportManager {

    public static Set<ModSupport> mods = new HashSet<>();
    
    static {
        if(doesClassExist("de.kxmischesdomi.morebannerfeatures.MoreBannerFeatures")) {
            mods.add(new MoreBannerFeaturesSupport());
            System.out.println("Wavey Capes loaded MoreBannerFeatures support!");
        }

        if(doesClassExist("net.minecraftcapes.MinecraftCapes")) {
            mods.add(new MinecraftCapesSupport());
            System.out.println("Wavey Capes loaded MinecraftCapes support!");
        }
    }
    
    public static Set<ModSupport> getSupportedMods(){
        return mods;
    }

    /**
     * Checks if a class exists or not
     * @param name
     * @return
     */
    private static boolean doesClassExist(String name) {
        try {
            if(Class.forName(name) != null) {
                return true;
            }
        } catch (ClassNotFoundException e) {}
        return false;
    }

}
