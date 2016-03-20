package elromantico.com.car;

/**
 * Created by angel on 3/20/2016.
 */
public class ConnectedSign {
    public long timestamp;
    public String signName;

    public ConnectedSign(String signName, long timestamp) {
        this.signName = signName;
        this.timestamp = timestamp;
    }

    @Override
    public int hashCode() {
        return signName.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConnectedSign)) return false;

        ConnectedSign that = (ConnectedSign) o;

        return signName.equals(that.signName);
    }

    public void insertInPool(ConnectedSignPool pool) {
        pool.connectedDevices.add(this);
    }
}
