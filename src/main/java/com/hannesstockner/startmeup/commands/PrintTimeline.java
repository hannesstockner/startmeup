package com.hannesstockner.startmeup.commands;

import com.hannesstockner.startmeup.Message;

public final class PrintTimeline extends Message {

  public PrintTimeline(final String name) {
    super(name);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("PrintTimeline{");
    sb.append("name='").append(super.getName()).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
