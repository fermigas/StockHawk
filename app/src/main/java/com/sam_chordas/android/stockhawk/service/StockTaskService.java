package com.sam_chordas.android.stockhawk.service;

import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.google.gson.Gson;
import com.sam_chordas.android.stockhawk.data.HistoricalQuoteColumns;
import com.sam_chordas.android.stockhawk.data.HistoricalQuoteResults;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.data.QuoteResult;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by sam_chordas on 9/30/15.
 * The GCMTask service is primarily for periodic tasks. However, OnRunTask can be called directly
 * and is used for the initialization and adding task as well.
 */
public class StockTaskService extends GcmTaskService{
  private String LOG_TAG = StockTaskService.class.getSimpleName();

  private OkHttpClient client = new OkHttpClient();
  private Context mContext;
  private StringBuilder mStoredSymbols = new StringBuilder();
  private boolean isUpdate;

  public StockTaskService(){}

  public StockTaskService(Context context){
    mContext = context;
  }
  String fetchData(String url) throws IOException{
    Request request = new Request.Builder()
        .url(url)
        .build();

//    client.setConnectTimeout(90, TimeUnit.SECONDS); // connect timeout
//    client.setReadTimeout(90, TimeUnit.SECONDS);    // socket timeout
    Response response = client.newCall(request).execute();
    return response.body().string();
  }

  @Override
  public int onRunTask(TaskParams params){
    if (mContext == null)
      mContext = this;

    StringBuilder urlStringBuilder = buildYahooStockDataUrl(params, new StringBuilder());

    String urlString;
    String getResponse;
    int result = GcmNetworkManager.RESULT_FAILURE;


    if (urlStringBuilder != null){
      urlString = urlStringBuilder.toString();
      try{
        getResponse = fetchData(urlString);
        result = GcmNetworkManager.RESULT_SUCCESS;

        if (params.getTag().equals("historical")) {
          processHistoricalQuotes(getResponse);
        }
        else {
          if(params.getTag().equals("add") && !isSymbolValid(getResponse))
            return GcmNetworkManager.RESULT_FAILURE;
          processQuotes(getResponse);
        }

      } catch (IOException e){
        e.printStackTrace();
      }
    }

    return result;
  }

  @NonNull
  private Boolean isSymbolValid(String getResponse) {

    QuoteResult quoteResult;

    Gson gson = new Gson();
    quoteResult = gson.fromJson(getResponse, QuoteResult.class);

    QuoteResult.QueryEntity.ResultsEntity.QuoteEntity qr =
            quoteResult.getQuery().getResults().getQuote();

    if( qr.getBid() == null)
      return false;
    else
      return true;
  }


  private void processHistoricalQuotes(String getResponse) {

    HistoricalQuoteResults historicalQuoteResults;

    Gson gson = new Gson();
    historicalQuoteResults = gson.fromJson(getResponse, HistoricalQuoteResults.class);

    ContentValues[] bulkInsertContentValues = createBulkInsertContentValues(historicalQuoteResults);

    int insertCount = mContext.getContentResolver()
            .bulkInsert(QuoteProvider.HistoricalQuotes.CONTENT_URI, bulkInsertContentValues);


    return;
  }

  private ContentValues[] createBulkInsertContentValues( HistoricalQuoteResults hqr) {

    int quoteCount = hqr.getQuery().getCount();
    ContentValues[] quoteValues = new ContentValues[quoteCount];

    for (int i = 0; i < quoteCount ; i++) {
      ContentValues qv = new ContentValues();

      HistoricalQuoteResults.QueryEntity.ResultsEntity.QuoteEntity qe =
              hqr.getQuery().getResults().getQuote().get(i);

      qv.put(HistoricalQuoteColumns.SYMBOL, qe.getSymbol());
      qv.put(HistoricalQuoteColumns.CLOSE_DATE, qe.getDate());
      qv.put(HistoricalQuoteColumns.OPEN, Float.valueOf(qe.getOpen()));
      qv.put(HistoricalQuoteColumns.HIGH, Float.valueOf(qe.getHigh()));
      qv.put(HistoricalQuoteColumns.LOW, Float.valueOf(qe.getLow()));
      qv.put(HistoricalQuoteColumns.CLOSE, Float.valueOf(qe.getClose()));
      qv.put(HistoricalQuoteColumns.ADJ_CLOSE, Float.valueOf(qe.getAdj_Close()));

      quoteValues[i] = qv;
    }

    return quoteValues;
  }

