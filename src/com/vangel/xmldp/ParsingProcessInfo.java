package com.vangel.xmldp;

import com.vangel.xmldp.entities.AutoCatalog;
import com.vangel.xmldp.entities.Offer;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author VAngeL
 * date: 20.01.13
 */
public class ParsingProcessInfo implements IrrAdsParserListener {
    private Integer fileSize;

    private AtomicLong processedBytes;

    private AtomicInteger offerProcessed;

    private AtomicBoolean wasError;

    private Exception errorDetails;

    private AtomicBoolean done;

    private AtomicBoolean canceled;

    public ParsingProcessInfo() {
        processedBytes = new AtomicLong(0L);
        offerProcessed = new AtomicInteger(0);
        wasError = new AtomicBoolean(false);
        done = new AtomicBoolean(false);
        canceled = new AtomicBoolean(false);
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public long getProcessedBytes() {
        return processedBytes.get();
    }

    public int getOfferProcessed() {
        return offerProcessed.get();
    }

    @Override
    public void onProcessedBytes(long sizeInBytes) {
        processedBytes.set(sizeInBytes);
    }

    @Override
    public void onOfferParsed(Offer offer) {
        offerProcessed.incrementAndGet();
    }

    @Override
    public void onCatalogParsed(AutoCatalog autoCatalog) {
        done.set(true);
    }

    @Override
    public void onCatalogFound(AutoCatalog autoCatalog) {
    }

    @Override
    public void onParsingStop() {
        done.set(true);
        canceled.set(true);
    }

    public boolean isDone() {
        return done.get();
    }

    public boolean isCanceled() {
        return canceled.get();
    }

    public void setError(Exception ex) {
        wasError.set(true);
        errorDetails = ex;
    }

    public boolean wasError() {
        return wasError.get();
    }

    public Exception getErrorDetails() {
        return errorDetails;
    }
}
