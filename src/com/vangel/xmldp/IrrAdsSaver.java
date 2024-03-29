package com.vangel.xmldp;

import com.vangel.xmldp.dao.IrrAdsDao;
import com.vangel.xmldp.entities.AutoCatalog;
import com.vangel.xmldp.entities.Offer;
import com.vangel.xmldp.xml.IrrAdsParserListenerAdapter;

/**
 * @author VAngeL
 * date: 20.01.13
 */
public class IrrAdsSaver extends IrrAdsParserListenerAdapter {
    private IrrAdsDao dao;

    public IrrAdsSaver(IrrAdsDao dao) {
        this.dao = dao;
    }

    @Override
    public void onOfferParsed(Offer offer) {
        Long id = dao.saveOffer(offer);
        offer.setId(id);
    }

    @Override
    public void onCatalogParsed(AutoCatalog autoCatalog) {
        dao.updateCatalog(autoCatalog);
    }

    @Override
    public void onCatalogFound(AutoCatalog autoCatalog) {
        Long id = dao.saveCatalog(autoCatalog);
        autoCatalog.setId(id);
    }
}
