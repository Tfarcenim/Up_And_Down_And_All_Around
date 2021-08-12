package tfar.gravitymod.asm.patches;

import tfar.gravitymod.asm.Ref;
import tfar.gravitymod.asm.Transformer;
import tfar.gravitymod.asm.util.patching.ClassPatcher;
import tfar.gravitymod.asm.util.patching.MethodPatcher;

//TODO: Try replacing with a patch that makes rotation variables relative at the start and then back to absolute afterwards

/**
 * If a player looks 'up' from their perspective, we want their head to look 'up'.
 * <p>
 * If a player has non standard gravity, then their pitch (and yaw) won't match their own perspective
 * (player rotation is always relative to normal minecraft gravity so that bows and similar work properly).
 * <p>
 * This replaces all accesses to the player's rotation fields with static hook calls that calculate and
 * return the field in question, as seen from the player's perspective.
 * <p>
 * Created by Mysteryem on 2017-01-31.
 */
public class PatchRenderLivingBase extends ClassPatcher {
    public PatchRenderLivingBase() {
        super("net.minecraft.client.renderer.entity.RenderLivingBase", MethodPatcher.create(
                Ref.RenderLivingBase$doRender_name::is,
                (methodNode -> Transformer.patchMethodUsingAbsoluteRotations(methodNode, Transformer.ALL_GET_ROTATION_VARS))));
    }
}
