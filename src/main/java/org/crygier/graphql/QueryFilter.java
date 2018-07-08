package org.crygier.graphql;

public class QueryFilter {
    public String getK() {
        return k;
    }

    public String getV() {
        return v;
    }

    public QueryFilterOperator getO() {
        return o;
    }

    public String getAndor() {
        return andor;
    }

    public QueryFilter getNext() {
        return next;
    }

    private String k;
    private String v;
    private QueryFilterOperator o;
    private String andor;
    private QueryFilter next;

    public QueryFilter(String k, QueryFilterOperator o, String v, String andor, QueryFilter next) {
        this.k = k;
        this.v = v;
        this.o = o;
        this.andor = andor;
        this.next = next;
    }

}

//TODO 可能需要扩展或者更规范化
enum QueryFilterOperator{
    ISNULL(0,"ISNULL","is null"),
    ISNOTNULL(1,"ISNOTNULL","is not null"),
    GREATTHAN(2,"GREATTHAN",">"),
    LESSTHAN(3,"LESSTHAN","<"),
    NOTLESSTHAN(4,"NOTLESSTHAN",">="),
    NOTGREATTHAN(5,"NOTGREATTHAN","<="),
    EQUEAL(6,"EQUEAL","="),
    IN(7,"IN","in"),
    NOTIN(8,"NOTIN","not in"),
    NOT(9,"NOT","not"),
    LIKE(10,"LIKE","like");

    private QueryFilterOperator(int value,String name,String description){
        this.value=value;
        this.name=name;
        this.description=description;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    private int value;
    private String name;
    private String description;

}