  private void processQuotes(String getResponse) {
    try {
      ContentValues contentValues = new ContentValues();
      // update ISCURRENT to 0 (false) so new data is current
      if (isUpdate){
        contentValues.put(QuoteColumns.ISCURRENT, 0);
        mContext.getContentResolver().update(QuoteProvider.Quotes.CONTENT_URI, contentValues,
            null, null);
      }
      mContext.getContentResolver().applyBatch(QuoteProvider.AUTHORITY,
          Utils.quoteJsonToContentVals(getResponse));
    }catch (RemoteException | OperationApplicationException e){
      Log.e(LOG_TAG, "Error applying batch insert", e);
    }
  }

  private StringBuilder buildYahooStockDataUrl(TaskParams params, StringBuilder urlStringBuilder) {
    // Base URL for the Yahoo query
    urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");

    String diagnostics = "false" ;
    if (params.getTag().equals("init") || params.getTag().equals("periodic")){
      attachQuoteQuery(urlStringBuilder);
      buildInitWhereClause(urlStringBuilder);
    }
    else if (params.getTag().equals("add")){
      attachQuoteQuery(urlStringBuilder);
      buildAddWhereClause(params, urlStringBuilder);
    }
    else if (params.getTag().equals("historical")){
      attachHistoricalDataQuery(urlStringBuilder);
      buildHistoricalDataWhereClause(params, urlStringBuilder);
      diagnostics = "false";
    }
    // finalize the URL for the API query.
    urlStringBuilder.append("&format=json&diagnostics=" + diagnostics +
            "&env=store%3A%2F%2Fdatatables" +
            ".org%2Falltableswithkeys&callback=");

    return urlStringBuilder;
  }


