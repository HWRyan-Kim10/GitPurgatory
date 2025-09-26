# GitPurgatory
2.1
- The init() method in the 'Git.java' class initializes a Git repository. The method, in order, creates the git directory if it doesn't already exist, creates the objects directory inside git directory if it doesn't already exist, creates the index file inside the git directory if it doesn't already exist, then creates the HEAD file inside the git directory if it doens't already exist.
    - if-else statement at the beginning of the method checks whether the repository was created and can print "Git Repository Created" or if all directories and files already existed and instead prints out "Git Repository Already Exists"
- TESTER: In 'public static void main' there is a for-loop. Inside of the for-loop, 'System.out.println' tells you which cycle you are on. Next, 'Git.init()' calls the init() method that is in 'Git.java'. Next, the verifyInit() method that is in GitTester is called and it checks to see if all the files and directories that should've been made exists and if it does it prints "Initialization Verified: PASS". If even one file or directory does not exist when it should "Initialization Verified: FAIL" gets printed instead. Finally the cleanup() method is called which deletes the files that were created by running the tester. It does so by calling the delete() method on the git directory. This method checks to see if the method that is trying to be deleted actually exists first and then checks to see if what's trying to be deleted is actually a directory. The delete method is then called on each child of the directory being deleted with a for-each loop causing complete deletion of the directory and its contents through recursion. Once finished the cleanup method prints "Cleanup complete". 

2.2
- The method encryptThisString performs a SHA1 hash on whatever string is the input and I pulled it from the following link: https://www.geeksforgeeks.org/java/sha-1-hash-in-java/
- The method sha1FromFile takes the path of the file that you are trying to hash and converts the contents into a string (it does so by using the encryptThisString method). I learned about the readString method at this link: https://www.geeksforgeeks.org/java/files-class-readstring-method-in-java-with-examples/ 
- I made hello.txt for the tester of these two methods. Inside of hello.txt are the words hello world so I compared the output of the sha1FromFile method to the sha1 string converter link provided on the assignment.



