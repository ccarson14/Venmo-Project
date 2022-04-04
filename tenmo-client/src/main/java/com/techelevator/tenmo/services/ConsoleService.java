package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.*;
import io.cucumber.java.bs.A;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Scanner;

public class ConsoleService {

    DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");

    private final Scanner scanner = new Scanner(System.in);
    private final UserService userService = new UserService();
    private final AccountService accountService = new AccountService();

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printBalance(Account balance, String authToken){
        System.out.println("--------------------------------------------\n" +
                "Current Balance\n" +
                "--------------------------------------------");
        System.out.println("Balance: $" + decimalFormat.format(balance.getBalance()));
    }
    public void printTransferHistoryHeader() {
        System.out.println();
        System.out.println("-------------------------------------------\n" +
                "Transfers\n" +
                "ID          From/To                 Amount\n" +
                "-------------------------------------------");
    }

    public void printPendingTransfersHeader() {
        System.out.println();
        System.out.println("-------------------------------------------\n" +
                "Pending Transfers\n" +
                "ID          From/To                     Amount\n" +
                "-------------------------------------------");
    }


    public void printTransferHistory(Transfer[] transfers, AuthenticatedUser user, String authToken) {
        if (transfers != null && transfers.length > 0) {
            for (Transfer transfer : transfers) {
                System.out.println(transferToStringAsLine(transfer, user, authToken));
            }
            System.out.println("---------");
        } else {
            System.out.println("No transfers found");
        }

    }

    public void printPendingTransfers(List<Transfer> transfers, AuthenticatedUser user, String authToken) {
        if (transfers != null && transfers.size() > 0) {
            for (Transfer transfer : transfers) {
                if (transfer.getTransferStatusId() == 1) {
                    System.out.println(transferToStringAsLine(transfer, user, authToken));
                }
            }
            System.out.println("---------");
        }
    }

    public String transferToStringAsLine(Transfer transfer, AuthenticatedUser user, String authToken) {
        String line = transfer.getTransferId() + "          ";

        if (accountService.getAccountIdByUserId(user.getUser().getId(), authToken) == transfer.getAccountTo()) {
            line += "From: " + userService.getUsernameById(transfer.getAccountFrom(), authToken);
        } else {
            line += "To:    " + userService.getUsernameById(transfer.getAccountTo(), authToken);
        }

        line += "        $ " + decimalFormat.format(transfer.getAmount());
        return line;
    }

    public void printTransfer(Transfer transfer, String authToken) {
        if (transfer != null) {
            System.out.println("--------------------------------------------\n" +
                    "Transfer Details\n" +
                    "--------------------------------------------");
            System.out.println("ID: " + transfer.getTransferId());
            System.out.println("From: " + userService.getUsernameById(transfer.getAccountFrom(), authToken));
            System.out.println("To: " + userService.getUsernameById(transfer.getAccountTo(), authToken));
            System.out.println("Type: " + transfer.typeToString());
            System.out.println("Status: " + transfer.statusToString());
            System.out.println("Amount: $" + decimalFormat.format(transfer.getAmount()));
            System.out.println();
        }
    }

    public void printApproveOrRejectMenu() {
        System.out.println("1: Approve\n" +
                "2: Reject\n" +
                "0: Don't approve or reject\n" +
                "---------");
    }

    public void printUserList(User[] users) {
        System.out.println("-------------------------------------------\n" +
                "Users\n" +
                "ID          Name\n" +
                "-------------------------------------------");

        if (users.length > 0) {
            for (User user : users) {
                System.out.println(user.getId() + "      " + user.getUsername());
            }
        } else {
            System.out.println("No users found.");
        }

        System.out.println("--------------");
        System.out.println();
    }


    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public float promptForFloat(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Float.parseFloat(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }

    public int approveOrRejectMenu() {
        int selection;
        while (true) {
            System.out.println();
            printApproveOrRejectMenu();
            System.out.print("Please input selection: ");
            try {
                selection = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e){
                System.out.println("Invalid entry!");
                System.out.println();
                continue;
            }
            if (selection >= 0 && selection <= 2) {
                break;
            } else {
                System.out.println("Invalid entry!");
                System.out.println();
            }
        }
        return selection;
    }

}
