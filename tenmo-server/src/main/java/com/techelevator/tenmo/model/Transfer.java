package com.techelevator.tenmo.model;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class Transfer {
    private long transferId;

    @Min(value = 1, message = "transferTypeId must be at least 1.")
    @Max(value = 2, message = "transferTypeId cannot be greater than 2.")
    private long transferTypeId;

    @Min(value = 1, message = "transferStatusId must be at least 1.")
    @Max(value = 3, message = "transferTypeId cannot be greater than 3.")
    private long transferStatusId;

    @Positive(message = "accountFrom must be positive.")
    private long accountFrom;

    @Positive(message = "accountTo must be positive.")
    private long accountTo;

    @Positive(message = "Amount sent must be positive.")
    private float amount;

    @AssertFalse(message = "accountTo cannot be the same as accountFrom.")
    private boolean isAccountToTheSameAsAccountFrom() {
        return (accountTo == accountFrom);
    }

    public Transfer() {};

    public Transfer(long transferId, long transferTypeId, long transferStatusId, long accountFrom, long accountTo, float amount) {
        this.transferId = transferId;
        this.transferTypeId = transferTypeId;
        this.transferStatusId = transferStatusId;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }

    public long getTransferId() {
        return transferId;
    }

    public void setTransferId(long transferId) {
        this.transferId = transferId;
    }

    public long getTransferTypeId() {
        return transferTypeId;
    }

    public void setTransferTypeId(long transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public long getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(long transferStatusId) {
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


}
