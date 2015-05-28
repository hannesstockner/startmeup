package com.hannesstockner.startmeup.streams;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.hannesstockner.startmeup.events.MessagePosted;
import com.hannesstockner.startmeup.events.UserFollowed;
import org.junit.Test;

import java.time.OffsetDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class WallEventStreamTest {

  private final OffsetDateTime dateTime = OffsetDateTime.parse("2015-05-01T10:15:30Z");
  private final WallEventStream eventStream = new WallEventStream((o) -> {});

  @Test
  public void shouldAddMessageToStreamIfNoFollowersAndStreamIsEmpty() {
    final MessagePosted event = new MessagePosted("Bob", "Good morning!", dateTime);

    final ImmutableSet<MessagePosted> expected = ImmutableSet.of(event);

    eventStream.handleMessagePosted(event);
    final ImmutableSet<MessagePosted> events = eventStream.eventsByStreamId("Bob");

    assertThat(events, is(expected));
  }

  @Test
  public void shouldAddMessageToStreamIfFollowersAndStreamIsEmpty() {
    final UserFollowed userFollowed = new UserFollowed("Alice", "Bob", dateTime);
    final MessagePosted event = new MessagePosted("Bob", "Good morning!", dateTime);

    final ImmutableSet<MessagePosted> expected = ImmutableSet.of(event);

    eventStream.handleUserFollowed(userFollowed);
    eventStream.handleMessagePosted(event);
    final ImmutableSet<MessagePosted> events = eventStream.eventsByStreamId("Alice");

    assertThat(events, is(expected));
  }

  @Test
  public void shouldAddMessageToStreamIfFollowersAndStreamContainsEvents() {
    final MessagePosted event = new MessagePosted("Bob", "Good morning!", dateTime);
    final UserFollowed userFollowed = new UserFollowed("Alice", "Bob", dateTime);
    final MessagePosted event1 =
      new MessagePosted("Bob", "Good afternoon!", dateTime.plusHours(4));

    final ImmutableSet<MessagePosted> expected = ImmutableSet.of(event1, event);

    eventStream.handleMessagePosted(event);
    eventStream.handleUserFollowed(userFollowed);
    eventStream.handleMessagePosted(event1);
    final ImmutableSet<MessagePosted> events = eventStream.eventsByStreamId("Alice");

    assertThat(events, is(expected));
    assertTrue(events.stream().findFirst().get().equals(event1));
  }

  @Test
  public void shouldAddMessageToStreamIfMultipleFollowersAndStreamContainsEvents() {
    final MessagePosted event = new MessagePosted("Bob", "Good morning!", dateTime);
    final MessagePosted event1 = new MessagePosted("Rich", "Good morning!", dateTime);
    final UserFollowed userFollowed = new UserFollowed("Alice", "Bob", dateTime);
    final UserFollowed userFollowed1 = new UserFollowed("Alice", "Rich", dateTime);
    final MessagePosted event2 =
      new MessagePosted("Bob", "Good afternoon!", dateTime.plusHours(4));

    final ImmutableSet<MessagePosted> expected = ImmutableSet.of(event2, event1, event);

    eventStream.handleMessagePosted(event);
    eventStream.handleUserFollowed(userFollowed);
    eventStream.handleMessagePosted(event1);
    eventStream.handleMessagePosted(event2);
    eventStream.handleUserFollowed(userFollowed1);
    final ImmutableSet<MessagePosted> events = eventStream.eventsByStreamId("Alice");

    assertThat(events, is(expected));
    assertTrue(events.stream().findFirst().get().equals(event2));
  }

  @Test
  public void testAddMessagesByIdWithEmptyExisting() {
    final MessagePosted event =
      new MessagePosted("Bob", "Good morning!", dateTime);

    final ImmutableSetMultimap<String, MessagePosted> expected =
      ImmutableSetMultimap.of(event.getName(), event);

    final ImmutableSetMultimap<String, MessagePosted> existing =
      ImmutableSetMultimap.of();

    final ImmutableSetMultimap<String, MessagePosted> newMultimap =
      WallEventStream.addMessagesById(existing, event);

    assertThat(newMultimap, is(expected));
  }

  @Test
  public void testAddMessagesByIdWithNonEmptyExisting() {
    final MessagePosted event =
      new MessagePosted("Bob", "Good morning!", dateTime);

    final MessagePosted event1 =
      new MessagePosted("Bob", "Good afternoon!", dateTime.plusHours(4));

    final ImmutableSetMultimap<String, MessagePosted> expected =
      ImmutableSetMultimap.of(event.getName(), event, event1.getName(), event1);

    final ImmutableSetMultimap<String, MessagePosted> existing =
      ImmutableSetMultimap.of(event.getName(), event);

    final ImmutableSetMultimap<String, MessagePosted> newMultimap =
      WallEventStream.addMessagesById(existing, event1);

    assertThat(newMultimap, is(expected));
  }

  @Test
  public void testAddFollowerWithEmptyExisting() {
    final UserFollowed event = new UserFollowed("Bob", "Alice", dateTime);

    final ImmutableSetMultimap<String, String> expected =
      ImmutableSetMultimap.of(event.getFollowing(), event.getName());

    final ImmutableSetMultimap<String, String> existing =
      ImmutableSetMultimap.of();

    final ImmutableSetMultimap<String, String> newMultimap =
      WallEventStream.addFollower(existing, event);

    assertThat(newMultimap, is(expected));
  }

  @Test
  public void testAddFollowerWithNonEmptyExisting() {
    final UserFollowed event = new UserFollowed("Bob", "Alice", dateTime);
    final UserFollowed event1 = new UserFollowed("Rob", "Alice", dateTime);

    final ImmutableSetMultimap<String, String> expected =
      ImmutableSetMultimap.of(
        event.getFollowing(), event.getName(), event1.getFollowing(), event1.getName());

    final ImmutableSetMultimap<String, String> existing =
      ImmutableSetMultimap.of(event.getFollowing(), event.getName());

    final ImmutableSetMultimap<String, String> newMultimap =
      WallEventStream.addFollower(existing, event1);

    assertThat(newMultimap, is(expected));
  }

  @Test
  public void testAddMessagesWithEmptyExisting() {
    final MessagePosted event =
      new MessagePosted("Bob", "Good morning!", dateTime);

    final ImmutableSetMultimap<String, MessagePosted> expected =
      ImmutableSetMultimap.of("Alice", event);

    final ImmutableSetMultimap<String, MessagePosted> existing =
      ImmutableSetMultimap.of();

    final ImmutableSetMultimap<String, MessagePosted> newMultimap =
      WallEventStream.addMessages(existing, "Alice", ImmutableSet.of(event));

    assertThat(newMultimap, is(expected));
  }

  @Test
  public void testAddMessagesWithNonEmptyExisting() {
    final MessagePosted event =
      new MessagePosted("Bob", "Good morning!", dateTime);

    final MessagePosted event1 =
      new MessagePosted("Rich", "Good afternoon!", dateTime.plusHours(4));
    final MessagePosted event2 =
      new MessagePosted("Rich", "Good night!", dateTime.plusHours(8));

    final ImmutableSetMultimap<String, MessagePosted> expected =
      ImmutableSetMultimap.of("Alice", event2, "Alice", event1, "Alice", event);

    final ImmutableSetMultimap<String, MessagePosted> existing =
      ImmutableSetMultimap.of("Alice", event);

    final ImmutableSetMultimap<String, MessagePosted> newMultimap =
      WallEventStream.addMessages(existing, "Alice", ImmutableSet.of(event2, event1));

    assertThat(newMultimap, is(expected));
    assertTrue(newMultimap.get("Alice").stream().findFirst().get().equals(event2));
  }

  @Test
  public void testAddMessageToOneUserWithEmptyExisting() {
    final MessagePosted event =
      new MessagePosted("Bob", "Good morning!", dateTime);

    final ImmutableSetMultimap<String, MessagePosted> expected =
      ImmutableSetMultimap.of("Alice", event);

    final ImmutableSetMultimap<String, MessagePosted> existing =
      ImmutableSetMultimap.of();

    final ImmutableSetMultimap<String, MessagePosted> newMultimap =
      WallEventStream.addMessage(existing, ImmutableSet.of("Alice"), event);

    assertThat(newMultimap, is(expected));
  }

  @Test
  public void testAddMessageToMultipleUserWithEmptyExisting() {
    final MessagePosted event =
      new MessagePosted("Bob", "Good morning!", dateTime);

    final ImmutableSetMultimap<String, MessagePosted> expected =
      ImmutableSetMultimap.of("Alice", event, "Rob", event);

    final ImmutableSetMultimap<String, MessagePosted> existing =
      ImmutableSetMultimap.of();

    final ImmutableSetMultimap<String, MessagePosted> newMultimap =
      WallEventStream.addMessage(existing, ImmutableSet.of("Alice", "Rob"), event);

    assertThat(newMultimap, is(expected));
  }

}