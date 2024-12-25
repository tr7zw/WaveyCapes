package dev.tr7zw.waveycapes;

//spotless:off
//#if MC >= 12102
import dev.tr7zw.waveycapes.versionless.ExtendedPlayerRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
//#else
//$$import net.minecraft.client.player.AbstractClientPlayer;
//$$import net.minecraft.world.entity.EquipmentSlot;
//$$import net.minecraft.world.entity.player.PlayerModelPart;
//#endif
//spotless:on
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import dev.tr7zw.waveycapes.versionless.CapeHolder;
import net.minecraft.resources.ResourceLocation;

public class CapeRenderInfo {
    //spotless:off
    //#if MC >= 12102
    private final PlayerRenderState renderState;

    public PlayerRenderState getRenderState() {
        return renderState;
    }

    public CapeRenderInfo(PlayerRenderState renderState) {
        this.renderState = renderState;
    }
    //#else
    //$$private final AbstractClientPlayer player;
    //$$
    //$$public AbstractClientPlayer getPlayer() {
    //$$	return player;
    //$$}
    //$$
    //$$public CapeRenderInfo(AbstractClientPlayer player) {
    //$$	this.player = player;
    //$$}
    //#endif
    //spotless:on

    public boolean isPlayerUnderwater() {
        //#if MC >= 12102
        return ((ExtendedPlayerRenderState) renderState).isUnderwater();
        //#else
        //$$return player.isUnderWater();
        //#endif
    }

    public ResourceLocation getCapeTexture() {
        //#if MC >= 12102
        return renderState.skin.capeTexture();
        //#else
        //$$return dev.tr7zw.util.NMSHelper.getPlayerCape(player);
        //#endif
    }

    public CapeHolder getCapeHolder() {
        //#if MC >= 12102
        return ((ExtendedPlayerRenderState) renderState).getCapeHolder();
        //#else
        //$$return (CapeHolder) player;
        //#endif
    }

    public boolean isPlayerInvisible() {
        //#if MC >= 12102
        return renderState.isInvisible;
        //#else
        //$$return player.isInvisible();
        //#endif
    }

    public boolean isCapeVisible() {
        //#if MC >= 12102
        return renderState.showCape && !isPlayerInvisible();
        //#else
        //$$return player.isModelPartShown(PlayerModelPart.CAPE);
        //#endif
    }

    public boolean hasElytraEquipped() {
        //#if MC >= 12104
        return renderState.chestEquipment.is(Items.ELYTRA);
        //#elseif MC >= 12102
        //$$return renderState.chestItem.is(Items.ELYTRA);
        //#else
        //$$return player.getItemBySlot(EquipmentSlot.CHEST).getItem().equals(Items.ELYTRA);
        //#endif
    }
}
