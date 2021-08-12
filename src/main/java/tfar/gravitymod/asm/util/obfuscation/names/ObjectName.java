package tfar.gravitymod.asm.util.obfuscation.names;

import tfar.gravitymod.asm.util.obfuscation.IClassName;

/**
 * Created by Mysteryem on 2017-01-30.
 */
public class ObjectName extends DeobfAwareString implements IClassName {
    public ObjectName(String name) {
        super(name);
    }

    @Override
    public String asDescriptor() {
        return 'L' + this.toString() + ';';
    }

    public String asClassName() {
        return this.toString().replace('/', '.');
    }
}
