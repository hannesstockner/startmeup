package com.hannesstockner.startmeup.streams;

import com.google.common.collect.*;
import com.google.common.eventbus.Subscribe;
import com.hannesstockner.startmeup.events.MessagePosted;
import com.hannesstockner.startmeup.events.UserFollowed;

import java.util.Comparator;
import java.util.function.Consumer;

/**
 * Contains a stream of MessagePosted events as a stream based on user following
 * other users.
 *
 * The stream reacts on events and updates the stream.
 * It subscribes to all events necessary to build up the stream.
 */
public class WallEventStream implements EventStream<MessagePosted> {

  private ImmutableSetMultimap<String, MessagePosted> stream =
    ImmutableSetMultimap.of();

  private ImmutableSetMultimap<String, MessagePosted> messagesById =
    ImmutableSetMultimap.of();
  private ImmutableSetMultimap<String, String> userFollowedBy =
    ImmutableSetMultimap.of();

  public WallEventStream(final Consumer<Object> eventPublisher) {
    eventPublisher.accept(this);
  }

  @Subscribe
  public void handleMessagePosted(final MessagePosted event) {
    messagesById = addMessagesById(messagesById, event);
    stream = addMessage(
      stream,
      new ImmutableSet.Builder<String>()
        .addAll(userFollowedBy.get(event.getName()))
        .add(event.getName())
        .build(),
      event);
  }

  @Subscribe
  public void handleUserFollowed(final UserFollowed event) {
    if (!userFollowedBy.containsEntry(event.getFollowing(), event.getName())) {
      userFollowedBy = addFollower(userFollowedBy, event);

      stream = addMessages(stream,
        event.getName(), messagesById.get(event.getFollowing()));
    }
  }

  protected static ImmutableSetMultimap<String, MessagePosted> addMessagesById(
      final ImmutableSetMultimap<String, MessagePosted> existing,
      final MessagePosted event) {
    return new ImmutableSetMultimap.Builder<String, MessagePosted>()
      .put(event.getName(), event)
      .putAll(existing)
      .build();
  }

  protected static ImmutableSetMultimap<String, String> addFollower(
      final ImmutableSetMultimap<String, String> existing,
      final UserFollowed event) {
    return new ImmutableSetMultimap.Builder<String, String>()
      .putAll(existing)
      .put(event.getFollowing(), event.getName())
      .build();
  }

  protected static ImmutableSetMultimap<String, MessagePosted> addMessages(
      final ImmutableSetMultimap<String, MessagePosted> existing,
      final String name, final ImmutableSet<MessagePosted> events) {
    return new ImmutableSetMultimap.Builder<String, MessagePosted>()
      .orderValuesBy(
        Comparator
          .comparing(MessagePosted::getTimestamp, Comparator.reverseOrder())
          .thenComparing(Comparator.comparing(MessagePosted::getName))
          .thenComparing(Comparator.comparing(MessagePosted::getMessage)))
      .putAll(existing)
      .putAll(name, events)
      .build();
  }

  protected static ImmutableSetMultimap<String, MessagePosted> addMessage(
      final ImmutableSetMultimap<String, MessagePosted> existing,
      final ImmutableSet<String> walls,
      final MessagePosted event) {
    final ImmutableSetMultimap.Builder<String, MessagePosted> setMultimapBuilder =
      new ImmutableSetMultimap.Builder<String, MessagePosted>()
        .orderValuesBy(
          Comparator
            .comparing(MessagePosted::getTimestamp, Comparator.reverseOrder())
            .thenComparing(Comparator.comparing(MessagePosted::getName))
            .thenComparing(Comparator.comparing(MessagePosted::getMessage)))
        .putAll(existing);

    walls.stream().forEach(id -> setMultimapBuilder.put(id, event));

    return setMultimapBuilder.build();
  }

  @Override
  public ImmutableSet<MessagePosted> eventsByStreamId(final String streamId) {
    return stream.get(streamId);
  }
}
