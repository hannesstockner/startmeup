package com.hannesstockner.startmeup.streams;

import com.google.common.collect.*;
import com.google.common.eventbus.Subscribe;
import com.hannesstockner.startmeup.events.MessagePosted;

import java.util.Comparator;
import java.util.function.Consumer;

/**
 * Contains a stream of MessagePosted events as a stream.
 *
 * The stream reacts on events and updates the stream.
 * It subscribes to all events necessary to build up the stream.
 */
public class TimelineEventStream implements EventStream<MessagePosted> {

  private ImmutableSetMultimap<String, MessagePosted> stream =
    ImmutableSetMultimap.of();

  public TimelineEventStream(final Consumer<Object> eventRegister) {
    eventRegister.accept(this);
  }

  @Subscribe
  public void handleMessagePosted(final MessagePosted event) {
    stream = addMessage(stream, event);
  }

  protected static ImmutableSetMultimap<String, MessagePosted> addMessage(
      final ImmutableSetMultimap<String, MessagePosted> existing,
      final MessagePosted event) {
    return new ImmutableSetMultimap.Builder<String, MessagePosted>()
      .putAll(existing)
      .put(event.getName(), event)
      .orderValuesBy(
        Comparator
          .comparing(MessagePosted::getTimestamp, Comparator.reverseOrder())
          .thenComparing(Comparator.comparing(MessagePosted::getMessage)))
      .build();
  }

  @Override
  public ImmutableSet<MessagePosted> eventsByStreamId(final String streamId) {
    return stream.get(streamId);
  }
}
