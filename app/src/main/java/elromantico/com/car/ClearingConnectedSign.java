package elromantico.com.car;

/**
 * Created by angel on 3/20/2016.
 */
public class ClearingConnectedSign extends ConnectedSign {

    public ClearingConnectedSign(String signName, long timestamp) {
        super(signName, timestamp);
    }

    @Override
    public void insertInPool(ConnectedSignPool pool) {
        pool.connectedDevices.clear();
        super.insertInPool(pool);
    }

}
