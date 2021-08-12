package tfar.gravitymod.asm.patches;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.VarInsnNode;
import tfar.gravitymod.asm.Ref;
import tfar.gravitymod.asm.Transformer;
import tfar.gravitymod.asm.util.patching.ClassPatcher;
import tfar.gravitymod.asm.util.patching.MethodPatcher;

/**
 * When the camera rotates due to a change in gravity, the player's ears also have to be rotated.
 * <p>
 * setListener calls SoundSystem::setListenerOrientation as if the player has normal gravity.
 * <p>
 * A hook is inserted that replaces this call and calls SoundSystem::setListenerOrientation with arguments that take into account the rotation of the
 * player's camera
 * <p>
 * Created by Mysteryem on 2017-01-31.
 */
public class PatchSoundManager extends ClassPatcher {
    public PatchSoundManager() {
        super("net.minecraft.client.audio.SoundManager", 0, ClassWriter.COMPUTE_MAXS);
        MethodPatcher patch_setListener = this.addMethodPatch(
                methodNode -> Ref.SoundManager$setListener_name.is(methodNode) && Ref.SoundSystem$setListenerOrientation_desc.is(methodNode.desc));
        patch_setListener.addInsnPatch((node, iterator) -> {
            if (Ref.SoundSystem$setListenerOrientation_name.is(node)) {
                Ref.Hooks$setListenerOrientationHook.replace(iterator);
                iterator.previous();
                iterator.add(new VarInsnNode(Opcodes.ALOAD, 1)); // load Entity method argument
                return true;
            }
            return false;
        });

        //TODO: Try replacing with a patch that makes rotation variables relative at the start and then back to absolute afterwards
        this.addMethodPatch(
                methodNode -> Ref.SoundManager$setListener_name.is(methodNode) && Ref.SoundSystem$setListenerOrientation_desc.is(methodNode.desc),
                (node) -> Transformer.patchMethodUsingAbsoluteRotations(
                        node,
                        Transformer.GET_ROTATIONYAW | Transformer.GET_PREVROTATIONYAW | Transformer.GET_ROTATIONPITCH | Transformer.GET_PREVROTATIONPITCH));
    }
}
