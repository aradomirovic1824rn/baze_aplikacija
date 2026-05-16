package org.baze.neuronauka.model;

import java.io.*;

public class UserRepository {
    private static final String FILE = "users.txt";


    private void ensureFileExists() {
        try {
            File file = new File(FILE);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void saveUser(User user){
        ensureFileExists();
        try(FileWriter fw = new FileWriter(FILE, true)){
            fw.write(user.toFileString() + "\n");
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public boolean userExists(String username){
        ensureFileExists();
        try(BufferedReader br = new BufferedReader(new FileReader(FILE))){
            String line;
            while((line = br.readLine()) != null){
                String[] parts = line.split(",");
                if(parts[0].equals(username)){
                    return true;
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        return false;
    }
}
