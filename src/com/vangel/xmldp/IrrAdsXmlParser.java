package com.vangel.xmldp;

import android.util.Xml;
import com.vangel.xmldp.entities.AutoCatalog;
import com.vangel.xmldp.entities.Offer;
import com.vangel.xmldp.utils.StringUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @author VAngeL
 * date: 20.01.13
 */
public class IrrAdsXmlParser {
    private static final Logger logger = Logger.getLogger(IrrAdsXmlParser.class.getName());

    private static final String TAG_AUTO_CATALOG = "auto-catalog";
    private static final String TAG_CREATION_DATE = "creation-date";
    private static final String TAG_HOST = "host";
//    private static final String TAG_OFFERS = "offers";
    private static final String TAG_OFFER = "offer";
    private static final String ATTR_TYPE = "type";
//    private static final String TAG_URL = "url";
    private static final String TAG_DATE = "date";
//    private static final String TAG_UPDATE_DATE = "update-date";
//    private static final String TAG_VALID_THRU_DATE = "valid-thru-date";
//    private static final String TAG_IMAGE = "image";
//    private static final String TAG_SELLER_CITY = "seller-city";
    private static final String TAG_MARK = "mark";
//    private static final String TAG_MODEL = "model";
//    private static final String TAG_YEAR = "year";
//    private static final String TAG_RUN_METRIC = "run-metric";
//    private static final String TAG_RUN = "run";
//    private static final String TAG_ADDITIONAL_INFO = "additional-info";
//    private static final String TAG_STATE = "state";
//    private static final String TAG_BODY_TYPE = "body-type";
//    private static final String TAG_ENGINE_TYPE = "engine-type";
//    private static final String TAG_GEAR_TYPE = "gear-type";
//    private static final String TAG_DISPLACEMENT = "displacement";
//    private static final String TAG_TRANSMISSION = "transmission";
//    private static final String TAG_STEERING_WHEEL = "steering-wheel";
//    private static final String TAG_STOCK = "stock";
//    private static final String TAG_PRICE = "price";
//    private static final String TAG_CURRENCY_TYPE = "currency-type";
//    private static final String TAG_HORSE_POWER = "horse-power";
//    private static final String TAG_SELLER_PHONE = "seller-phone";
//    private static final String TAG_SELLER = "seller";

    private final ArrayList<IrrAdsParserListener> listeners =
            new ArrayList<IrrAdsParserListener>();

    public IrrAdsXmlParser() {
    }

    private static Integer parseInteger(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }

        return Integer.valueOf(text);
    }

    private static Double parseDouble(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }

        return Double.valueOf(text);
    }

    public synchronized void addListener(IrrAdsParserListener listener) {
        listeners.add(listener);
    }

    public void parse(InputStream inputStream) throws IOException, XmlPullParserException {
        XmlPullParser parser = Xml.newPullParser();

        InputStreamSizeWrapper wrappedStream = new InputStreamSizeWrapper(inputStream);
        parser.setInput(new InputStreamReader(wrappedStream, "windows-1251"));

        int eventType = parser.getEventType();

        boolean done = false;
        String tagName;

        AutoCatalog autoCatalog = null;
        Offer offer = null;

        while (!done && eventType != XmlPullParser.END_DOCUMENT) {
            fireProcessedBytes(wrappedStream.getTotalBytesRead());

            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    tagName = parser.getName();

                    if (!tagName.equalsIgnoreCase(TAG_AUTO_CATALOG) && autoCatalog == null) {
                        throw new XmlPullParserException("Malformed xml!!");
                    }

                    if (tagName.equalsIgnoreCase(TAG_AUTO_CATALOG)) {
                        autoCatalog = new AutoCatalog();
                        fireCatalogFound(autoCatalog);
                    }  else if (tagName.equalsIgnoreCase(TAG_CREATION_DATE)) {
                        autoCatalog.setCreationDate(parser.nextText());
                    } else if (tagName.equalsIgnoreCase(TAG_HOST)) {
                        autoCatalog.setHost(parser.nextText());
                    } else if (tagName.equalsIgnoreCase(TAG_OFFER)) {
                        offer = new Offer();
                        offer.setType(parser.getAttributeValue(null, ATTR_TYPE));
                        offer.setCatalogId(autoCatalog.getId());
                    } else if (tagName.equalsIgnoreCase(TAG_DATE)) {
                        offer.setDate(parser.nextText());
                    } else if (tagName.equalsIgnoreCase(TAG_MARK)) {
                        offer.setMark(parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    tagName = parser.getName();
                    if (tagName.equalsIgnoreCase(TAG_OFFER)) {
                        fireOfferParsed(offer);

                        offer = null;
                    } else if (tagName.equalsIgnoreCase(TAG_AUTO_CATALOG)) {
                        done = true;
                        fireCatalogParsed(autoCatalog);

                        autoCatalog = null;
                    }
                    break;
            }

            eventType = parser.next();
        }
    }

    private void fireProcessedBytes(long bytesCnt) {
        for (IrrAdsParserListener listener: listeners) {
            listener.onProcessedBytes(bytesCnt);
        }
    }

    private void fireOfferParsed(Offer offer) {
        for (IrrAdsParserListener listener: listeners) {
            listener.onOfferParsed(offer);
        }
    }

    private void fireCatalogFound(AutoCatalog catalog) {
        for (IrrAdsParserListener listener: listeners) {
            listener.onCatalogFound(catalog);
        }
    }

    private void fireCatalogParsed(AutoCatalog catalog) {
        for (IrrAdsParserListener listener: listeners) {
            listener.onCatalogParsed(catalog);
        }
    }
}
