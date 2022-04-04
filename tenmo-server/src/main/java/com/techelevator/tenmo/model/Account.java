package com.techelevator.tenmo.model;

import javax.validation.constraints.Positive;

public class Account {

    private long accountId;

    @Positive(message = "userId must be positive.")
    private long userId;
    @Positive(message = "Balance must be positive.")
    private float balance;

    public Account(){}

    public Account(long accountId, long userId, Float balance) {
        this.accountId = accountId;
        this.userId = userId;
        this.balance = balance;
    }
//getters
    public long getAccountId() {
        return accountId;
    }

    public long getUserId() {
        return userId;
    }

    public float getBalance() {
        return balance;
    }

//setters
    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "account_id=" + accountId +
                ", user_id='" + userId + '\'' +
                ", balance=" + balance +
                '}';
    }
}
