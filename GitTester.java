import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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


        public static void runIndexSuite() {
        System.out.println("== Running Index Suite ==");
        resetWorkspace();
        Git.init();

        String[][] samples = new String[][]{
            {"alpha.txt",   "aaa\n"},
            {"beta.txt",    "bbb\nccc\n"},
            {"gamma.txt",   "The quick brown fox\njumps over the lazy dog\n"},
            {"delta.txt",   ""}
        };
        for (String[] s : samples) createTextFile(s[0], s[1]);

        for (String[] s : samples) {
            String name = s[0];
            Git.createBlob(name);
            Git.addToIndex(name);
            verifyBlobExists(name);
        }

        boolean ok = verifyIndexMatches(samples);
        if (ok) {
            System.out.println("Index Verification: PASS");
        } 
        else {
            System.out.println("Index Verification: FAIL");
        }
        System.out.println();
    }

    private static boolean verifyIndexMatches(String[][] samples) {
        List<String> expected = new ArrayList<>();
        for (String[] s : samples) {
            String name = s[0];
            String hash = Git.sha1FromFile(name);
            expected.add(hash + " " + name);
        }

        List<String> actual = readAllLines(new File("git", "index"));

        boolean sizesEqual = expected.size() == actual.size();
        if (!sizesEqual) {
            System.out.println("Mismatch count: expected " + expected.size() + " lines, found " + actual.size());
            dumpDiff(expected, actual);
            return false;
        }

        for (int i = 0; i < expected.size(); i++) {
            if (!expected.get(i).equals(actual.get(i))) {
                System.out.println("Line " + (i + 1) + " mismatch");
                dumpDiff(expected, actual);
                return false;
            }
        }
        return true;
    }

    private static void dumpDiff(List<String> expected, List<String> actual) {
        System.out.println("---- Expected ----");
        for (String s : expected) {
            System.out.println(s);
        }
        System.out.println("---- Actual   ----");
        for (String s : actual) {
            System.out.println(s);
        }
        System.out.println("------------------");
    }

    private static List<String> readAllLines(File f) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(f, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) lines.add(line);
        } 
        catch (IOException e) {
            throw new RuntimeException("Error reading " + f, e);
        }
        return lines;
    }


    public static void resetWorkspace() {
        deleteRecursively(new File("git"));

        deleteIfExists(new File("alpha.txt"));
        deleteIfExists(new File("beta.txt"));
        deleteIfExists(new File("gamma.txt"));
        deleteIfExists(new File("delta.txt"));
        deleteIfExists(new File("hello.txt"));
        deleteIfExists(new File("world.txt"));

        Git.init();
        File index = new File("git", "index");
        try (FileWriter fw = new FileWriter(index, false)) {
        } 
        catch (IOException e) {
            throw new RuntimeException("Unable to reset index", e);
        }
        System.out.println("Workspace reset.");
    }

    private static void deleteIfExists(File f) {
        if (f.exists()) {
            f.delete();
        }
        
    }

    private static void deleteRecursively(File f) {
        if (!f.exists()) return;
        if (f.isDirectory()) {
            File[] kids = f.listFiles();
            if (kids != null) {
                for (File k : kids) deleteRecursively(k);
            }
        }
        f.delete();
    }


    private static void createTextFile(String name, String content) {
        try (FileWriter fw = new FileWriter(name, StandardCharsets.UTF_8, false)) {
            fw.write(content);
        } 
        catch (IOException e) {
            throw new RuntimeException("Error creating " + name, e);
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

        runIndexSuite();
        resetWorkspace();
        runIndexSuite();
        resetWorkspace();
    }


    
}
