package tfar.gravitymod.common.modsupport.prepostmodifier;

import tfar.gravitymod.asm.EntityPlayerWithGravity;

/**
 * All the possible PrePostModifiers, all absolute is the default state, so is not included.
 * <p>
 * Created by Mysteryem on 2016-10-23.
 */
public enum EnumPrePostModifier implements IPrePostModifier<EntityPlayerWithGravity> {
    ALL_MOTION_RELATIVE() {
        @Override
        public void preModify(EntityPlayerWithGravity player) {
            player.makeMotionRelative();
        }

        @Override
        public void postModify(EntityPlayerWithGravity player) {
            player.popMotionStack();
        }
    },
    ABSOLUTE_X() {
        @Override
        public void preModify(EntityPlayerWithGravity player) {
            player.makeMotionRelative();
            player.storeMotionX();
        }

        @Override
        public void postModify(EntityPlayerWithGravity player) {
            double motionXChange = player.undoMotionXChange();
            player.popMotionStack();
            player.motionX += motionXChange;
        }
    },
    RELATIVE_Z() {
        @Override
        public void preModify(EntityPlayerWithGravity player) {
            player.makeMotionRelative();
            player.storeMotionX();
            player.storeMotionY();
        }

        @Override
        public void postModify(EntityPlayerWithGravity player) {
            double motionXChange = player.undoMotionXChange();
            double motionYChange = player.undoMotionYChange();
            player.popMotionStack();
            player.motionX += motionXChange;
            player.motionY += motionYChange;
        }
    },
    RELATIVE_Y() {
        @Override
        public void preModify(EntityPlayerWithGravity player) {
            player.makeMotionRelative();
            player.storeMotionX();
            player.storeMotionZ();
        }

        @Override
        public void postModify(EntityPlayerWithGravity player) {
            double motionXChange = player.undoMotionXChange();
            double motionZChange = player.undoMotionZChange();
            player.popMotionStack();
            player.motionX += motionXChange;
            player.motionZ += motionZChange;
        }
    },
    ABSOLUTE_Y() {
        @Override
        public void preModify(EntityPlayerWithGravity player) {
            player.makeMotionRelative();
            player.storeMotionY();
        }

        @Override
        public void postModify(EntityPlayerWithGravity player) {
            double motionYChange = player.undoMotionYChange();
            player.popMotionStack();
            player.motionY += motionYChange;
        }
    },
    RELATIVE_X() {
        @Override
        public void preModify(EntityPlayerWithGravity player) {
            player.makeMotionRelative();
            player.storeMotionY();
            player.storeMotionZ();
        }

        @Override
        public void postModify(EntityPlayerWithGravity player) {
            double motionYChange = player.undoMotionYChange();
            double motionZChange = player.undoMotionZChange();
            player.popMotionStack();
            player.motionY += motionYChange;
            player.motionZ += motionZChange;
        }
    },
    ABSOLUTE_Z() {
        @Override
        public void preModify(EntityPlayerWithGravity player) {
            player.makeMotionRelative();
            player.storeMotionZ();
        }

        @Override
        public void postModify(EntityPlayerWithGravity player) {
            double motionZChange = player.undoMotionZChange();
            player.popMotionStack();
            player.motionZ += motionZChange;
        }
    },
    ROTATION_RELATIVE() {
        @Override
        public void preModify(EntityPlayerWithGravity player) {
            player.makeRotationRelative();
        }

        @Override
        public void postModify(EntityPlayerWithGravity player) {
            player.popRotationStack();
        }
    };


    static {
    }

    EnumPrePostModifier() {
    }

    @Override
    public int getUniqueID() {
        return this.ordinal();
    }

}
