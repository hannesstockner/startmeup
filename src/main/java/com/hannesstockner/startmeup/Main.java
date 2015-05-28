package com.hannesstockner.startmeup;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.hannesstockner.startmeup.streams.TimelineEventStream;
import com.hannesstockner.startmeup.ui.Endpoint;
import com.hannesstockner.startmeup.ui.Output;
import com.hannesstockner.startmeup.streams.WallEventStream;
import com.hannesstockner.startmeup.ui.Routes;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Scanner;
import java.util.function.Supplier;

public class Main {

  public static void main(String[] args) throws Exception {

    //create context and get entry point
    final Endpoint endpoint = createContext();

    Scanner in = new Scanner(System.in);

    while(true) {
      System.out.print("> ");
      String input = in.nextLine();
      endpoint.receive(input);
    }
  }

  private static Endpoint createContext() {
    //time function (provide Supplier for increasing testability)
    final Supplier<OffsetDateTime> toCurrentTime =
      () -> OffsetDateTime.now(Clock.systemUTC());

    //create event bus for event communication
    //MessagePosted and UserFollowed are published and consumed by the streams
    final EventBus eventBus = new EventBus();

    //event streams (
    final TimelineEventStream timelineEventStream =
      new TimelineEventStream(eventBus::register);

    final WallEventStream wallEventStream =
      new WallEventStream(eventBus::register);

    //ui
    final Output output = new Output(toCurrentTime, System.out::println);

    final Routes routes = new Routes();

    //create endpoint with routes (routes are getting everything they need directly)
    return new Endpoint(ImmutableList.of(
      routes.posting(eventBus::post, toCurrentTime).get(),
      routes.reading(timelineEventStream::eventsByStreamId, output::printTimeline).get(),
      routes.following(eventBus::post, toCurrentTime).get(),
      routes.wall(wallEventStream::eventsByStreamId, output::printWall).get()));
  }
}
