package course.java.sdm.engine;

import java.util.*;

/*
Adding and retrieving users is synchronized and in that manner - these actions are thread safe
Note that asking if a user exists (isUserExists) does not participate in the synchronization and it is the responsibility
of the user of this class to handle the synchronization of isUserExists with other methods here on it's own
 */
public class UserManager {
    private final List<String> usersList = new ArrayList<>();
    private Map<Integer, Customer> allCustomers = new HashMap<>();
    private Map<Integer, StoreOwner> allStoreOwners = new HashMap<>();


    public synchronized List<String>getUsers() {
        return Collections.unmodifiableList(usersList);
    }

    public synchronized Boolean isCustomer(Integer userId) {
      //  return usersMap.get(userName);
        return allCustomers.containsKey(userId);

    }

    public boolean isUserExists(String username) {
        return usersList.contains(username);
    }

    public synchronized void addUser(int id, String userName, boolean isCustomer){
        usersList.add(userName);
        if(isCustomer)
            allCustomers.put(id, new Customer(id,userName));
        else
            allStoreOwners.put(id, new StoreOwner(id,userName));
    }

    public Map<Integer, Customer> getAllCustomers() {
        return allCustomers;
    }

    public Map<Integer, StoreOwner> getAllStoreOwners() {
        return allStoreOwners;
    }

    public String getUserName(Integer userId) {
        return allCustomers.containsKey(userId) ? allCustomers.get(userId).getName() : allStoreOwners.get(userId).getName();
    }

    public void updateAccount(Integer userIdFromSession, String action, double amount, String date) {
        Account account = isCustomer(userIdFromSession)? allCustomers.get(userIdFromSession).getAccount() :allStoreOwners.get(userIdFromSession).getAccount();
            account.updateAccount(action, amount,date);

    }

    public List<Account.AccountAction> getUserActions(Integer userIdFromSession) {
        return isCustomer(userIdFromSession)? allCustomers.get(userIdFromSession).getAccount().getActions() :  allStoreOwners.get(userIdFromSession).getAccount().getActions();
    }
}
