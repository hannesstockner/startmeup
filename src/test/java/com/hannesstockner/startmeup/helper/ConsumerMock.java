package com.hannesstockner.startmeup.helper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Consumer;

public class ConsumerMock<T> {
  private List<T> results = Lists.newArrayList();
  private T result;

  public T getResult() {
    return result;
  }

  public ImmutableList<T> getResults() {
    return ImmutableList.copyOf(results);
  }

  public final Consumer<T> consumer = new Consumer<T>() {
    @Override
    public void accept(T r) {
      result = r;
      results.add(r);
    }
  };
}
