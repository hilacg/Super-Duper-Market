package course.java.sdm.engine;

import java.util.*;

/*
Adding and retrieving users is synchronized and in that manner - these actions are thread safe
Note that asking if a user exists (isUserExists) does not participate in the synchronization and it is the responsibility
of the user of this class to handle the synchronization of isUserExists with other methods here on it's own
 */
public class UserManager {

    private final Map<String,String> usersMap;

    public UserManager() {
        usersMap = new HashMap<>();
    }

    public synchronized void addUser(String username,String type) {
        usersMap.put(username,type);
    }

    public synchronized void removeUser(String username) {
        usersMap.remove(username);
    }

    public synchronized Map<String,String> getUsers() {
        return Collections.unmodifiableMap(usersMap);
    }

    public boolean isUserExists(String username) {
        return usersMap.containsKey(username);
    }
}
