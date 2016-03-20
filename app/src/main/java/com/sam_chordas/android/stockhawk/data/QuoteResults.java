package com.sam_chordas.android.stockhawk.data;

import java.util.List;

/**
 * Created by jon on 3/19/2016.
 */
public class QuoteResults {

    /**
     * count : 4
     * created : 2016-03-19T15:03:01Z
     * lang : en-US
     * results : {"quote":[{"symbol":"YHOO","Bid":"35.00","Change":"+0.89","ChangeinPercent":"+2.61%"},{"symbol":"AAPL","Bid":"105.76","Change":"+0.12","ChangeinPercent":"+0.11%"},{"symbol":"GOOG","Bid":"736.80","Change":"-0.18","ChangeinPercent":"-0.02%"},{"symbol":"MSFT","Bid":"53.41","Change":"-1.17","ChangeinPercent":"-2.14%"}]}
     */

    private QueryEntity query;

    public QueryEntity getQuery() {
        return query;
    }

    public void setQuery(QueryEntity query) {
        this.query = query;
    }

    public static class QueryEntity {
        private int count;
        private String created;
        private String lang;
        private ResultsEntity results;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public ResultsEntity getResults() {
            return results;
        }

        public void setResults(ResultsEntity results) {
            this.results = results;
        }

        public static class ResultsEntity {
            /**
             * symbol : YHOO
             * Bid : 35.00
             * Change : +0.89
             * ChangeinPercent : +2.61%
             */

            private List<QuoteEntity> quote;

            public List<QuoteEntity> getQuote() {
                return quote;
            }

            public void setQuote(List<QuoteEntity> quote) {
                this.quote = quote;
            }

            public static class QuoteEntity {
                private String symbol;
                private String Bid;
                private String Change;
                private String ChangeinPercent;

                public String getSymbol() {
                    return symbol;
                }

                public void setSymbol(String symbol) {
                    this.symbol = symbol;
                }

                public String getBid() {
                    return Bid;
                }

                public void setBid(String Bid) {
                    this.Bid = Bid;
                }

                public String getChange() {
                    return Change;
                }

                public void setChange(String Change) {
                    this.Change = Change;
                }

                public String getChangeinPercent() {
                    return ChangeinPercent;
                }

                public void setChangeinPercent(String ChangeinPercent) {
                    this.ChangeinPercent = ChangeinPercent;
                }
            }
        }
    }
}
