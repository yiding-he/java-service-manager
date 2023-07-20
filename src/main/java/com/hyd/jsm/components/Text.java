package com.hyd.jsm.components;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

public class Text {

  public static Text of(Object obj) {
    return of(String.valueOf(obj));
  }

  public static Text of(String text) {
    Text textObj = new Text();
    textObj.text = text;
    textObj.attributedStyle = AttributedStyle.DEFAULT;
    return textObj;
  }

  private AttributedStyle attributedStyle;

  private String text;

  private Text() {

  }

  public AttributedString value() {
    return new AttributedStringBuilder()
      .style(this.attributedStyle)
      .append(this.text)
      .toAttributedString();
  }

  public String getText() {
    return text;
  }

  public Text bold() {
    this.attributedStyle = this.attributedStyle.bold();
    return this;
  }

  public Text italic() {
    this.attributedStyle = this.attributedStyle.italic();
    return this;
  }

  public Text faint() {
    this.attributedStyle = this.attributedStyle.faint();
    return this;
  }

  public Text underline() {
    this.attributedStyle = this.attributedStyle.underline();
    return this;
  }

  public Text blink() {
    this.attributedStyle = this.attributedStyle.blink();
    return this;
  }

  public Text inverse() {
    this.attributedStyle = this.attributedStyle.inverse();
    return this;
  }

  public Text hidden() {
    this.attributedStyle = this.attributedStyle.hidden();
    return this;
  }

  public Text black() {
    this.attributedStyle = this.attributedStyle.foreground(AttributedStyle.BLACK);
    return this;
  }

  public Text red() {
    this.attributedStyle = this.attributedStyle.foreground(AttributedStyle.RED);
    return this;
  }

  public Text green() {
    this.attributedStyle = this.attributedStyle.foreground(AttributedStyle.GREEN);
    return this;
  }

  public Text yellow() {
    this.attributedStyle = this.attributedStyle.foreground(AttributedStyle.YELLOW);
    return this;
  }

  public Text blue() {
    this.attributedStyle = this.attributedStyle.foreground(AttributedStyle.BLUE);
    return this;
  }

  public Text magenta() {
    this.attributedStyle = this.attributedStyle.foreground(AttributedStyle.MAGENTA);
    return this;
  }

  public Text cyan() {
    this.attributedStyle = this.attributedStyle.foreground(AttributedStyle.CYAN);
    return this;
  }

  public Text white() {
    this.attributedStyle = this.attributedStyle.foreground(AttributedStyle.WHITE);
    return this;
  }

  public Text color(int color) {
    this.attributedStyle = this.attributedStyle.foreground(color);
    return this;
  }
}
