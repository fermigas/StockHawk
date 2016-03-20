package com.sam_chordas.android.stockhawk.data;

/**
 * Created by jon on 3/19/2016.
 */
public class QuoteResult {


    /**
     * count : 1
     * created : 2016-03-19T15:48:22Z
     * lang : en-US
     * results : {"quote":{"symbol":"AxsfY","Bid":null,"Change":null,"ChangeinPercent":null}}
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
        /**
         * quote : {"symbol":"AxsfY","Bid":null,"Change":null,"ChangeinPercent":null}
         */

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
             * symbol : AxsfY
             * Bid : null
             * Change : null
             * ChangeinPercent : null
             */

            private QuoteEntity quote;

            public QuoteEntity getQuote() {
                return quote;
            }

            public void setQuote(QuoteEntity quote) {
                this.quote = quote;
            }

            public static class QuoteEntity {
                private String symbol;
                private Object Bid;
                private Object Change;
                private Object ChangeinPercent;

                public String getSymbol() {
                    return symbol;
                }

                public void setSymbol(String symbol) {
                    this.symbol = symbol;
                }

                public Object getBid() {
                    return Bid;
                }

                public void setBid(Object Bid) {
                    this.Bid = Bid;
                }

                public Object getChange() {
                    return Change;
                }

                public void setChange(Object Change) {
                    this.Change = Change;
                }

                public Object getChangeinPercent() {
                    return ChangeinPercent;
                }

                public void setChangeinPercent(Object ChangeinPercent) {
                    this.ChangeinPercent = ChangeinPercent;
                }
            }
        }
    }
}
