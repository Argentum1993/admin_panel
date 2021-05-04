package com.game.service.impl.filters;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Condition {
    private String field;
    private Object value;
    private TypeValue typeValue;
    private Comparison comparison;

    public Condition(TypeValue typeValue, Comparison comparison, String field, Object value){
        this.typeValue = typeValue;
        this.comparison = comparison;
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }

    public TypeValue getTypeValue() {
        return typeValue;
    }

    public Comparison getComparison() {
        return comparison;
    }
}
