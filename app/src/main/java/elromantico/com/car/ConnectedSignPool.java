package elromantico.com.car;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by angel on 3/20/2016.
 */
public class ConnectedSignPool {
    public static Set<ConnectedSign> connectedDevices = new HashSet<>();

    public void insert(ConnectedSign sign) {
        sign.insertInPool(this);
    }

    public List<ConnectedSign> toOrderedSignList() {
        List<ConnectedSign> orderedSignList = new ArrayList<>(connectedDevices);

        Collections.sort(orderedSignList, new Comparator<ConnectedSign>() {

            @Override
            public int compare(ConnectedSign lhs, ConnectedSign rhs) {
                return new Long(rhs.timestamp).compareTo(lhs.timestamp);
            }
        });

        return orderedSignList;
    }
}
