package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.exception.InsufficientFundsException;
import com.techelevator.tenmo.exception.NegativeValueException;
import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class JdbcAccountDao implements AccountDao{
    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account findAccountByAccountId(long accountId) throws AccountNotFoundException {
        String sql = "SELECT account_id, user_id, balance FROM account WHERE account_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, accountId);
        if (rowSet.next()){
            return mapRowToAccount(rowSet);
        }
        throw new AccountNotFoundException();
    }

    @Override
    public Account findAccountByUserId(long userId) throws AccountNotFoundException {
        String sql = "SELECT account_id, user_id, balance FROM account WHERE user_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);
        if (rowSet.next()){
            return mapRowToAccount(rowSet);
        }
        throw new AccountNotFoundException();
    }

//    @Override
//    public Account findBalance(long accountId) throws AccountNotFoundException {
//        String sql = "SELECT balance FROM account WHERE account_id = ?;";
//        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, accountId);
//        if (rowSet.next()){
//            return mapRowToAccount(rowSet);
//        }
//        throw new AccountNotFoundException();
//    }

    @Override
    public Account subtractBalance(long accountId, float balanceChange) throws AccountNotFoundException, InsufficientFundsException, NegativeValueException {
        Account accountToUpdate = findAccountByAccountId(accountId);

        if (balanceChange <= 0) {
            throw new NegativeValueException();
        }
        if (balanceChange > accountToUpdate.getBalance()) {

            throw new InsufficientFundsException();

        } else {
            float newBalance = accountToUpdate.getBalance() - balanceChange;

            accountToUpdate.setBalance(newBalance);

            String sql = "UPDATE account " +
                    "SET user_id = ?, " +
                    "balance = ? " +
                    "WHERE account_id = ?;";

            jdbcTemplate.update(sql,
                    accountToUpdate.getUserId(),
                    accountToUpdate.getBalance(),
                    accountToUpdate.getAccountId());

            return findAccountByAccountId(accountId);
        }
    }

    @Override
    public Account addBalance(long accountId, float balanceChange) throws AccountNotFoundException, NegativeValueException {
        Account accountToUpdate = findAccountByAccountId(accountId);

        if (balanceChange <= 0) {
            throw new NegativeValueException();
        }
        float newBalance = accountToUpdate.getBalance() + balanceChange;

        accountToUpdate.setBalance(newBalance);

        String sql = "UPDATE account " +
                    "SET user_id = ?, " +
                    "balance = ? " +
                    "WHERE account_id = ?;";

        jdbcTemplate.update(sql,
                    accountToUpdate.getUserId(),
                    accountToUpdate.getBalance(),
                    accountToUpdate.getAccountId());

        return findAccountByAccountId(accountId);

    }

    private Account mapRowToAccount(SqlRowSet rs) {
        Account account = new Account();
        account.setAccountId(rs.getLong("account_id"));
        account.setUserId(rs.getLong("user_id"));
        account.setBalance(rs.getFloat("balance"));
        return account;
    }
}
