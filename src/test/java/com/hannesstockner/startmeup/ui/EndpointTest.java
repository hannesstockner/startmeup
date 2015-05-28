package com.hannesstockner.startmeup.ui;

import com.google.common.collect.ImmutableList;
import com.hannesstockner.startmeup.helper.ConsumerMock;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class EndpointTest {

  @Test
  public void testReceive() throws Exception {
    final ConsumerMock<String> consumerMock = new ConsumerMock<>();

    Routes.Route route = new Routes().new Route<>(
      Pattern.compile("\\w+"),
        m -> m.group(0),
       consumerMock.consumer);

    Routes.Route route1 = new Routes().new Route<>(
      Pattern.compile("(\\w+)\\s(wall)"),
        m -> m.group(0),
       consumerMock.consumer);

    final Endpoint endpoint = new Endpoint(ImmutableList.of(route, route1));

    endpoint.receive("Alice wall");

    assertEquals("Alice wall", consumerMock.getResult());
  }
}