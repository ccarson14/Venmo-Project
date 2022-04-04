package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.exception.InsufficientFundsException;
import com.techelevator.tenmo.exception.NegativeValueException;
import com.techelevator.tenmo.model.Account;



public interface AccountDao {

    Account findAccountByAccountId(long accountId) throws AccountNotFoundException;
    Account findAccountByUserId(long userId) throws AccountNotFoundException;

//    Account findBalance(long accountId) throws AccountNotFoundException;
    Account subtractBalance(long accountId, float balanceChange) throws AccountNotFoundException, InsufficientFundsException, NegativeValueException;
    Account addBalance(long accountId, float balanceChange) throws AccountNotFoundException, NegativeValueException;
}
