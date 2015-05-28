package com.hannesstockner.startmeup;

/**
 * Parent class for all messages (commands and events).
 */
public abstract class Message {

  private final String name;

  public Message(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Message)) return false;

    Message message = (Message) o;

    return name.equals(message.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
