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

    //used internally for convenience regarding date-time functions
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;
        return instant != null ? instant.equals(that.instant) : that.instant == null;
    }

    @Override
    public int hashCode() {
        int result = amount != null ? amount.hashCode() : 0;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (instant != null ? instant.hashCode() : 0);
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
        if (instant != null) {
            str.append("Instant: ").append(instant.toString());
        }
        return str.toString();
    }




}
