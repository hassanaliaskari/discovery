package travnet.discovery;

/**
 * Created by Sunny on 20/8/2016.
 */
public class S3Key {

    private String keyID;
    private String secretKey;

    private static S3Key ourInstance = new S3Key();

    public static S3Key getInstance() {
        return ourInstance;
    }

    private S3Key() {
    }

    public String getKeyID() {
        return keyID;
    }

    public void setKeyID(String keyID) {
        this.keyID = keyID;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
