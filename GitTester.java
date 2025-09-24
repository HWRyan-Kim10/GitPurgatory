import java.io.File;

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

    public static void main(String[] args){
        for (int i = 1; i <= 3; i++) {
            System.out.println("\nCycle " + i + ":");
            Git.init();
            verifyInit();
            cleanup();
        }
    }
}
