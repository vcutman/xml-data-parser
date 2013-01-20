package com.vangel.xmldp;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.vangel.xmldp.entities.AutoCatalog;

/**
 * @author VAngeL
 * date: 20.01.13
 */
public class AutoCatalogActivity extends Activity {
    private ListView lvOffers;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.auto_catalog);

        lvOffers = (ListView) findViewById(R.id.lvOffers);

        AutoCatalog catalog = ParsingService.getInstance().getLastCatalog();

        if (catalog != null) {
            Cursor cursor = ParsingService.getInstance().getCatalogOffers(catalog.getId());
            startManagingCursor(cursor);

            // формируем столбцы сопоставления
            String[] from = new String[] {"date", "mark"};
            int[] to = new int[] { R.id.tOfferDate, R.id.tOfferMark};

            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.offer_item, cursor, from, to);
            lvOffers.setAdapter(adapter);
        }
    }
}