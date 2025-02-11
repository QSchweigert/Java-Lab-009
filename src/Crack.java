import org.apache.commons.codec.digest.Crypt;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.stream.Stream;

public class Crack {
    private final User[] users;
    private final String dictionary;

    public Crack(String shadowFile, String dictionary) throws FileNotFoundException {
        this.dictionary = dictionary;
        this.users = Crack.parseShadow(shadowFile);
    }

    public void crack() throws FileNotFoundException {
        Scanner s = new Scanner(new FileInputStream(this.dictionary));
        while (s.hasNextLine()) {
            String word = s.nextLine();
            for (User user : users) {
                if (user.getPassHash().startsWith("$")){
                    String hash = Crypt.crypt(word, user.getPassHash());
                    if(hash.equals(user.getPassHash())){
                        System.out.printf("Found password %s for user %s.%n", word, user.getUserName());
                    }
                }
            }
        }
    }

    public static int getLineCount(String path) {
        int lineCount = 0;
        try (Stream<String> stream = Files.lines(Path.of(path), StandardCharsets.UTF_8)) {
            lineCount = (int)stream.count();
        } catch(IOException ignored) {}
        return lineCount;
    }

    public static User[] parseShadow(String shadowFile) throws FileNotFoundException {
        Scanner s = new Scanner(new FileInputStream(shadowFile));
        User[] users = new User[getLineCount(shadowFile)];
        int i;
        for(i = 0;s.hasNextLine(); i++){
            String[] line = s.nextLine().split(":");
            User u = new User(line[0], line[1]);
            users[i] = u;
        }
       return users;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Type the path to your shadow file: ");
        String shadowPath = sc.nextLine();
        System.out.print("Type the path to your dictionary file: ");
        String dictPath = sc.nextLine();

        Crack c = new Crack(shadowPath, dictPath);
        c.crack();
    }
}
