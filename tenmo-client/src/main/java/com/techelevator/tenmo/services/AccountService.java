package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class AccountService {

    private static final String API_BASE_URL = "http://localhost:8080/account/";
    private final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public void setAuthToken(String authToken){
        this.authToken = authToken;
    }

    public Account getUserAccount(String authToken) {
        Account account = null;
        try {
            account = restTemplate.exchange(API_BASE_URL, HttpMethod.GET, makeAuthEntity(authToken), Account.class).getBody();
        } catch (ResourceAccessException | RestClientResponseException e) {
            BasicLogger.log(e.getMessage());
        }
        return account;
    }

    public Account findBalance(long id, String authToken) {
        Account balance = null;
        try {
            balance = restTemplate.exchange(API_BASE_URL + id, HttpMethod.GET, makeAuthEntity(authToken), Account.class).getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }

    public Account getAccountByAccountId(long accountId, String authToken) {
        Account account = null;
        try {
            account = restTemplate.exchange(API_BASE_URL, HttpMethod.GET, makeAuthEntity(authToken), Account.class).getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return account;
    }

    /*public Account getAccountByUserId(long userId, String authToken) {
        Account account = null;
        try {
            account = restTemplate.exchange(API_BASE_URL + userId, HttpMethod.GET, makeAuthEntity(authToken), Account.class).getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return account;
    }*/

    public long getAccountIdByUserId(long userId, String authToken) {
        long accountId = 0;
        try {
            ResponseEntity<Long> response =
                    restTemplate.exchange(API_BASE_URL + userId, HttpMethod.GET, makeAuthEntity(authToken), Long.class);
            accountId = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return accountId;
    }

    private HttpEntity<Void> makeAuthEntity(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}
