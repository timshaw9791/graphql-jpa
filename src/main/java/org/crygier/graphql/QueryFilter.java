package org.crygier.graphql;

public class QueryFilter {
    String k;
    String v;
    String o;
    String andor;
    QueryFilter next;

    public QueryFilter(String k, String o, String v, String andor, QueryFilter next) {
        this.k = k;
        this.v = v;
        this.o = o;
        this.andor = andor;
        this.next = next;
    }
}

