package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.*;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/transfer")
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private final UserDao userDao;
    private final TransferDao transferDao;
    private final AccountDao accountDao;

    //TRANSFER TYPE IDS
    private final long REQUEST = 1;
    private final long SEND = 2;

    //TRANSFER STATUS IDS
    private final long PENDING = 1;
    private final long APPROVED = 2;
    private final long REJECTED = 3;

    public TransferController(UserDao userDao, TransferDao transferDao, AccountDao accountDao) {
        this.userDao = userDao;
        this.transferDao = transferDao;
        this.accountDao = accountDao;
    }




    /**
     * Return all transfers made to/from the user
     */

    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping("")
    public List<Transfer> listTransfers(Principal principal) throws UserNotFoundException {
        return transferDao.findAll(principal.getName());
    }

    /**
     * Return all pending transfers to/from user
     */

    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping("/pending")
    public List<Transfer> listPendingTransfers(Principal principal) {
        return transferDao.findAllPending(principal.getName());
    }

    /**
     * Return transfer by id
     */

    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping("/{id}")
    public Transfer getTransfer(@PathVariable int id, Principal principal) throws TransferNotFoundException {
        return transferDao.get(id, principal.getName());
    }

    /**
     * Initiate a new transfer
     */

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public Transfer createTransfer(@Valid @RequestBody Transfer transfer, Principal principal)
            throws TransferNotFoundException, UserNotAuthorizedException, AccountNotFoundException,
            InsufficientFundsException, NegativeValueException {

        if(transfer.getTransferTypeId() == SEND) {
            return processSend(transfer, principal);

        } else if(transfer.getTransferTypeId() == REQUEST) {
            return processRequest(transfer, principal);
        }

        throw new TransferNotFoundException();
    }

    /**
     * Approve a transfer
     */

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/approve/{id}")
    @Transactional
    public Transfer approveTransfer(@PathVariable long id, Principal principal) throws NegativeValueException,
            TransferNotFoundException, UserNotAuthorizedException, InsufficientFundsException,
            AccountNotFoundException {

        return approveRequest(id, principal);

    }


    /**
     * Reject a transfer
     */

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/reject/{id}")
    @Transactional
    public Transfer rejectTransfer(@PathVariable long id, Principal principal) throws TransferNotFoundException,
            UserNotAuthorizedException, AccountNotFoundException {

        return rejectRequest(id, principal);
    }

//    /**
//     * Update a transfer
//     */
//
//    @ResponseStatus(HttpStatus.FOUND)
//    @PutMapping("/{id}")
//    public Transfer updateTransfer(@Valid @RequestBody Transfer transfer, @PathVariable int id,
//                                   Principal principal) throws TransferNotFoundException,
//                                    UserNotAuthorizedException {
//
//        if (id != transfer.getTransferId()) {
//            throw new TransferNotFoundException();
//        } else return transferDao.update(transfer, id, principal.getName());
//    }






    /**
     * *************  BOOLEANS *************
     */


    /**
     *Check if user has sufficient balance for a transfer
     */

    private boolean isBalanceSufficient(float amount, Principal principal) throws AccountNotFoundException {
        long userId = userDao.findIdByUsername(principal.getName());
        Account userAccount = accountDao.findAccountByUserId(userId);

        return userAccount.getBalance() >= amount;
    }

    /**
     *Check if accountId of principal matches a given accountId
     */

    private boolean isUserAccount(float accountId, Principal principal) throws AccountNotFoundException {
        long userId = userDao.findIdByUsername(principal.getName());
        Account userAccount = accountDao.findAccountByUserId(userId);

        return userAccount.getAccountId() == accountId;

    }







    /**
     * ************** HELPER METHODS *************
     */

    /**
     * Verify accountId of current user is same as accountFrom,
     * Verify user accountBalance is >= amount being sent,
     * Subtract amount from accountFrom,
     * Add amount to accountTo,
     * Post Transfer to database
     */


    private Transfer processSend(Transfer transfer, Principal principal)
            throws AccountNotFoundException, InsufficientFundsException,
            TransferNotFoundException, UserNotAuthorizedException, NegativeValueException {


            if (isBalanceSufficient(transfer.getAmount(), principal)) {

                transfer.setTransferStatusId(APPROVED);

                accountDao.subtractBalance(transfer.getAccountFrom(), transfer.getAmount());
                accountDao.addBalance(transfer.getAccountTo(), transfer.getAmount());

                return transferDao.create(transfer, principal.getName());

            } else {
                throw new InsufficientFundsException();
            }

    }


    /**
     * Verify accountId of user is same as accountTo in Transfer
     * Set transferStatus to Pending
     * Add Transfer to DB
     */

    private Transfer processRequest(Transfer transfer, Principal principal) throws AccountNotFoundException, UserNotAuthorizedException, TransferNotFoundException {

        transfer.setTransferStatusId(PENDING);
        return transferDao.create(transfer, principal.getName());

    }

    /**
     * Retrieve Transfer from transferId
     * Verify accountId of user is same as accountFrom in Transfer
     * Verify Transfer status was Pending
     * Verify user balance is sufficient for transfer
     * Set Transfer status to Approved
     * Transfer funds
     * Update Transfer
     */

    private Transfer approveRequest(long transferId, Principal principal) throws TransferNotFoundException,
            AccountNotFoundException, UserNotAuthorizedException, InsufficientFundsException,
            NegativeValueException {

        Transfer transferToApprove = transferDao.get(transferId, principal.getName());

        if (isUserAccount(transferToApprove.getAccountFrom(), principal)) {
            if (transferToApprove.getTransferStatusId() == PENDING) {
                if (isBalanceSufficient(transferToApprove.getAmount(), principal)) {

                    transferToApprove.setTransferStatusId(APPROVED);

                    accountDao.subtractBalance(transferToApprove.getAccountFrom(), transferToApprove.getAmount());
                    accountDao.addBalance(transferToApprove.getAccountTo(), transferToApprove.getAmount());

                    return transferDao.update(transferToApprove, transferId, principal.getName());

                } else {
                    throw new InsufficientFundsException();
                }

            } else {
                throw new TransferNotFoundException();
            }

        } else {
            throw new UserNotAuthorizedException();
        }
    }

    /**
     * Retrieve Transfer from transferId
     * Verify accountId of user is same as accountFrom in Transfer
     * Verify Transfer status was Pending
     * Set Transfer status to Rejected
     * Update Transfer
     */

    private Transfer rejectRequest(long transferId, Principal principal) throws TransferNotFoundException, AccountNotFoundException, UserNotAuthorizedException {

        Transfer transferToReject = transferDao.get(transferId, principal.getName());

        if (isUserAccount(transferToReject.getAccountFrom(), principal)) {

            if (transferToReject.getTransferStatusId() == PENDING) {

                transferToReject.setTransferStatusId(REJECTED);
                return transferDao.update(transferToReject, transferId, principal.getName());

            } else {
                throw new TransferNotFoundException();
            }

        } else {
            throw new UserNotAuthorizedException();
        }
    }

}
