import java.io.File;
import java.io.IOException;

public class Git {

    public static void init() {
        File gitDir = new File("git");
        File objectsDir = new File(gitDir, "objects");
        File indexFile = new File(gitDir, "index");
        File headFile = new File(gitDir, "HEAD");


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

        if (gitDir.exists() && objectsDir.exists() && indexFile.exists() && headFile.exists()) {
            System.out.println("Git Repository Already Exists"); 
        }
        else{
            System.out.println("Git Repository Created");
        }
    }


}