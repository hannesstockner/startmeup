package com.hannesstockner.startmeup.ui;

import com.google.common.collect.ImmutableSet;
import com.hannesstockner.startmeup.Message;
import com.hannesstockner.startmeup.commands.FollowUser;
import com.hannesstockner.startmeup.commands.PrintTimeline;
import com.hannesstockner.startmeup.commands.PrintWall;
import com.hannesstockner.startmeup.commands.PostMessage;
import com.hannesstockner.startmeup.events.MessagePosted;
import com.hannesstockner.startmeup.events.UserFollowed;

import java.time.OffsetDateTime;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains Route definitions which can be used by endpoints.
 *
 * Every definition returns a Supplier of a Route which allows to pass in all the
 * necessary dependencies straight to them (partially applied function).
 *
 * By injecting functional interfaces the dependencies are made explicit.
 */
public class Routes {

  /**
   * Route definition for posting.
   *
   * If string input matches the pattern it generates a PostMessage command.
   * The handler creates a MessagesPosted event based on the command and publishes it.
   */
  public Supplier<Route<PostMessage>> posting(
      final Consumer<Message> eventPublisher,
      final Supplier<OffsetDateTime> toCurrentTime) {
    return () ->
        new Route<>(
          Pattern.compile("(\\w+)\\s(->)\\s(.+)"),
          m -> new PostMessage(
            m.group(1), m.group(3)),
          command -> eventPublisher.accept(
            new MessagePosted(
              command.getName(), command.getMessage(), toCurrentTime.get())));
  }

  /**
   * Route definition for reading.
   *
   * If string input matches the pattern it generates a PrintTimeline command.
   * The handler fetches MessagePosted events for a timeline and mandates to print them.
   */
  public Supplier<Route<PrintTimeline>> reading(
      final Function<String, ImmutableSet<MessagePosted>> toEvents,
      final Consumer<ImmutableSet<MessagePosted>> print) {
    return () ->
        new Route<>(
          Pattern.compile("\\w+"),
          m -> new PrintTimeline(m.group(0)),
          command -> print.accept(toEvents.apply(command.getName())));
  }

  /**
   * Route definition for following.
   *
   * If string input matches the pattern it generates a FollowUser command.
   * The handler creates a UserFollowed event based on the command and publishes it.
   */
  public Supplier<Route<FollowUser>> following(
      final Consumer<Message> eventPublisher,
      final Supplier<OffsetDateTime> toCurrentTime) {
    return () ->
        new Route<>(
          Pattern.compile("(\\w+)\\s(follows)\\s(\\w+)"),
          m -> new FollowUser(
            m.group(1), m.group(3)),
          command -> eventPublisher.accept(
            new UserFollowed(
              command.getName(), command.getFollowing(), toCurrentTime.get())));
  }

  /**
   * Route definition for wall.
   *
   * If string input matches the pattern it generates a PrintWall command.
   * The handler fetches MessagePosted events for a wall and mandates to print them.
   */
  public Supplier<Route<PrintWall>> wall(
      final Function<String, ImmutableSet<MessagePosted>> toEvents,
      final Consumer<ImmutableSet<MessagePosted>> print) {
    return () ->
      new Route<>(
        Pattern.compile("(\\w+)\\s(wall)"),
        m -> new PrintWall(m.group(1)),
        command -> print.accept(toEvents.apply(command.getName())));
  }

  /**
   * Defines a single route.
   *
   * This includes:
   * - the regex pattern of the route
   * - a message factory which transforms the string input into a command
   * - a handler which handles the command
   */
  public class Route<T> {

    private final Pattern pattern;
    private final Function<Matcher, T> toCommand;
    private final Consumer<T> handler;

    public Route(final Pattern pattern,
                 final Function<Matcher, T> toCommand,
                 final Consumer<T> handler) {
      this.pattern = pattern;
      this.toCommand = toCommand;
      this.handler = handler;
    }

    public boolean match(final String input) {
      return input.matches(pattern.pattern());
    }

    public void executeIfMatch(final String input) {
      Matcher m = pattern.matcher(input);
      if (m.matches()) {
        handler.accept(toCommand.apply(m));
      }
    }
  }
}
