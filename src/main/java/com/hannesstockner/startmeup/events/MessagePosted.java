package com.hannesstockner.startmeup.events;

import com.hannesstockner.startmeup.Message;

import java.time.OffsetDateTime;

public final class MessagePosted extends Message {

  private final String message;
  private final OffsetDateTime timestamp;

  public MessagePosted(final String name,
                       final String message,
                       final OffsetDateTime timestamp) {
    super(name);
    this.message = message;
    this.timestamp = timestamp;
  }

  public String getMessage() {
    return message;
  }

  public OffsetDateTime getTimestamp() {
    return timestamp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MessagePosted)) return false;
    if (!super.equals(o)) return false;

    MessagePosted that = (MessagePosted) o;

    if (!message.equals(that.message)) return false;
    return timestamp.equals(that.timestamp);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + message.hashCode();
    result = 31 * result + timestamp.hashCode();
    return result;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("MessagePosted{");
    sb.append("name='").append(super.getName()).append('\'');
    sb.append(", message='").append(message).append('\'');
    sb.append(", timestamp=").append(timestamp);
    sb.append('}');
    return sb.toString();
  }
}
