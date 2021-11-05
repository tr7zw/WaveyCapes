package dev.tr7zw.waveycapes.accessor;

import net.minecraft.client.model.geom.ModelPart;

/**
 * Used to expose the thinArms setting of the player model
 *
 */
public interface PlayerEntityModelAccessor {
	public ModelPart[] getCustomCapeParts();
}