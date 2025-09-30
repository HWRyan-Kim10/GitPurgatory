import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
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

    public static void createBlob(String filePath) {
        File gitDir = new File("git");
        File objectsDir = new File(gitDir, "objects");
        if (!objectsDir.exists()) {
            init();
        }

        String hash = sha1FromFile(filePath);

        File blobFile = new File(objectsDir, hash);

        try {
            if (blobFile.exists()) {
                System.out.println("Blob already exists: " + hash);
                return;
            }
    
            copyContent(new File(filePath), blobFile);
    
            System.out.println("Blob created: " + hash);
        } 
        catch (Exception e) {
            System.out.println("Error creating blob: " + e.getMessage());
        }
    }

    
    public static void copyContent(File a, File b)
        throws Exception
    {
        FileInputStream in = new FileInputStream(a);
        FileOutputStream out = new FileOutputStream(b);

        try {

            int n;

            // read() function to read the
            // byte of data
            while ((n = in.read()) != -1) {
                // write() function to write
                // the byte of data
                out.write(n);
            }
        }
        finally {
            if (in != null) {

                // close() function to close the
                // stream
                in.close();
            }
            // close() function to close
            // the stream
            if (out != null) {
                out.close();
            }
        }
    }



    public static void addToIndex(String filePath) {
    try {
        File indexFile = new File("git", "index");
            if (!indexFile.exists()) {
                indexFile.getParentFile().mkdirs();
                indexFile.createNewFile();
            }

            String hash = sha1FromFile(filePath);
            String name = new File(filePath).getPath();
            String entry = hash + " " + name;

            boolean endsWithNewline = false;
            if (indexFile.length() > 0) {
                try (BufferedReader reader = new BufferedReader(new FileReader(indexFile))) {
                    String line;
                    String lastLine = null;
                    while ((line = reader.readLine()) != null) {
                        lastLine = line;
                    }
                    if (lastLine == null || lineEndsWithNewline(indexFile)) {
                        endsWithNewline = true;
                    }
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(indexFile, true))) {
                if (indexFile.length() == 0) {
                    writer.write(entry);
                } 
                else if (endsWithNewline) {
                    writer.write(entry);
                } 
                else {
                    writer.newLine();
                    writer.write(entry);
                }
            }

        }  
        catch (IOException e) {
            throw new RuntimeException("Error updating index", e);
        }
    }

    private static boolean lineEndsWithNewline(File file) throws IOException {
        if (file.length() == 0) {
            return false;
        }
        try (FileInputStream in = new FileInputStream(file)) {
            in.skip(file.length() - 1);
            return in.read() == '\n';
        }
    }

    public static void addTree(String [] entries) throws FileNotFoundException, IOException {
        File tree = new File("tree");
        tree.createNewFile();
        for(String entry : entries) {
            boolean endsWithNewline = false;
            if (tree.length() > 0) {
                try (BufferedReader reader = new BufferedReader(new FileReader(tree))) {
                    String line;
                    String lastLine = null;
                    while ((line = reader.readLine()) != null) {
                        lastLine = line;
                    }
                    if (lastLine == null || lineEndsWithNewline(tree)) {
                        endsWithNewline = true;
                    }
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tree, true))) {
                if (tree.length() == 0) {
                    writer.write(entry);
                } 
                else if (endsWithNewline) {
                    writer.write(entry);
                } 
                else {
                    writer.newLine();
                    writer.write(entry);
                }
            }
        }
        createBlob("tree");
        addToIndex("tree");
        tree.delete();
    }
    
}