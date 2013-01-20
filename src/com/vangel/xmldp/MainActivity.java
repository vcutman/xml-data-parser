package com.vangel.xmldp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;

public class MainActivity extends Activity {
    private static final Integer START_PARSING = 1;
    private static final Integer STOP_PARSING = 2;

    private Button bParseAction;

    private ProgressBar pbProgressBar;

    private EditText etPathToXmlFile;

    private TextView tProcessedBytes;

    private TextView tParsedOffers;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        bParseAction = (Button) findViewById(R.id.parseAction);
        pbProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        etPathToXmlFile = (EditText) findViewById(R.id.pathToXmlFile);
        tParsedOffers = (TextView) findViewById(R.id.parsedOffers);
        tProcessedBytes = (TextView) findViewById(R.id.processedBytes);

        initParseActionButton();

        ParsingService.getInstance().init(this);
    }

    private void initParseActionButton() {
        bParseAction.setTag(START_PARSING);
        bParseAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bParseAction.getTag().equals(START_PARSING)) {
                    parseData(view);
                    bParseAction.setTag(STOP_PARSING);
                    bParseAction.setText(R.string.btn_stop_parse);
                } else {
                    stopCurrentParsing(view);
                    bParseAction.setTag(START_PARSING);
                    bParseAction.setText(R.string.btn_parse_xml);
                }
            }
        });
    }

    public void parseData(final View view) {
        resetProgress();

        String path = etPathToXmlFile.getText().toString();

        try {
            URL url = new URL(path);
            final ParsingProcessInfo processInfo = ParsingService.getInstance().parseIrrAdsXml(url);

            if (processInfo.getFileSize() != null) {
                pbProgressBar.setMax(processInfo.getFileSize());
            } else {
                pbProgressBar.setIndeterminate(processInfo.getFileSize() == null);
            }

            etPathToXmlFile.setEnabled(false);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!processInfo.wasError() && !processInfo.isDone()) {
                        updateProgress(processInfo);
                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException ex) { }
                    }

                    updateProgress(processInfo);
                    showResultList();
                }
            }).start();
        } catch (Exception ex) {
            view.setBackgroundColor(888);
        }
    }

    public void stopCurrentParsing(View view) {
        etPathToXmlFile.setEnabled(true);
    }

    private void showResultList() {
        Intent listIntent = new Intent(this, AutoCatalogActivity.class);
        startActivity(listIntent);
    }

    private void updateProgress(final ParsingProcessInfo processInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (processInfo.getFileSize() != null) {
                    pbProgressBar.setProgress((int) processInfo.getProcessedBytes());
                }

                tProcessedBytes.setText(prepareProcessedBytesString(processInfo));
                tParsedOffers.setText(prepareParsedOffersString(processInfo));
            }
        });
    }

    private String prepareProcessedBytesString(ParsingProcessInfo processInfo) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(getString(R.string.bytes_processed))
                .append(": ")
                .append(processInfo.getProcessedBytes());

        if (processInfo.getFileSize() != null) {
            buffer.append(" / ").append(processInfo.getFileSize());
        }

        return buffer.toString();
    }

    private String prepareParsedOffersString(ParsingProcessInfo processInfo) {
        return getString(R.string.parsed_offers) + ": " + processInfo.getOfferProcessed();
    }

    private void resetProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pbProgressBar.setProgress(0);
                tProcessedBytes.setText("-");
                tParsedOffers.setText("-");
            }
        });
    }
}
