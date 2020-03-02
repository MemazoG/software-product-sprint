package com.google.sps.data;

/** An item on a todo list. */
public final class Task {

  private final long id;
  private final String author;
  private final String comment;
  private final long timestamp;

  public Task(long id, String author, String comment, long timestamp) {
    this.id = id;
    this.author = author;
    this.comment = comment;
    this.timestamp = timestamp;
  }
}