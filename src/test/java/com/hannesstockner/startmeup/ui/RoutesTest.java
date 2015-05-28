package com.hannesstockner.startmeup.ui;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.hannesstockner.startmeup.Message;
import com.hannesstockner.startmeup.commands.FollowUser;
import com.hannesstockner.startmeup.commands.PostMessage;
import com.hannesstockner.startmeup.commands.PrintTimeline;
import com.hannesstockner.startmeup.commands.PrintWall;
import com.hannesstockner.startmeup.events.MessagePosted;
import com.hannesstockner.startmeup.events.UserFollowed;
import com.hannesstockner.startmeup.helper.ConsumerMock;
import org.junit.Test;

import java.time.OffsetDateTime;

import static org.junit.Assert.*;

public class RoutesTest {

  private final OffsetDateTime dateTime = OffsetDateTime.parse("2015-05-01T10:15:30Z");
  private final Routes routes = new Routes();

  @Test
  public void testPosting() throws Exception {
    final MessagePosted expectedEvent = new MessagePosted("Bob", "hey!", dateTime);

    final ConsumerMock<Message> consumerMock = new ConsumerMock<>();

    Routes.Route<PostMessage> route =
      routes.posting(consumerMock.consumer, () -> dateTime).get();

    assertTrue(route.match("Bob -> hey!"));
    assertFalse(route.match("Bob ->"));
    assertFalse(route.match("Bob -"));

    route.executeIfMatch("Bob -> hey!");

    assertEquals(expectedEvent, consumerMock.getResult());
  }

  @Test
  public void testReading() throws Exception {
    final MessagePosted event = new MessagePosted("Bob", "hey!", dateTime);

    final ImmutableSet<MessagePosted> expectedEvents = ImmutableSet.of(event);

    final ImmutableSetMultimap<String, MessagePosted> stream =
      ImmutableSetMultimap.of("Bob", event);

    final ConsumerMock<ImmutableSet<MessagePosted>> printMock = new ConsumerMock<>();

    Routes.Route<PrintTimeline> route =
      routes.reading(stream::get, printMock.consumer).get();

    assertFalse(route.match("Bob -> hey!"));
    assertFalse(route.match("Bob ->"));
    assertFalse(route.match("Bob -"));
    assertTrue(route.match("Bob"));

    route.executeIfMatch("Bob");

    assertEquals(expectedEvents, printMock.getResult());
  }

  @Test
  public void testFollowing() throws Exception {
    final UserFollowed expectedEvent = new UserFollowed("Bob", "Alice", dateTime);

    final ConsumerMock<Message> consumerMock = new ConsumerMock<>();
    Routes.Route<FollowUser> route =
      routes.following(consumerMock.consumer, () -> dateTime).get();

    assertFalse(route.match("Bob ->"));
    assertFalse(route.match("Bob follows"));
    assertTrue(route.match("Bob follows Alice"));

    route.executeIfMatch("Bob follows Alice");

    assertEquals(expectedEvent, consumerMock.getResult());
  }

  @Test
  public void testWall() throws Exception {
    final MessagePosted event = new MessagePosted("Bob", "hey!", dateTime);

    final ImmutableSet<MessagePosted> expectedEvents = ImmutableSet.of(event);

    final ImmutableSetMultimap<String, MessagePosted> stream =
      ImmutableSetMultimap.of("Bob", event);

    final ConsumerMock<ImmutableSet<MessagePosted>> printMock = new ConsumerMock<>();

    Routes.Route<PrintWall> route =
      routes.wall(stream::get, printMock.consumer).get();

    assertFalse(route.match("Bob -> hey!"));
    assertFalse(route.match("Bob ->"));
    assertFalse(route.match("Bob -"));
    assertFalse(route.match("Bob"));
    assertFalse(route.match("Bob Wall"));
    assertTrue(route.match("Bob wall"));

    route.executeIfMatch("Bob wall");

    assertEquals(expectedEvents, printMock.getResult());
  }
}