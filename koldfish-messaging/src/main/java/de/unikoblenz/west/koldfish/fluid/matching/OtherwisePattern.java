package de.unikoblenz.west.koldfish.fluid.matching;

import java.util.function.Function;

/**
 * Created by Martin Leinberger on 13.04.2016.
 */
public class OtherwisePattern implements PatternMatching.Pattern {
    public static PatternMatching.Pattern otherwise(Function<Object, Object> function) {
        return new OtherwisePattern(function);
    }

    private final Function<Object,Object> function;

    public OtherwisePattern(Function<Object, Object> function) {
        this.function = function;
    }

    @Override
    public boolean matches(Object value) {
        return true;
    }

    @Override
    public Object apply(Object value) {
        return function.apply(value);
    }
}
