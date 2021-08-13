package tfar.gravitymod;

import tfar.gravitymod.common.listeners.GravityManagerCommon;

/**
 * Created by Mysteryem on 2016-08-04.
 */
public class CommonProxy {

    public GravityManagerCommon gravityManagerCommon;

    public void registerGravityManager() {
        this.gravityManagerCommon = new GravityManagerCommon();
    }

    public GravityManagerCommon getGravityManager() {
        return this.gravityManagerCommon;
    }

}
