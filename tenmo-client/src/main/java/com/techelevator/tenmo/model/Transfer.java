package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {

    private int transferId;
    private int transferTypeId;
    private int transferStatusId;
    private long accountFrom;
    private long accountTo;
    private float amount;
    private boolean isAccountToTheSameAsAccountFrom() {
        return (accountTo == accountFrom);
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getTransferTypeId() {
        return transferTypeId;
    }

    public void setTransferTypeId(int transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public int getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(int transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    public long getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(long accountFrom) {
        this.accountFrom = accountFrom;
    }

    public long getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(long accountTo) {
        this.accountTo = accountTo;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String typeToString() {
        String typeString = "";
        if (this.transferTypeId == 1) {
            typeString = "Request";
        } else if (this.transferTypeId == 2) {
            typeString = "Send";
        }
        return typeString;
    }

    public String statusToString() {
        String statusString = "";
        if (this.getTransferStatusId() == 1) {
            statusString = "Pending";
        } else if (this.getTransferStatusId() == 2) {
            statusString = "Approved";
        } else if (this.getTransferStatusId() == 3) {
            statusString = "Rejected";
        }
        return statusString;
    }
}
