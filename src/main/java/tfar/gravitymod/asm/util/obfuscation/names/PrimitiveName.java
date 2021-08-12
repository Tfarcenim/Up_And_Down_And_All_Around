package tfar.gravitymod.asm.util.obfuscation.names;

import tfar.gravitymod.asm.util.obfuscation.IClassName;

/**
 * Created by Mysteryem on 2017-01-30.
 */
public class PrimitiveName extends DeobfAwareString implements IClassName {
    public PrimitiveName(String name) {
        super(name);
    }
}
