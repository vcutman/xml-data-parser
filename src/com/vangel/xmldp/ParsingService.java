package com.vangel.xmldp;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.vangel.xmldp.dao.IrrAdsDao;
import com.vangel.xmldp.dao.IrrAdsDaoImpl;
import com.vangel.xmldp.entities.AutoCatalog;
import com.vangel.xmldp.xml.IrrAdsXmlParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author VAngeL
 * date: 20.01.13
 */
public class ParsingService {
    private static final ParsingService instance = new ParsingService();

    private IrrAdsDao irrAdsDao;

    private IrrAdsXmlParser currentParser;

    public ParsingService() {
    }

    public static ParsingService getInstance() {
        return instance;
    }

    public void init(Context context) {
        irrAdsDao = new IrrAdsDaoImpl(context);
    }

    public Integer getFileSize(final URL url) throws IOException {
        int length = -1;
        if ("http".equalsIgnoreCase(url.getProtocol())) {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                length = connection.getContentLength();
            }
        } else {
            URLConnection connection = url.openConnection();
            length = connection.getContentLength();
        }

        return length == -1 ? null : length;
    }

    public ParsingProcessInfo parseIrrAdsXml(final URL xmlUrl) {
        final ParsingProcessInfo processInfo = new ParsingProcessInfo();

        try {
            processInfo.setFileSize(getFileSize(xmlUrl));
        } catch (IOException ex) {
            Log.i("PS", "Can't detect file size", ex);
        }

        final IrrAdsXmlParser xmlParser = new IrrAdsXmlParser();
        xmlParser.addListener(new IrrAdsSaver(irrAdsDao));
        xmlParser.addListener(processInfo);

        currentParser = xmlParser;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    xmlParser.parse(xmlUrl.openStream());
                } catch (Exception ex) {
                    processInfo.setError(ex);
                }
            }
        });

        t.start();
        return processInfo;
    }

    public void stopCurrentParsing() {
        if (currentParser != null) {
            currentParser.stopParsing();
        }
    }

    public AutoCatalog getLastCatalog() {
        return irrAdsDao.findLastCatalog();
    }

    public Cursor getCatalogOffers(Long catalogId) {
        return irrAdsDao.getCatalogOffers(catalogId);
    }
}
