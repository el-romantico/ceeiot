package elromantico.com.car;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by angel on 3/20/2016.
 */
public class ConnectedSignPool {
    public static Set<ConnectedSign> connectedDevices = new HashSet<>();

    public void insert(ConnectedSign sign) {
        sign.insertInPool(this);
    }

    public ArrayList<ConnectedSign> toOrderedSignList() {
        // TODO: Sort by timestamp
        return new ArrayList<>(connectedDevices);
    }
}
