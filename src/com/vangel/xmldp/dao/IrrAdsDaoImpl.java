package com.vangel.xmldp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.vangel.xmldp.entities.AutoCatalog;
import com.vangel.xmldp.entities.Offer;

/**
 * @author VAngeL
 * date: 20.01.13
 */
public class IrrAdsDaoImpl implements IrrAdsDao {
    public static final String CATALOG_TABLE = "auto_catalog";
    public static final String OFFERS_TABLE = "offers";

    private final IrrAdsDbCreator dbCreator;

    public IrrAdsDaoImpl(Context context) {
        dbCreator = new IrrAdsDbCreator(context);
    }

    @Override
    public Long saveOffer(Offer offer) {
        ContentValues values = new ContentValues();
        values.put("date", offer.getDate());
        values.put("mark", offer.getMark());
        values.put("type", offer.getType());
        values.put("auto_catalog_id", offer.getCatalogId());

        long id = dbCreator.getWritableDatabase().insert(OFFERS_TABLE, null, values);
        return id == -1 ? null : id;
    }

    @Override
    public Long saveCatalog(AutoCatalog catalog) {
        ContentValues values = new ContentValues();
        values.put("creation_date", catalog.getCreationDate());
        values.put("host", catalog.getHost());

        long id = dbCreator.getWritableDatabase().insert(CATALOG_TABLE, null, values);
        return id == -1 ? null : id;
    }

    @Override
    public void updateCatalog(AutoCatalog catalog) {
        ContentValues values = new ContentValues();
        values.put("creation_date", catalog.getCreationDate());
        values.put("host", catalog.getHost());

        dbCreator.getWritableDatabase().update(CATALOG_TABLE, values, "id = ?", new String[] {""+catalog.getId()});
    }

    @Override
    public AutoCatalog findLastCatalog() {
        Cursor cursor = dbCreator.getReadableDatabase().query(CATALOG_TABLE,
                new String[]{"id", "creation_date", "host"},
                "id = (select max(t.id) from " + CATALOG_TABLE + " as t)",
                null,
                null,
                null,
                null);

        if (cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToNext();

        AutoCatalog catalog = new AutoCatalog();
        catalog.setId(cursor.getLong(0));
        catalog.setCreationDate(cursor.getString(1));
        catalog.setHost(cursor.getString(2));

        cursor.close();

        return catalog;
    }

    @Override
    public Cursor getCatalogOffers(Long catalogId) {
        return dbCreator.getReadableDatabase().rawQuery("select id as _id, date, mark " +
                " from " + OFFERS_TABLE +
                " where auto_catalog_id = ?;",
                new String[] {""+catalogId});
    }
}
