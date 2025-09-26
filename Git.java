import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Git {


    public static void init() {
        File gitDir = new File("git");
        File objectsDir = new File(gitDir, "objects");
        File indexFile = new File(gitDir, "index");
        File headFile = new File(gitDir, "HEAD");

        if (gitDir.exists() && objectsDir.exists() && indexFile.exists() && headFile.exists()) {
            System.out.println("Git Repository Already Exists"); 
        }

        else{
            if (!gitDir.exists()) {
                gitDir.mkdir();
            }
    
            if (!objectsDir.exists()) {
                objectsDir.mkdir();
            }
    
            if (!indexFile.exists()) {
                try {
                    indexFile.createNewFile();
                } 
                catch (IOException e) {
                    System.out.println("Error creating index file: " + e.getMessage());
                }
            }
    
            if (!headFile.exists()) {
                try {
                    headFile.createNewFile();
                } 
                catch (IOException e) {
                    System.out.println("Error creating HEAD file: " + e.getMessage());
                }
            }

            System.out.println("Git Repository Created");
        }
    }


    public static String encryptThisString(String input) {
        try {
            // getInstance() method is called with algorithm SHA-1
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 40 digits long
            while (hashtext.length() < 40) {
                hashtext = "0" + hashtext;
            }

            // return the HashText
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String sha1FromFile(String filePath) {
        try {
            String content = Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
            return encryptThisString(content);
        } 
        catch (Exception e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }
    }

}