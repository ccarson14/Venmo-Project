package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TransferService {

    private static final String API_BASE_URL = "http://localhost:8080/transfer/";
    private final RestTemplate restTemplate = new RestTemplate();

    public Transfer[] getAll(String authToken) {
        Transfer[] transfers = null;
        try {
            ResponseEntity<Transfer[]> response =
                    restTemplate.exchange(API_BASE_URL, HttpMethod.GET, makeAuthEntity(authToken), Transfer[].class);
            transfers = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfers;
    }

    public Transfer getTransfer(long id, String authToken) {
        Transfer transfer = null;
        try {
            ResponseEntity<Transfer> response =
                    restTemplate.exchange(API_BASE_URL + id, HttpMethod.GET, makeAuthEntity(authToken), Transfer.class);
            transfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfer;
    }

    public List<Transfer> getAllPending(String authToken) {
        List<Transfer> pendingTransfers = new ArrayList<>();
        Transfer[] allTransfers = getAll(authToken);
        for (Transfer transfer : allTransfers) {
            if(transfer.getTransferStatusId() == 1) {
                pendingTransfers.add(transfer);
            }
        }

        return pendingTransfers;
    }

    public Transfer createTransfer(Transfer transfer, String authToken) {
        Transfer returnedTransfer = null;
        HttpEntity<Transfer> entity = makeAuthTransferEntity(transfer, authToken);

        try {
            ResponseEntity<Transfer> response = restTemplate.exchange(API_BASE_URL, HttpMethod.POST, entity, Transfer.class);
            returnedTransfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return returnedTransfer;
    }

    public Transfer sendTransfer(long toId, long fromId, float amount, String authToken) {

        Transfer transfer = new Transfer();
        transfer.setTransferTypeId(2);
        transfer.setTransferStatusId(1);
        transfer.setAccountFrom(fromId);
        transfer.setAccountTo(toId);
        transfer.setAmount(amount);
        Transfer sentTransfer = createTransfer(transfer, authToken);
        if (sentTransfer == null) {
            System.out.println("I'm sorry. You can't do that.");
        }
        return sentTransfer;
    }

    public Transfer requestTransfer(long toId, long fromId, float amount, String authToken) {

        Transfer transfer = new Transfer();
        transfer.setTransferTypeId(1);
        transfer.setTransferStatusId(1);
        transfer.setAccountFrom(fromId);
        transfer.setAccountTo(toId);
        transfer.setAmount(amount);
        Transfer requestedTransfer = createTransfer(transfer, authToken);
        return requestedTransfer;
    }

    public boolean approvePendingTransfer(Transfer transfer, String authToken) {
        boolean success = false;

        try {
            restTemplate.exchange(API_BASE_URL +"approve/" + transfer.getTransferId(), HttpMethod.POST, makeAuthTransferEntity(transfer, authToken), Transfer.class);
            success = true;

        } catch (ResourceAccessException | RestClientResponseException e) {
            BasicLogger.log(e.getMessage());
        }

        return success;
    }

    public boolean rejectPendingTransfer(Transfer transfer, String authToken) {
        boolean success = false;

        try {
            restTemplate.exchange(API_BASE_URL + "reject/" + transfer.getTransferId(), HttpMethod.POST, makeAuthTransferEntity(transfer, authToken), Transfer.class);
            success = true;
        } catch (ResourceAccessException | RestClientResponseException e) {
            BasicLogger.log(e.getMessage());
        }
        return success;
    }

    private HttpEntity<Void> makeAuthEntity(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }

    private HttpEntity<Transfer> makeAuthTransferEntity(Transfer transfer, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transfer, headers);
    }
}
