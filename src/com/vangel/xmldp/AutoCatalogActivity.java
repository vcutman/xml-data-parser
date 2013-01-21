package com.vangel.xmldp;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.vangel.xmldp.entities.AutoCatalog;
import com.vangel.xmldp.utils.StringUtils;

/**
 * @author VAngeL
 * date: 20.01.13
 */
public class AutoCatalogActivity extends Activity {
    private ListView lvOffers;

    private TextView tCatalogInfo;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.auto_catalog);

        lvOffers = (ListView) findViewById(R.id.lvOffers);
        tCatalogInfo = (TextView) findViewById(R.id.tCatalogInfo);

        AutoCatalog catalog = ParsingService.getInstance().getLastCatalog();

        if (catalog != null) {
            tCatalogInfo.setText(prepareCatalogInfoString(catalog));

            Cursor cursor = ParsingService.getInstance().getCatalogOffers(catalog.getId());
            startManagingCursor(cursor);

            // формируем столбцы сопоставления
            String[] from = new String[] {"date", "mark"};
            int[] to = new int[] { R.id.tOfferDate, R.id.tOfferMark};

            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.offer_item, cursor, from, to);
            lvOffers.setAdapter(adapter);
        }
    }

    private String prepareCatalogInfoString(AutoCatalog catalog) {
        StringBuilder buffer = new StringBuilder();

        buffer.append(getString(R.string.created))
                .append(": ")
                .append(StringUtils.isNotEmpty(catalog.getCreationDate()) ? catalog.getCreationDate() : getString(R.string.empty))
                .append(";   ")
                .append(getString(R.string.host))
                .append(": ")
                .append(StringUtils.isNotEmpty(catalog.getHost()) ? catalog.getHost() : getString(R.string.empty));

        return buffer.toString();
    }
}