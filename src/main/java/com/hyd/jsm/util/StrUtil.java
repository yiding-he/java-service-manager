package com.hyd.jsm.util;

import java.lang.Character.UnicodeBlock;

public class StrUtil {

  public static int widthOfString(String input) {
    int width = 0;
    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      width += widthOfChar(c);
    }
    return width;
  }

  public static int widthOfChar(char c) {
    if (
      UnicodeBlock.of(c) == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
      || UnicodeBlock.of(c) == UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
      || UnicodeBlock.of(c) == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
      || UnicodeBlock.of(c) == UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
      || UnicodeBlock.of(c) == UnicodeBlock.CJK_STROKES
      || UnicodeBlock.of(c) == UnicodeBlock.CJK_COMPATIBILITY_FORMS
      || UnicodeBlock.of(c) == UnicodeBlock.CJK_RADICALS_SUPPLEMENT
      || UnicodeBlock.of(c) == UnicodeBlock.SMALL_FORM_VARIANTS
    ) {
      return 2;
    } else {
      return 1;
    }
  }

  public static String rightPad(String s, int width) {
    var sb = new StringBuilder(s);
    sb.append(" ".repeat(Math.max(0, width - widthOfString(s))));
    return sb.toString();
  }
}
