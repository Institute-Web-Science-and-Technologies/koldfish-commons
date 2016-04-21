package de.unikoblenz.west.koldfish.fluid.matching;

import java.util.function.Function;

/**
 * Created by Martin Leinberger on 13.04.2016.
 */
public class ClassPattern<T> implements PatternMatching.Pattern {

  public static <T> PatternMatching.Pattern inCase(Class<T> clazz, Function<T, Object> function) {
    return new ClassPattern<T>(clazz, function);
  }

  private final Class<T> clazz;
  private final Function<T, Object> function;

  public ClassPattern(Class<T> clazz, Function<T, Object> function) {
    this.clazz = clazz;
    this.function = function;
  }

  @Override
  public boolean matches(Object value) {
    return clazz.isInstance(value);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Object apply(Object value) {
    return function.apply((T) value);
  }
}