  private void  attachQuoteQuery(StringBuilder urlStringBuilder) {
    try{
      urlStringBuilder.append(URLEncoder.encode(
              "select Change, symbol, Bid, ChangeinPercent from yahoo.finance.quotes where symbol "
              + "in (", "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }


  private void buildInitWhereClause(StringBuilder urlStringBuilder) {
    Cursor initQueryCursor;
    isUpdate = true;
    initQueryCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
        new String[] { "Distinct " + QuoteColumns.SYMBOL }, null,
        null, null);
    if (initQueryCursor.getCount() == 0 || initQueryCursor == null){
      // Init task. Populates DB with quotes for the symbols seen below
      try {
        urlStringBuilder.append(
            URLEncoder.encode("\"YHOO\",\"AAPL\",\"GOOG\",\"MSFT\")", "UTF-8"));
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    } else if (initQueryCursor != null){
      DatabaseUtils.dumpCursor(initQueryCursor);
      initQueryCursor.moveToFirst();
      for (int i = 0; i < initQueryCursor.getCount(); i++){
        mStoredSymbols.append("\""+
            initQueryCursor.getString(initQueryCursor.getColumnIndex("symbol"))+"\",");
        initQueryCursor.moveToNext();
      }
      mStoredSymbols.replace(mStoredSymbols.length() - 1, mStoredSymbols.length(), ")");
      try {
        urlStringBuilder.append(URLEncoder.encode(mStoredSymbols.toString(), "UTF-8"));
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    }
  }


  private void attachHistoricalDataQuery (StringBuilder urlStringBuilder) {
    try{
      urlStringBuilder.append(URLEncoder.encode(
              "select * from yahoo.finance.historicaldata where symbol= ", "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  private void buildHistoricalDataWhereClause(TaskParams params, StringBuilder urlStringBuilder) {

    // get symbol from params.getExtra and build query
    String stockInput = params.getExtras().getString("symbol");
    try {
      urlStringBuilder.append(URLEncoder.encode( "\""+stockInput+"\"", "UTF-8"));
    } catch (UnsupportedEncodingException e){
      e.printStackTrace();
    }

    if(doWeHaveDataFromTheLast30Days(stockInput))
      encodeStringForMissingDays(urlStringBuilder, stockInput);
    else
      encodeStringFor30Days(urlStringBuilder);

  }


  private void encodeStringForMissingDays(StringBuilder urlStringBuilder, String stockInput) {
    try {
      urlStringBuilder.append(URLEncoder.encode(
              stringForMissingDays(stockInput), "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  private String stringForMissingDays(String stockInput) {

    Calendar c = Calendar.getInstance();
    Date date = new Date();
    c.setTime(date);
    String endDate = toDateStringWithDashes(c);
    String startDate = getLastRetrievedDate(stockInput);
    return  " and startDate = \"" +  startDate + "\"  and endDate = \"" +
            endDate  + "\" ";

  }

  private String getLastRetrievedDate(String stockInput) {

    Cursor initQueryCursor = null;
    try {
      initQueryCursor = mContext.getContentResolver().query(
              QuoteProvider.HistoricalQuotes.CONTENT_URI,
              new String[]{HistoricalQuoteColumns.CLOSE_DATE, HistoricalQuoteColumns.CLOSE},
              HistoricalQuoteColumns.SYMBOL + "= ?",
              new String[]{stockInput},
              HistoricalQuoteColumns.CLOSE_DATE + " DESC");  // Last first

      if (initQueryCursor.getCount() == 0 || initQueryCursor == null) {
        return null;
      } else {
        initQueryCursor.moveToFirst();
        int dateColumnIndex = initQueryCursor.getColumnIndex(HistoricalQuoteColumns.CLOSE_DATE);
        String lastRetrievedDate = initQueryCursor.getString(dateColumnIndex);
        if (lastRetrievedDate != null){
          if(lastRetrievedDate.contains("-"))
            return lastRetrievedDate.replace("-", "");
        }
        return null;
      }
    } finally {
      if(initQueryCursor != null)
        initQueryCursor.close();
    }
  }

  private boolean doWeHaveDataFromTheLast30Days(String stockInput) {

    if(getLastRetrievedDate(stockInput) == null)
      return false;
    return Integer.parseInt(getLastRetrievedDate(stockInput))
            > Integer.parseInt(dateStringFrom30DaysAgo());
  }

  private String  dateStringFrom30DaysAgo() {
    // Get string for 30 days ago
    Calendar c = Calendar.getInstance();
    Date date = new Date();
    c.setTime(date);
    c.add(Calendar.DATE, -30);   // last 30 days
    return toDateString(c);
  }

  private void encodeStringFor30Days(StringBuilder urlStringBuilder) {
    try {
      urlStringBuilder.append(URLEncoder.encode(stringForTheLast30Days(), "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  private void buildAddWhereClause(TaskParams params, StringBuilder urlStringBuilder) {
    isUpdate = false;
    // get symbol from params.getExtra and build query
    String stockInput = params.getExtras().getString("symbol");
    try {
      urlStringBuilder.append(URLEncoder.encode("\""+stockInput+"\")", "UTF-8"));
    } catch (UnsupportedEncodingException e){
      e.printStackTrace();
    }
  }

  private String stringForTheLast30Days() {

    Calendar c = Calendar.getInstance();
    Date date = new Date();
    c.setTime(date);
    String endDate = toDateStringWithDashes(c);
    c.add(Calendar.DATE, -30);   // last 30 days
    String startDate = toDateStringWithDashes(c);

    return  " and startDate = \"" +  startDate + "\"  and endDate = \"" +
            endDate  + "\" ";

  }

  private String toDateStringWithDashes(Calendar c) {

    return  Integer.toString( c.get(Calendar.YEAR)  ) + "-" +
            Integer.toString( 1+ c.get(Calendar.MONTH) ) + "-" +  // Month zero-offset
            Integer.toString( c.get(Calendar.DATE)  );

  }

  private String toDateString(Calendar c) {

    return  Integer.toString( c.get(Calendar.YEAR)  )  +
            Integer.toString( 1+ c.get(Calendar.MONTH) )  +  // Month zero-offset
            Integer.toString( c.get(Calendar.DATE)  );

  }

}
