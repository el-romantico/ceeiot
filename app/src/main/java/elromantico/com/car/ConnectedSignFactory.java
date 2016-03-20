package elromantico.com.car;

/**
 * Created by angel on 3/20/2016.
 */
public class ConnectedSignFactory {
    public static ConnectedSign create(String signName, long timeStamp) {
        switch(signName) {
            default:
                return new ConnectedSign(signName, timeStamp);
        }
    }
}
