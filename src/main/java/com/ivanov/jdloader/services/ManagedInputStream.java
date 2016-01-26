package com.ivanov.jdloader.services;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class ManagedInputStream extends BufferedInputStream {

    private int lastCount;

    public ManagedInputStream(final InputStream in) {
        super(in);
    }

    public byte[] read(final int limit) throws IOException {
        if (lastCount == -1) {
            return null;
        }

        int sum = 0;
        byte[] buffer = new byte[limit];
        long start = new Date().getTime();

        while (sum != limit) {
            lastCount = this.read(buffer, sum, limit - sum);
            if (lastCount == -1) {
                break;
            }
            sum += lastCount;
        }

        if (sum == 0) {
            return null;
        }

        pause(start, limit, sum);

        if (lastCount == -1 && sum > 0) {
            byte[] lastBuffer = new byte[sum];
            System.arraycopy(buffer, 0, lastBuffer, 0, sum);
            return lastBuffer;
        }
        return buffer;
    }

    private void pause(final long start, final double limit, final double sum) {
        final int interval = 1000;
        final long time = new Date().getTime() - start;

        if (time < interval) {
            int percentsOfBuffer = (int) Math.round(sum / limit * 100);
            long percentOfLimitTime = (interval - time) / 100;
            try {
                Thread.sleep(percentsOfBuffer * percentOfLimitTime);
            } catch (InterruptedException e) {
                System.out.println("Прерывание потока");
            }
        }
    }

}
