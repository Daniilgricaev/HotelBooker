package services;

import model.Role;
import model.User;
import storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.*;


@Service
public class UserService {
    private final Storage storage;
    private final AtomicInteger userIDCnt;
    @Autowired
    public UserService(Storage storage) {
        this.storage = storage;
        this.userIDCnt = new AtomicInteger(storage.getUsers().stream().mapToInt(User::getId).max().orElse(0));
    }

    public User authenticate(String username, String password) {
        return storage.getUsers().stream().filter(u -> u.getLogin().equals(username) && u.getPassword().equals(password)).findFirst().orElse(null);
    }

    public User registerUser(String username, String password) {
        if (storage.getUsers().stream().anyMatch(u -> u.getLogin().equals(username))) {
            return null;
        }
        int newId = userIDCnt.incrementAndGet();
        User newUser = new User(newId, username, password,Role.USER);
        storage.getUsers().add(newUser);
        storage.saveUsers();
        return newUser;
    }

    public boolean updateUser(int userId, String newUsername, String newPassword) {
        User userUpdate = storage.getUsers().stream().filter(u -> u.getId() == userId).findFirst().orElse(null);
        if (userUpdate != null) {
            if (!newUsername.equals(userUpdate.getLogin()) && storage.getUsers().stream().anyMatch(u -> u.getLogin().equals(newUsername))){
                System.out.println("это имя уже занято");
                return false;
            }
            userUpdate.setLogin(newUsername);
            userUpdate.setPassword(newPassword);
            storage.saveUsers();
            return true;
        }
        return false;
    }

    public boolean isLoginExist( String username) {
        return storage.getUsers().stream().anyMatch(u->u.getLogin().equalsIgnoreCase(username));
    }

}
