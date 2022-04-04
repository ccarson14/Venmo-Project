package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.TransferNotFoundException;
import com.techelevator.tenmo.exception.UserNotAuthorizedException;
import com.techelevator.tenmo.exception.UserNotFoundException;
import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    List<Transfer> findAll(String username);

    List<Transfer> findAllPending(String username);

    Transfer get(long transferId, String username) throws TransferNotFoundException;

    Transfer create(Transfer transfer, String username) throws TransferNotFoundException, UserNotAuthorizedException;

    Transfer update(Transfer transfer, long transferId, String username) throws TransferNotFoundException, UserNotAuthorizedException;

    void delete(long transferId, String username) throws TransferNotFoundException;

}
