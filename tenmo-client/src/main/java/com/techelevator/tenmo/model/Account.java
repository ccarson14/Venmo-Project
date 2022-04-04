package com.techelevator.tenmo.model;

public class Account {

    private Long accountId;
    private Long userId;
    private Long balance;

    //getters
    public Long getAccountId() {
        return accountId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getBalance() {
        return balance;
    }

    //setters
    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }
}
