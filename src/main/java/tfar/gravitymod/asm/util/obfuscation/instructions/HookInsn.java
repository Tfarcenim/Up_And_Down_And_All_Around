package tfar.gravitymod.asm.util.obfuscation.instructions;

import org.objectweb.asm.Opcodes;
import tfar.gravitymod.asm.util.obfuscation.IDeobfAware;
import tfar.gravitymod.asm.util.obfuscation.MethodDesc;
import tfar.gravitymod.asm.util.obfuscation.names.ObjectName;

/**
 * Created by Mysteryem on 2017-01-30.
 */
public class HookInsn extends MethodInsn {
    private static final ObjectName Hooks = new ObjectName("tfar/gravitymod/asm/Hooks");

    public HookInsn(IDeobfAware methodName, MethodDesc description) {
        super(Opcodes.INVOKESTATIC, Hooks, methodName, description);
    }

    public HookInsn(String methodName, MethodDesc description) {
        super(Opcodes.INVOKESTATIC, Hooks, methodName, description);
    }
}
