package com.hannesstockner.startmeup.events;

import com.hannesstockner.startmeup.Message;

import java.time.OffsetDateTime;

public final class UserFollowed extends Message {

  private final String following;
  private final OffsetDateTime timestamp;

  public UserFollowed(final String name,
                      final String following,
                      final OffsetDateTime timestamp) {
    super(name);
    this.following = following;
    this.timestamp = timestamp;
  }

  public String getFollowing() {
    return following;
  }

  public OffsetDateTime getTimestamp() {
    return timestamp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserFollowed)) return false;
    if (!super.equals(o)) return false;

    UserFollowed that = (UserFollowed) o;

    if (!following.equals(that.following)) return false;
    return timestamp.equals(that.timestamp);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + following.hashCode();
    result = 31 * result + timestamp.hashCode();
    return result;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("UserFollowed{");
    sb.append("name='").append(super.getName()).append('\'');
    sb.append(", following='").append(following).append('\'');
    sb.append(", timestamp=").append(timestamp);
    sb.append('}');
    return sb.toString();
  }
}
