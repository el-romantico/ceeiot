package elromantico.com.car;

/**
 * Created by angel on 3/20/2016.
 */
public class ConnectedSignFactory {
    public static ConnectedSign create(String signName, long timeStamp) {

        Signs sign = Signs.valueOf(signName);

        switch(sign) {
            case B1:
            case B2:
            case B3:
                return new ClearingConnectedSign(signName, timeStamp);
            default:
                return new ConnectedSign(signName, timeStamp);
        }
    }
}
