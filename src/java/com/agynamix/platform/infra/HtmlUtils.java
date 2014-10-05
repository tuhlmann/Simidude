package com.agynamix.platform.infra;

public class HtmlUtils {

  public static String escapeHtmlFull(String s)
  {
    StringBuilder b = new StringBuilder(s.length());
    for (int i = 0; i < s.length(); i++)
    {
      char ch = s.charAt(i);
      if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9')
      {
        // safe
        b.append(ch);
      } else if (Character.isWhitespace(ch))
      {
        // paranoid version: whitespaces are unsafe - escape
        // conversion of (int)ch is naive
        b.append("&#").append((int) ch).append(";");
      } else if (Character.isISOControl(ch))
      {
        // paranoid version:isISOControl which are not isWhitespace removed !
        // do nothing do not include in output !
      } else if (Character.isHighSurrogate(ch))
      {
        int codePoint;
        if (i + 1 < s.length() && Character.isSurrogatePair(ch, s.charAt(i + 1))
            && Character.isDefined(codePoint = (Character.toCodePoint(ch, s.charAt(i + 1)))))
        {
          b.append("&#").append(codePoint).append(";");
        } else
        {
          System.out.println("bug:isHighSurrogate");
        }
        i++; // in both ways move forward
      } else if (Character.isLowSurrogate(ch))
      {
        // wrong char[] sequence, //TODO: LOG !!!
        System.out.println("bug:isLowSurrogate");
        i++; // move forward,do nothing do not include in output !
      } else
      {
        if (Character.isDefined(ch))
        {
          // paranoid version
          // the rest is unsafe, including <127 control chars
          b.append("&#").append((int) ch).append(";");
        }
        // do nothing do not include undefined in output!
      }
    }
    return b.toString();
  }

}
