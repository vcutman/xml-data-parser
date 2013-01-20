package com.vangel.xmldp.dao;

import android.database.Cursor;
import com.vangel.xmldp.entities.AutoCatalog;
import com.vangel.xmldp.entities.Offer;

/**
 * @author VAngeL
 * date: 20.01.13
 */
public interface IrrAdsDao {
    Long saveOffer(Offer offer);

    Long saveCatalog(AutoCatalog catalog);

    void updateCatalog(AutoCatalog catalog);

    AutoCatalog findLastCatalog();

    Cursor getCatalogOffers(Long catalogId);
}
