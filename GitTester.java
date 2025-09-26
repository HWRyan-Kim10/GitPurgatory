import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GitTester {
    public static void verifyInit() {
        File gitDir = new File("git");
        File objectsDir = new File(gitDir, "objects");
        File indexFile = new File(gitDir, "index");
        File headFile = new File(gitDir, "HEAD");

        if(gitDir.exists() && objectsDir.exists() && indexFile.exists() && headFile.exists()){
            System.out.println("Initialization Verified: PASS");
        }
        else{
            System.out.println("Initialization Verified: FAIL");
        }
    }

    public static void cleanup(){
        delete(new File("git"));
        System.out.println("Cleanup complete.");
    }

    private static void delete(File file){
        if (!file.exists()){
            return;
        }
        if (file.isDirectory()) {
            for (File child: file.listFiles()) {
                delete(child);
            }
        }
        file.delete();
    }


    public static void verifyBlobExists(String filePath) {
        File objectsDir = new File("git", "objects");
        if (!objectsDir.exists()) {
            System.out.println("Blob Verification: FAIL (objects directory missing)");
            return;
        }

        String expectedHash = Git.sha1FromFile(filePath);
        File blobFile = new File(objectsDir, expectedHash);

        if(blobFile.exists()) {
            System.out.println("Blob Verification: PASS (" + expectedHash + " found in objects)");
        } 
        else {
            System.out.println("Blob Verification: FAIL (" + expectedHash + " not found in objects)");
        }
    }

    public static void resetForRetest() {
        cleanup();

        createTestFile("hello.txt", "hello\n");
        createTestFile("world.txt", "world\n");

        System.out.println("Reset complete.");
    }


    private static void createTestFile(String name, String content) {
        try (FileWriter fw = new FileWriter(name)){
            fw.write(content);
        } 
        catch (IOException e) {
            System.out.println("Error creating " + name + ": " + e.getMessage());
        }
    }


    public static void main(String[] args){
        resetForRetest();

        for (int i = 1; i <= 3; i++) {
            Git.init();
            verifyInit();

            Git.createBlob("hello.txt");
            Git.createBlob("world.txt");

            verifyBlobExists("hello.txt");
            verifyBlobExists("world.txt");

            resetForRetest();
        }

        String hash = Git.sha1FromFile("shaone.txt");
        System.out.println("SHA1 of file contents: " + hash);
    }

}
