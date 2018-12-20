package org.knowm.xchange.alt.utils.retries;

public interface IPredicate<T> {
  boolean test(T t);
}
