package tfar.gravitymod;

import tfar.gravitymod.client.listeners.GravityManagerClient;

/**
 * Created by Mysteryem on 2016-08-04.
 */
public class ClientProxy extends CommonProxy {
    @Override
    public void registerGravityManager() {
        this.gravityManagerCommon = new GravityManagerClient();
    }

}
