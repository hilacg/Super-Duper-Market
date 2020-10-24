package course.java.sdm.engine;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Account {
    private double balance = 0;
    private List<AccountAction> actions = new ArrayList<>();

    public List<AccountAction> getActions() {
        return actions;
    }

    public double getBalance() {
        return balance;
    }

    public void updateAccount(String action, double amount, String date) {
        AccountAction newAction = new AccountAction(action,amount,date);
        if(action.equals("deposit"))
            this.balance += amount;
        else
            this.balance -= amount;

        newAction.currBalance = this.balance;
        actions.add(newAction);
    }



    public class AccountAction {
        private String type;
        private String date;
        private double amount;
        private double prevBalance;
        private double currBalance;

        public AccountAction(String action, double amount, String date) {
            this.type = action;
            this.date = date;
            this.amount = amount;
            this.prevBalance = balance;
        }

        public double getAmount() {
            return amount;
        }

        public double getCurrBalance() {
            return currBalance;
        }

        public double getPrevBalance() {
            return prevBalance;
        }

        public String getDate() {
            return date;
        }

        public String getType() {
            return type;
        }
    }
}
