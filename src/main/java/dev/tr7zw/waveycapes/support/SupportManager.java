package dev.tr7zw.waveycapes.support;

import java.util.HashSet;
import java.util.Set;

public class SupportManager {

    public static Set<ModSupport> mods = new HashSet<>();
    
    static {
        try {
            Class.forName("de.kxmischesdomi.morebannerfeatures.MoreBannerFeatures");
            mods.add(new MoreBannerFeaturesSupport());
            System.out.println("Wavey Capes loaded MoreBannerFeatures support!");
        }catch(Exception ex) {
            // not loaded
        }
    }
    
    public static Set<ModSupport> getSupportedMods(){
        return mods;
    }
    
}
