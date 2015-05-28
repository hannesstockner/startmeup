package com.hannesstockner.startmeup.commands;

import com.hannesstockner.startmeup.Message;

public final class PrintWall extends Message {

  public PrintWall(final String name) {
    super(name);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("PrintWall{");
    sb.append("name='").append(super.getName()).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
