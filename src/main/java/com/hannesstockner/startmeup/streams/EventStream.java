package com.hannesstockner.startmeup.streams;

import com.google.common.collect.ImmutableSet;

/**
 * Every stream implementation has to return a set of events by streamId.
 */
public interface EventStream<T> {

  ImmutableSet<T> eventsByStreamId(final String streamId);
}
