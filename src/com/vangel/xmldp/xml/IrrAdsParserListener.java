package com.vangel.xmldp.xml;

import com.vangel.xmldp.entities.AutoCatalog;
import com.vangel.xmldp.entities.Offer;

/**
 * @author VAngeL
 * date: 20.01.13
 */
public interface IrrAdsParserListener {
    void onProcessedBytes(long sizeInBytes);

    void onOfferParsed(Offer offer);

    void onCatalogParsed(AutoCatalog autoCatalog);

    void onCatalogFound(AutoCatalog autoCatalog);

    void onParsingStop();

}
