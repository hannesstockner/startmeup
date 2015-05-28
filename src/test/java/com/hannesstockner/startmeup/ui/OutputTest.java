package com.hannesstockner.startmeup.ui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.hannesstockner.startmeup.events.MessagePosted;
import com.hannesstockner.startmeup.helper.ConsumerMock;
import org.junit.Test;

import java.time.OffsetDateTime;

import static org.junit.Assert.*;

public class OutputTest {

  private final OffsetDateTime dateTime = OffsetDateTime.parse("2015-05-01T10:15:30Z");
  private final OffsetDateTime currentTime = OffsetDateTime.parse("2015-05-01T10:15:35Z");

  @Test
  public void testPrintTimeline() throws Exception {
    final ConsumerMock<String> consumerMock = new ConsumerMock<>();

    final Output output = new Output(() -> currentTime, consumerMock.consumer);

    final MessagePosted event = new MessagePosted("Bob", "hey!", dateTime);
    final MessagePosted event1 = new MessagePosted("Bob", "oh!", dateTime.plusSeconds(1));
    final ImmutableSet<MessagePosted> events = ImmutableSet.of(event1, event);

    output.printTimeline(events);

    assertEquals(
      ImmutableList.of(
        "oh! (4 seconds ago)",
        "hey! (5 seconds ago)"),
      consumerMock.getResults());
  }

  @Test
  public void testPrintWall() throws Exception {
    final ConsumerMock<String> consumerMock = new ConsumerMock<>();

    final Output output = new Output(() -> currentTime, consumerMock.consumer);

    final MessagePosted event = new MessagePosted("Bob", "hey!", dateTime);
    final MessagePosted event1 = new MessagePosted("Ed", "oh!", dateTime.plusSeconds(1));
    final ImmutableSet<MessagePosted> events = ImmutableSet.of(event1, event);

    output.printWall(events);

    assertEquals(
      ImmutableList.of(
        "Ed - oh! (4 seconds ago)",
        "Bob - hey! (5 seconds ago)"),
      consumerMock.getResults());
  }
}