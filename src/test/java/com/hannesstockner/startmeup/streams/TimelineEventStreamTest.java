package com.hannesstockner.startmeup.streams;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.hannesstockner.startmeup.events.MessagePosted;
import org.junit.Test;

import java.time.OffsetDateTime;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class TimelineEventStreamTest {

  private final OffsetDateTime dateTime = OffsetDateTime.parse("2015-05-01T10:15:30Z");
  private final TimelineEventStream eventStream = new TimelineEventStream((o) -> {});

  @Test
  public void shouldAddMessageToStreamIfStreamIsEmpty() {
    final MessagePosted event = new MessagePosted("Bob", "Good morning!", dateTime);

    final ImmutableSet<MessagePosted> expected = ImmutableSet.of(event);

    eventStream.handleMessagePosted(event);
    final ImmutableSet<MessagePosted> events = eventStream.eventsByStreamId("Bob");

    assertThat(events, is(expected));
  }

  @Test
  public void shouldAddMessageToStreamIfStreamContainsEvents() {
    final MessagePosted event =
      new MessagePosted("Bob", "Good morning!", dateTime);
    final MessagePosted event1 =
      new MessagePosted("Bob", "Good afternoon!", dateTime.plusHours(4));
    final MessagePosted event2 =
      new MessagePosted("Bob", "Good night!", dateTime.plusHours(8));

    final ImmutableSet<MessagePosted> expected = ImmutableSet.of(event2, event1, event);

    eventStream.handleMessagePosted(event);
    eventStream.handleMessagePosted(event1);
    eventStream.handleMessagePosted(event2);
    final ImmutableSet<MessagePosted> events = eventStream.eventsByStreamId("Bob");

    assertThat(events, is(expected));
    assertTrue(events.stream().findFirst().get().equals(event2));
  }

  @Test
  public void shouldAddMessageToStreamWithDifferentName() {
    final MessagePosted event =
      new MessagePosted("Bob", "Good morning!", dateTime);
    final MessagePosted event1 =
      new MessagePosted("Alice", "Good afternoon!", dateTime.plusHours(4));
    final MessagePosted event2 =
      new MessagePosted("Bob", "Good night!", dateTime.plusHours(8));

    final ImmutableSet<MessagePosted> expected = ImmutableSet.of(event2, event);
    final ImmutableSet<MessagePosted> expected1 = ImmutableSet.of(event1);

    eventStream.handleMessagePosted(event);
    eventStream.handleMessagePosted(event1);
    eventStream.handleMessagePosted(event2);
    final ImmutableSet<MessagePosted> events = eventStream.eventsByStreamId("Bob");
    final ImmutableSet<MessagePosted> events1 = eventStream.eventsByStreamId("Alice");

    assertThat(events, is(expected));
    assertThat(events1, is(expected1));
  }

  @Test
  public void testAddMessageWithEmptyExisting() {
    final MessagePosted event =
      new MessagePosted("Bob", "Good morning!", dateTime);

    final ImmutableSetMultimap<String, MessagePosted> expected =
      ImmutableSetMultimap.of(event.getName(), event);

    final ImmutableSetMultimap<String, MessagePosted> existing =
      ImmutableSetMultimap.of();

    final ImmutableSetMultimap<String, MessagePosted> newMultimap =
      TimelineEventStream.addMessage(existing, event);

    assertThat(newMultimap, is(expected));
  }

  @Test
  public void testAddMessageWithNonEmptyExisting() {
    final MessagePosted event =
      new MessagePosted("Bob", "Good morning!", dateTime);

    final MessagePosted event1 =
      new MessagePosted("Bob", "Good afternoon!", dateTime.plusHours(4));

    final ImmutableSetMultimap<String, MessagePosted> expected =
      ImmutableSetMultimap.of(event.getName(), event, event1.getName(), event1);

    final ImmutableSetMultimap<String, MessagePosted> existing =
      ImmutableSetMultimap.of(event.getName(), event);

    final ImmutableSetMultimap<String, MessagePosted> newMultimap =
      TimelineEventStream.addMessage(existing, event1);

    assertThat(newMultimap, is(expected));
  }
}