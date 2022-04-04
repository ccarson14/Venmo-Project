package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.TransferNotFoundException;
import com.techelevator.tenmo.exception.UserNotAuthorizedException;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{
    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Transfer> findAll(String username) {

        List<Transfer> transfers = new ArrayList<>();

        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, " +
                "account_from, account_to, amount " +
                "FROM transfer " +
                "WHERE account_from = " +
                "(SELECT account_id FROM account JOIN tenmo_user USING (user_id) WHERE username = ?) " +
                "OR account_to = " +
                "(SELECT account_id FROM account JOIN tenmo_user USING (user_id) WHERE username = ?);";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username, username);


        while(results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            transfers.add(transfer);
        }

        return transfers;

    }

    @Override
    public List<Transfer> findAllPending(String username) {

        List<Transfer> transfers = new ArrayList<>();

        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, " +
                "account_from, account_to, amount " +
                "FROM transfer " +
                "WHERE (account_from = " +
                "(SELECT account_id FROM account JOIN tenmo_user USING (user_id) WHERE username = ?) " +
                "OR account_to = " +
                "(SELECT account_id FROM account JOIN tenmo_user USING (user_id) WHERE username = ?)); " +
                "AND transfer_status_id = 1;";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username, username);

        while(results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            transfers.add(transfer);
        }

        return transfers;

    }

    @Override
    public Transfer get(long transferId, String username) throws TransferNotFoundException {

        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, " +
                "account_from, account_to, amount " +
                "FROM transfer " +
                "WHERE transfer_id = ? AND " +
                "(account_from = " +
                "(SELECT account_id FROM account JOIN tenmo_user USING (user_id) WHERE username = ?) " +
                "OR account_to = " +
                "(SELECT account_id FROM account JOIN tenmo_user USING (user_id) WHERE username = ?));";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId, username, username);

        if (results.next()) {
            return mapRowToTransfer(results);
        }

        throw new TransferNotFoundException();
    }

    @Override
    public Transfer create(Transfer transfer, String username) throws TransferNotFoundException, UserNotAuthorizedException {

        String sql = "SELECT account_id " +
                "FROM account " +
                "JOIN tenmo_user USING (user_id) " +
                "WHERE username = ?;";

        Long userId = jdbcTemplate.queryForObject(sql, Long.class, username);

                sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, " +
                        "account_from, account_to, amount) " +
                        "VALUES (?, ?, ?, ?, ?) " +
                        "RETURNING transfer_id;";

                Long transferId = jdbcTemplate.queryForObject(sql, Long.class, transfer.getTransferTypeId(),
                        transfer.getTransferStatusId(), transfer.getAccountFrom(),
                        transfer.getAccountTo(), transfer.getAmount());

                if (transferId != null) {

                    return get(transferId, username);

                } else {

                    throw new TransferNotFoundException();
                }

    }

    @Override
    public Transfer update(Transfer transfer, long transferId, String username) throws TransferNotFoundException, UserNotAuthorizedException {

        String sql = "SELECT account_id " +
                "FROM account " +
                "JOIN tenmo_user USING (user_id) " +
                "WHERE username = ?;";

        Long userAccountId = jdbcTemplate.queryForObject(sql, Long.class, username);

        if (userAccountId != null &&
                (userAccountId == transfer.getAccountFrom() || userAccountId == transfer.getAccountTo())) {

            sql = "UPDATE transfer " +
                    "SET transfer_id = ?, " +
                    "transfer_type_id = ?, " +
                    "transfer_status_id = ?, " +
                    "account_from = ?, " +
                    "account_to = ?, " +
                    "amount = ? " +
                    "WHERE transfer_id = ? AND " +
                    "(account_from = " +
                    "(SELECT account_id FROM account JOIN tenmo_user USING (user_id) WHERE username = ?) " +
                    "OR account_to = " +
                    "(SELECT account_id FROM account JOIN tenmo_user USING (user_id) WHERE username = ?));";

            jdbcTemplate.update(sql,
                    transfer.getTransferId(),
                    transfer.getTransferTypeId(),
                    transfer.getTransferStatusId(),
                    transfer.getAccountFrom(),
                    transfer.getAccountTo(),
                    transfer.getAmount(),
                    transferId,
                    username,
                    username);

            return get(transferId, username);

        } else {
            throw new UserNotAuthorizedException();
        }

    }

    @Override
    public void delete(long transferId, String username) throws TransferNotFoundException {

        String sql = "DELETE FROM transfer WHERE transfer_id = ? AND " +
                "(account_from = " +
                "(SELECT account_id FROM account JOIN tenmo_user USING (user_id) WHERE username = ?) " +
                "OR account_to = " +
                "(SELECT account_id FROM account JOIN tenmo_user USING (user_id) WHERE username = ?));";

        int rowsDeleted = jdbcTemplate.update(sql, transferId, username, username);

        if (rowsDeleted == 0) throw new TransferNotFoundException();
    }

    private Transfer mapRowToTransfer(SqlRowSet results) {

        Transfer transfer = new Transfer();

        transfer.setTransferId(results.getInt("transfer_id"));
        transfer.setTransferTypeId(results.getInt("transfer_type_id"));
        transfer.setTransferStatusId(results.getInt("transfer_status_id"));
        transfer.setAccountFrom(results.getInt("account_from"));
        transfer.setAccountTo(results.getInt("account_to"));
        transfer.setAmount(results.getFloat("amount"));

        return transfer;
    }

}
