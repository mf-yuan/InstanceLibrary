package com.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.ToString;
import org.apache.poi.ss.formula.functions.T;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
@ToString(exclude = {"createDate"})
public class NonceDelayed implements Delayed {

    @JsonIgnore
    private final Date createDate;

    private long delayCount;

    private String nonce;

    public NonceDelayed(String nonce, long delayCount) {
        this.nonce = nonce;
        this.delayCount = delayCount;
        this.createDate = new Date();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = this.createDate.getTime() + this.delayCount - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed delayed) {
        return Long.compare(this.getDelay(TimeUnit.MILLISECONDS),delayed.getDelay(TimeUnit.MILLISECONDS));
    }

    public long getDelayCount() {
        return delayCount;
    }

    public void setDelayCount(long delayCount) {
        this.delayCount = delayCount;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}