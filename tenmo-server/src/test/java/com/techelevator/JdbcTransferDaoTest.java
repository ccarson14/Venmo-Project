package com.techelevator;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.exception.TransferNotFoundException;
import com.techelevator.tenmo.exception.UserNotAuthorizedException;
import com.techelevator.tenmo.model.Transfer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

public class JdbcTransferDaoTest extends BaseDaoTest{

    private Transfer testTransfer;

    private JdbcTransferDao sut;

    @Before
    public void setup() {
        sut = new JdbcTransferDao(new JdbcTemplate(dataSource));
        testTransfer = new Transfer(0, 2, 2, 2001, 2002, 100);
    }

    @Test
    public void findAll_returns_4_when_given_user_1(){
        List<Transfer> results = sut.findAll("user1");
        Assert.assertEquals(4, results.size());
    }

    @Test
    public void findAll_returns_empty_List_when_given_user_4(){
        List<Transfer> results = sut.findAll("user5");
        Assert.assertEquals(0, results.size());
    }

    @Test
    public void findAllPending_returns_2_when_given_user_2(){
        List<Transfer> results = sut.findAllPending("user2");
        Assert.assertEquals(2, results.size());
    }

    @Test
    public void get_returns_correct_transfer_for_id_and_user() throws TransferNotFoundException {
        Transfer transferToMatch = sut.findAll("user1").get(0);
        Transfer transfer = sut.get(transferToMatch.getTransferId(), "user1");
        assertTransfersMatch(transferToMatch, transfer);
    }

    @Test(expected = TransferNotFoundException.class)
    public void get_throws_TransferNotFound_for_bogus_id() throws TransferNotFoundException {
        sut.get(99999, "user1");
    }

    @Test(expected = TransferNotFoundException.class)
    public void get_throws_TransferNotFound_for_wrong_user() throws TransferNotFoundException {
        Transfer transferToMatch = sut.findAll("user1").get(0);
        sut.get(transferToMatch.getTransferId(), "user5");
    }

    @Test
    public void create_creates_Transfer_as_expected() throws TransferNotFoundException, UserNotAuthorizedException {
        Transfer createdTransfer = sut.create(testTransfer, "user1");

        long newId = createdTransfer.getTransferId();
        Assert.assertTrue(newId > 0);

        testTransfer.setTransferId(newId);
        assertTransfersMatch(testTransfer, createdTransfer);
    }

    @Test(expected = UserNotAuthorizedException.class)
    public void create_throws_UserNotAuthorizedException_for_wrong_user() throws TransferNotFoundException, UserNotAuthorizedException {
        sut.create(testTransfer, "user2");
    }

    @Test
    public void created_Transfer_has_expected_values_when_retrieved() throws TransferNotFoundException, UserNotAuthorizedException {
        Transfer createdTransfer = sut.create(testTransfer, "user1");

        long newId = createdTransfer.getTransferId();
        Transfer retrievedTransfer = sut.get(newId, "user1");

        assertTransfersMatch(createdTransfer, retrievedTransfer);
    }

    @Test
    public void updated_Transfer_has_expected_values_when_retrieved() throws TransferNotFoundException, UserNotAuthorizedException {
        Transfer transferToUpdate = sut.findAll("user1").get(0);
        long transferId = transferToUpdate.getTransferId();

        transferToUpdate.setAmount(999);
        transferToUpdate.setTransferStatusId(3);

        sut.update(transferToUpdate, transferId, "user1");

        Transfer retrievedTransfer = sut.get(transferId, "user1");
        assertTransfersMatch(transferToUpdate, retrievedTransfer);

    }

    @Test(expected = TransferNotFoundException.class)
    public void deleted_Transfer_throws_TransferNotFoundException_when_retrieved() throws TransferNotFoundException {
        Transfer transferToDelete = sut.findAll("user1").get(0);
        long transferId = transferToDelete.getTransferId();

        sut.delete(transferId, "user1");

        Transfer retrievedTransfer = sut.get(transferId, "user1");
        Assert.assertNull(retrievedTransfer);
    }



    public void assertTransfersMatch(Transfer expected, Transfer actual){
        Assert.assertEquals(expected.getTransferId(), actual.getTransferId());
        Assert.assertEquals(expected.getTransferStatusId(), actual.getTransferStatusId());
        Assert.assertEquals(expected.getTransferTypeId(), actual.getTransferTypeId());
        Assert.assertEquals(expected.getAccountFrom(), actual.getAccountFrom());
        Assert.assertEquals(expected.getAccountTo(), actual.getAccountTo());
        Assert.assertEquals(expected.getAmount(), actual.getAmount(), 0.1);
    }

}
