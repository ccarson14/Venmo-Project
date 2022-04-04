package com.techelevator;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.exception.InsufficientFundsException;
import com.techelevator.tenmo.exception.NegativeValueException;
import com.techelevator.tenmo.model.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

public class JbdcAccountTest extends BaseDaoTest{

    private JdbcAccountDao sut;

    @Before
    public void setup() {
        sut = new JdbcAccountDao(new JdbcTemplate(dataSource));
    }

    @Test
    public void findAccountByAccountId_returns_correct_Account() throws AccountNotFoundException {
        Account results = sut.findAccountByAccountId(2001);

        Assert.assertEquals(1001, results.getUserId());
    }

    @Test(expected = AccountNotFoundException.class)
    public void findAccountsByAccountId_throws_AccountNotFoundException_as_expected() throws AccountNotFoundException {
        sut.findAccountByAccountId(314159);
    }

    @Test
    public void findAccountByUserId_returns_correct_Account() throws AccountNotFoundException {
        Account results = sut.findAccountByUserId(1001);

        Assert.assertEquals(1001, results.getUserId());
    }

    @Test(expected = AccountNotFoundException.class)
    public void findAccountByUserId_throws_AccountNotFoundException_as_expected() throws AccountNotFoundException {
        sut.findAccountByAccountId(314159);
    }

    @Test
    public void subtractBalance_subtracts_as_expected() throws InsufficientFundsException, AccountNotFoundException, NegativeValueException {

        Account account = sut.subtractBalance(2001, 100);
        Assert.assertEquals(900, account.getBalance(), 0.1);
    }

    @Test(expected = InsufficientFundsException.class)
    public void subtractBalance_throws_InsufficientFundsException_as_expected() throws InsufficientFundsException, AccountNotFoundException, NegativeValueException {
        sut.subtractBalance(2001, 9999999);
    }

    @Test(expected = AccountNotFoundException.class)
    public void subtractBalance_throws_AccountNotFoundException_as_expected() throws InsufficientFundsException, AccountNotFoundException, NegativeValueException {
        sut.subtractBalance(999999, 20);
    }

    @Test(expected = NegativeValueException.class)
    public void subtractBalance_throws_NegativeValueException_as_expected() throws NegativeValueException, InsufficientFundsException, AccountNotFoundException {
        sut.subtractBalance(2001, -100);
    }

    @Test
    public void addBalance_adds_as_expected() throws AccountNotFoundException, NegativeValueException {
        Account account = sut.addBalance(2001, 100);
        Assert.assertEquals(1100, account.getBalance(), 0.1);
    }

    @Test (expected = AccountNotFoundException.class)
    public void addBalance_throws_AccountNotFoundException_as_expected() throws AccountNotFoundException, NegativeValueException {
        sut.addBalance(999999, 100);
    }

    @Test (expected = NegativeValueException.class)
    public void addBalance_throws_NegativeValueException_as_expected() throws NegativeValueException, AccountNotFoundException {
        sut.addBalance(2001, -10);
    }

    public void assertAccountsMatch(Account expected, Account actual) {
        Assert.assertEquals(expected.getAccountId(), actual.getAccountId());
        Assert.assertEquals(expected.getUserId(), actual.getUserId());
        Assert.assertEquals(expected.getBalance(), actual.getBalance(), 0.1);
    }
}
