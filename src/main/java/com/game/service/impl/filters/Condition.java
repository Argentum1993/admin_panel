package com.game.service.impl.filters;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Condition {
    private String field;
    private List<Object> values;
    private TypeValue typeValue;
    private Comparison comparison;

    public Condition(TypeValue typeValue, Comparison comparison, String field, Object ...values){
        this.typeValue = typeValue;
        this.comparison = comparison;
        this.field = field;
        this.values = Arrays.stream(values).collect(Collectors.toList());
    }

    public String getField() {
        return field;
    }

    public List<Object> getValues() {
        return values;
    }

    public TypeValue getTypeValue() {
        return typeValue;
    }

    public Comparison getComparison() {
        return comparison;
    }
}
