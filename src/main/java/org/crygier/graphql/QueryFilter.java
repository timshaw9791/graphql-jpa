package org.crygier.graphql;

import cn.wzvtcsoft.x.bos.domain.BosEntity;
import cn.wzvtcsoft.x.bos.domain.Bostype;
import groovy.transform.CompileStatic;
import org.crygier.graphql.annotation.SchemaDocumentation;

@SchemaDocumentation("过滤条件")
public class QueryFilter {

    @SchemaDocumentation("键，可以带导航的.号")
    public void setKey(String key) {
        this.key = key;
    }
    @SchemaDocumentation("值，可以是和like相对应的%abc%")
    public void setValue(String value) {
        this.value = value;
    }
    @SchemaDocumentation("操作符")
    public void setOperator(QueryFilterOperator operator) {
        this.operator = operator;
    }
    @SchemaDocumentation("条件组合符号")
    public void setCombinator(QueryFilterCombinator combinator) {
        this.combinator = combinator;
    }
    @SchemaDocumentation("下一个条件")
    public void setNext(QueryFilter next) {
        this.next = next;
    }


    private String key;

    private String value;

    private QueryFilterOperator operator;

    private QueryFilterCombinator combinator;

    private QueryFilter next;

    public QueryFilter(){

    }
    public QueryFilter(String key, QueryFilterOperator operator, String value, QueryFilterCombinator combinator, QueryFilter next) {
        this.key = key;
        this.value = value;
        this.operator = operator;
        this.combinator = combinator;
        this.next = next;
    }


    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public QueryFilterOperator getOperator() {
        return operator;
    }

    public QueryFilterCombinator getCombinator() {
        return combinator;
    }

    public QueryFilter getNext() {
        return next;
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



enum QueryFilterCombinator{
    AND(0,"AND","and"),
    OR(1,"OR","or"),
    NOT(2,"NOT","!");


    private QueryFilterCombinator(int value,String name,String description){
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
