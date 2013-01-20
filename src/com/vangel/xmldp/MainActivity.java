package com.vangel.xmldp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends Activity {
    private static final String LOG_TAG = "MainActivity";

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
                    if (startParsing(view)) {
                        etPathToXmlFile.setEnabled(false);
                        showProgressInfo();

                        bParseAction.setTag(STOP_PARSING);
                        bParseAction.setText(R.string.btn_stop_parse);
                    }
                } else {
                    stopCurrentParsing(view);
                    etPathToXmlFile.setEnabled(true);
                    //hideProgressInfo();

                    bParseAction.setTag(START_PARSING);
                    bParseAction.setText(R.string.btn_parse_xml);
                }
            }
        });
    }

    private URL createAndValidateUrl(String path) {
        try {
            URL url = new URL(path);

            if (!"http".equalsIgnoreCase(url.getProtocol()) &&
                    !"ftp".equalsIgnoreCase(url.getProtocol())) {

                showError(getString(R.string.bad_protocol));
                return null;
            }

            boolean fileExists = isFileExists(url);
            if (!fileExists) {
                showError(getString(R.string.file_not_found));
                return null;
            }

            return url;
        } catch (MalformedURLException ex) {
            Log.i(LOG_TAG, "Invalid URL.", ex);
            showError(getString(R.string.invalid_url));
        } catch (IOException ex) {
            Log.i(LOG_TAG, "Can't connect to URL.", ex);
            showError(getString(R.string.failed_connect_to_url));
        }

        return null;
    }

    public boolean startParsing(final View view) {
        resetProgress();

        String path = etPathToXmlFile.getText().toString();

        URL url = createAndValidateUrl(path);
        if (url == null) {
            return false;
        }

        final ParsingProcessInfo processInfo = ParsingService.getInstance().parseIrrAdsXml(url);

        if (processInfo.getFileSize() != null) {
            pbProgressBar.setMax(processInfo.getFileSize());
        } else {
            pbProgressBar.setIndeterminate(processInfo.getFileSize() == null);
        }

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

                if (processInfo.wasError()) {
                    Log.e(LOG_TAG, "XML parsing process failed.", processInfo.getErrorDetails());
                    showError(getString(R.string.bad_xml));
                } else {
                    showResultList();
                }
            }
        }).start();

        return true;
    }

    private void showProgressInfo() {
        pbProgressBar.setVisibility(View.VISIBLE);
        tParsedOffers.setVisibility(View.VISIBLE);
        tProcessedBytes.setVisibility(View.VISIBLE);
    }

    private void hideProgressInfo() {
        pbProgressBar.setVisibility(View.INVISIBLE);
        tParsedOffers.setVisibility(View.INVISIBLE);
        tProcessedBytes.setVisibility(View.INVISIBLE);
    }

    private boolean isFileExists(URL url) throws IOException {
        URLConnection connection = url.openConnection();

        if ("http".equalsIgnoreCase(url.getProtocol())) {
            return ((HttpURLConnection)connection).getResponseCode() == HttpURLConnection.HTTP_OK;
        } else { //try to read 1 byte
            InputStream is = null;
            try {
                is = connection.getInputStream();
                is.read();
            } catch (IOException ex) {
                return false;
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        return true;
    }

    public void stopCurrentParsing(View view) {
        ParsingService.getInstance().stopCurrentParsing();
    }

    void showError(String message) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

        dlgAlert.setMessage(message);
        dlgAlert.setTitle(getString(R.string.error_dlg_title));
        dlgAlert.setPositiveButton(getString(R.string.btn_ok),  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
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
