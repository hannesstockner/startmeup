package com.hannesstockner.startmeup.commands;

import com.hannesstockner.startmeup.Message;

public final class PostMessage extends Message {

  private final String message;

  public PostMessage(final String name,
                     final String message) {
    super(name);
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PostMessage)) return false;
    if (!super.equals(o)) return false;

    PostMessage that = (PostMessage) o;

    return message.equals(that.message);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + message.hashCode();
    return result;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("PostMessage{");
    sb.append("name='").append(super.getName()).append('\'');
    sb.append(", message='").append(message).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
