package com.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.*;

/**
 * @author
 * @date 2023/8/9 17:33
 * @description
 */
@Slf4j
public class SimpleDelayQueueUtils {
    private final static DelayQueue<SimpleDelayed> NONCE_DELAYED_DELAY_QUEUE = new DelayQueue<>();

    static{
        CompletableFuture.runAsync(() -> {
            for (;;) {
                try {
                    log.info("元素以过期{}",NONCE_DELAYED_DELAY_QUEUE.take().getData());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            //     join() 是为了获取返回值  并且会阻塞调用的当前线程
        }, Executors.newSingleThreadExecutor());
    }

    public static void add(SimpleDelayed... delayeds) {
        NONCE_DELAYED_DELAY_QUEUE.addAll(Arrays.asList(delayeds));
    }


    public static void main(String[] args) {
        SimpleDelayed<String> nonceDelayed = new SimpleDelayQueueUtils.SimpleDelayed<>("111",5001);
        SimpleDelayed<String> nonceDelayed1 = new SimpleDelayQueueUtils.SimpleDelayed<>("222", 10);
        SimpleDelayQueueUtils.add(nonceDelayed,nonceDelayed1);
    }
    static class SimpleDelayed<T> implements Delayed {

        @JsonIgnore
        private final Date createDate;

        private long delayCount;

        private T data;

        public SimpleDelayed(T data, long delayCount) {
            this.data = data;
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

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }
}
