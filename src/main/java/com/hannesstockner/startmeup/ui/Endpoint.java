package com.hannesstockner.startmeup.ui;

import com.google.common.collect.ImmutableList;

/**
 * Entry point for all user input.
 *
 * Handles the input by delivering it to the responsible Route (based on regex).
 * Routes are already setup with all dependencies they need to work properly and
 * process the input (no dependency passing inside the Endpoint necessary).
 */
public class Endpoint {

  private final ImmutableList<Routes.Route> routes;

  public Endpoint(final ImmutableList<Routes.Route> routes) {
    this.routes = routes;
  }

  public void receive(final String input) {
    final String trimmed = input.trim();
    routes
      .stream()
      .filter(r -> r.match(trimmed))
      .findFirst()
      .ifPresent(r -> r.executeIfMatch(trimmed));
  }

}
