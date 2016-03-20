package com.sam_chordas.android.stockhawk.data;

import java.util.List;

/**
 * Created by jon on 3/11/2016.
 */
public class HistoricalQuoteResults {


    /**
     * count : 6
     * created : 2016-03-11T15:32:06Z
     * lang : en-US
     * results : {"quote":[{"Symbol":"YHOO","Date":"2009-09-18","Open":"17.700001","High":"17.700001","Low":"16.85","Close":"17.389999","Volume":"86402600","Adj_Close":"17.389999"},{"Symbol":"YHOO","Date":"2009-09-17","Open":"17.00","High":"17.790001","Low":"16.959999","Close":"17.50","Volume":"62010000","Adj_Close":"17.50"},{"Symbol":"YHOO","Date":"2009-09-16","Open":"16.57","High":"17.110001","Low":"16.52","Close":"16.99","Volume":"53594700","Adj_Close":"16.99"},{"Symbol":"YHOO","Date":"2009-09-15","Open":"16.01","High":"16.49","Low":"15.87","Close":"16.41","Volume":"64668200","Adj_Close":"16.41"},{"Symbol":"YHOO","Date":"2009-09-14","Open":"15.45","High":"15.58","Low":"15.28","Close":"15.57","Volume":"19451200","Adj_Close":"15.57"},{"Symbol":"YHOO","Date":"2009-09-11","Open":"15.53","High":"15.68","Low":"15.41","Close":"15.59","Volume":"26860700","Adj_Close":"15.59"}]}
     */

    private QueryEntity query;

    public void setQuery(QueryEntity query) {
        this.query = query;
    }

    public QueryEntity getQuery() {
        return query;
    }

    public static class QueryEntity {
        private int count;
        private String created;
        private String lang;
        private ResultsEntity results;

        public void setCount(int count) {
            this.count = count;
        }

        public void setCreated(String created) {
            this.created = created;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public void setResults(ResultsEntity results) {
            this.results = results;
        }

        public int getCount() {
            return count;
        }

        public String getCreated() {
            return created;
        }

        public String getLang() {
            return lang;
        }

        public ResultsEntity getResults() {
            return results;
        }

        public static class ResultsEntity {
            /**
             * Symbol : YHOO
             * Date : 2009-09-18
             * Open : 17.700001
             * High : 17.700001
             * Low : 16.85
             * Close : 17.389999
             * Volume : 86402600
             * Adj_Close : 17.389999
             */

            private List<QuoteEntity> quote;

            public void setQuote(List<QuoteEntity> quote) {
                this.quote = quote;
            }

            public List<QuoteEntity> getQuote() {
                return quote;
            }

            public static class QuoteEntity {
                private String Symbol;
                private String Date;
                private String Open;
                private String High;
                private String Low;
                private String Close;
                private String Volume;
                private String Adj_Close;

                public void setSymbol(String Symbol) {
                    this.Symbol = Symbol;
                }

                public void setDate(String Date) {
                    this.Date = Date;
                }

                public void setOpen(String Open) {
                    this.Open = Open;
                }

                public void setHigh(String High) {
                    this.High = High;
                }

                public void setLow(String Low) {
                    this.Low = Low;
                }

                public void setClose(String Close) {
                    this.Close = Close;
                }

                public void setVolume(String Volume) {
                    this.Volume = Volume;
                }

                public void setAdj_Close(String Adj_Close) {
                    this.Adj_Close = Adj_Close;
                }

                public String getSymbol() {
                    return Symbol;
                }

                public String getDate() {
                    return Date;
                }

                public String getOpen() {
                    return Open;
                }

                public String getHigh() {
                    return High;
                }

                public String getLow() {
                    return Low;
                }

                public String getClose() {
                    return Close;
                }

                public String getVolume() {
                    return Volume;
                }

                public String getAdj_Close() {
                    return Adj_Close;
                }
            }
        }
    }
}

