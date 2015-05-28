package com.hannesstockner.startmeup.ui;

import com.google.common.collect.ImmutableSet;
import com.hannesstockner.startmeup.events.MessagePosted;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.JustNow;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Prints the timeline and wall.
 */
public class Output {

  private final Supplier<OffsetDateTime> toCurrentTime;
  private final Consumer<String> out;

  public Output(final Supplier<OffsetDateTime> toCurrentTime,
                final Consumer<String> out) {
    this.toCurrentTime = toCurrentTime;
    this.out = out;
  }

  public void printTimeline(final ImmutableSet<MessagePosted> events) {
    final PrettyTime pt = prettyTime();
    events
      .stream()
      .map(e ->
        e.getMessage()
          + " (" + pt.format(Date.from(e.getTimestamp().toInstant())) + ")")
      .forEach(out);
  }

  public void printWall(final ImmutableSet<MessagePosted> events) {
    final PrettyTime pt = prettyTime();
    events
      .stream()
      .map(e -> e.getName()
        + " - " + e.getMessage()
        + " (" + pt.format(Date.from(e.getTimestamp().toInstant())) + ")")
      .forEach(out);
  }

  private PrettyTime prettyTime() {
    PrettyTime prettyTime = new PrettyTime(
      Date.from(toCurrentTime.get().toInstant()), Locale.ENGLISH);
    prettyTime.getUnit(JustNow.class).setMaxQuantity(0L);
    return prettyTime;
  }
}
