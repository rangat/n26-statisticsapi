package com.rrt.n26.objects;

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

    public Transaction() {
    }

    public Transaction(Double amount, Long timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
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
    }

    public Instant toInstant() {
        return Instant.ofEpochMilli(timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        return timestamp != null ? timestamp.equals(that.timestamp) : that.timestamp == null;
    }

    @Override
    public int hashCode() {
        int result = amount != null ? amount.hashCode() : 0;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
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
        return str.toString();
    }




}
