package com.hannesstockner.startmeup.commands;

import com.hannesstockner.startmeup.Message;

public final class FollowUser extends Message {

  private final String following;

  public FollowUser(final String name,
                    final String following) {
    super(name);
    this.following = following;
  }

  public String getFollowing() {
    return following;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FollowUser)) return false;
    if (!super.equals(o)) return false;

    FollowUser that = (FollowUser) o;

    return following.equals(that.following);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + following.hashCode();
    return result;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("FollowUser{");
    sb.append("name='").append(super.getName()).append('\'');
    sb.append(", following='").append(following).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
