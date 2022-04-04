package com.techelevator.tenmo;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.*;

import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final TransferService transferService = new TransferService();
    private final UserService userService = new UserService();
    private final AccountService accountService = new AccountService();

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }

    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance(currentUser.getToken());
            } else if (menuSelection == 2) {
                viewTransferHistory(currentUser.getToken());
            } else if (menuSelection == 3) {
                viewPendingRequests(currentUser.getToken());
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

    private void viewCurrentBalance(String authToken) {

        Account account = accountService.getUserAccount(authToken);
        if (account != null) {
            consoleService.printBalance(account, authToken);
        } else {
            System.out.println("No account found!");
        }

//        int accountId = consoleService.promptForInt("Please enter account ID to view details (0 to cancel): ");
//
//        if (accountId != 0) {
//            Account balance = accountService.findBalance(accountId, currentUser.getToken());
//            consoleService.printBalance(balance, authToken);
//        }
		
	}


    private void viewTransferHistory(String authToken) {
        consoleService.printTransferHistoryHeader();

        Transfer[] transfers = transferService.getAll(currentUser.getToken());
        consoleService.printTransferHistory(transfers, currentUser, authToken);

        int transferId = consoleService.promptForInt("Please enter transfer ID to view details (0 to cancel): ");

        if (transferId != 0) {
            Transfer transfer = transferService.getTransfer(transferId, currentUser.getToken());
            consoleService.printTransfer(transfer, authToken);
        }
    }

    private void viewPendingRequests(String authToken) {
        consoleService.printPendingTransfersHeader();

        List<Transfer> transfers = transferService.getAllPending(authToken);
        consoleService.printPendingTransfers(transfers, currentUser, authToken);

        if (transfers != null && transfers.size() > 0) {

            int transferId = consoleService.promptForInt("Please enter transfer ID to approve/reject (0 to cancel): ");
            Transfer transfer = transferService.getTransfer(transferId, currentUser.getToken());

            if (transfer != null &&
                    transfer.getAccountFrom() == accountService.getAccountIdByUserId(currentUser.getUser().getId(), authToken)) {
                approveOrReject(transfer, authToken);

            } else if (transfer != null) {
                System.out.println("You can't approve or reject a transfer you requested!");

            } else {
                System.out.println("Invalid selection!");
            }

        } else {
            System.out.println("No pending transfers found!");
        }
    }

    private void sendBucks() {
        User[] userList = userService.getAll(currentUser.getToken());
        consoleService.printUserList(userList);
        int userId = consoleService.promptForInt("Enter ID of user you are sending to (0 to cancel):");
        float amount = consoleService.promptForFloat("Enter amount:");
        long fromAccount = accountService.getAccountIdByUserId(currentUser.getUser().getId(), currentUser.getToken());
        long toAccount = accountService.getAccountIdByUserId(userId, currentUser.getToken());
        Transfer sentTransfer = transferService.sendTransfer(toAccount, fromAccount, amount, currentUser.getToken());
        System.out.println();
        consoleService.printTransfer(sentTransfer, currentUser.getToken());
    }

    private void requestBucks() {
        User[] userList = userService.getAll(currentUser.getToken());
        consoleService.printUserList(userList);
        int userId = consoleService.promptForInt("Enter ID of user you are requesting from (0 to cancel):");
        float amount = consoleService.promptForFloat("Enter amount:");
        long toAccount = accountService.getAccountIdByUserId(currentUser.getUser().getId(), currentUser.getToken());
        long fromAccount = accountService.getAccountIdByUserId(userId, currentUser.getToken());
        Transfer requestedTransfer = transferService.requestTransfer(toAccount, fromAccount, amount, currentUser.getToken());
        System.out.println();
        consoleService.printTransfer(requestedTransfer, currentUser.getToken());
    }

    private void approveOrReject(Transfer transfer, String authToken) {

        int transferId = transfer.getTransferId();

        int selection = consoleService.approveOrRejectMenu();

        if (selection == 1) {
            transferService.approvePendingTransfer(transfer, authToken);
        } if (selection == 2) {
            transferService.rejectPendingTransfer(transfer, authToken);
        }

        transfer = transferService.getTransfer(transferId, currentUser.getToken());

        if (transfer.getTransferStatusId() == 1 && selection != 0) {
            System.out.println("I'm sorry. You can't do that.");

        } else {
            consoleService.printTransfer(transfer, currentUser.getToken());
        }
    }
}
