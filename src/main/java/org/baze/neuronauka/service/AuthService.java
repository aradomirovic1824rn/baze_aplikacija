package org.baze.neuronauka.service;
import org.baze.neuronauka.model.User;
import org.baze.neuronauka.model.UserRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AuthService {
    private UserRepository repo = new UserRepository();

    public boolean register(String username, String password){
        if(repo.userExists(username)) return false;

        repo.saveUser(new org.baze.neuronauka.model.User(username, password));
        return true;
    }

    public String login(String username, String password){
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                String fileUser = parts[0];
                String filePass = parts[1];

                if (fileUser.equals(username) && filePass.equals(password)) {
                    return "LOGIN_OK";
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "USER_NOT_FOUND";
    }
}
