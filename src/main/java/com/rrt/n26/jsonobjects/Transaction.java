package com.rrt.n26.jsonobjects;

import javax.xml.bind.annotation.XmlRootElement;
import java.time.Instant;

/*
    Transaction class to represent input to the /transactions POST
    Since objects are only stored in memory, there is no need for an `id` parameter
 */
@XmlRootElement
public class Transaction {
    private Double amount;
    private Long timestamp;
    private Instant instant;

    public Transaction() {
    }

    public Transaction(Double amount, Long timestamp, Instant instant) {
        this.amount = amount;
        this.timestamp = timestamp;
        this.instant = instant;
    }

    public Transaction(Double amount, Long timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
        this.instant = Instant.ofEpochMilli(timestamp);
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
        this.instant = Instant.ofEpochMilli(timestamp);
    }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer();
        if (amount != null) {
            str.append("Amount: ").append(amount.toString()).append("\t");
        }
        if (timestamp != null) {
            str.append("Timestamp: ").append(timestamp.toString()).append("\t");
        }
        if (instant != null) {
            str.append("Instant: ").append(instant.toString());
        }
        return str.toString();
    }




}
