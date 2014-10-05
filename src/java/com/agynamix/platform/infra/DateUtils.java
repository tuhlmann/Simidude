/*
 * Copyright by AGYNAMIX(R). All rights reserved. 
 * This file is made available under the terms of the
 * license this product is released under.
 * 
 * For details please see the license file you should have
 * received, or go to:
 * 
 * http://www.agynamix.com
 * 
 * Contributors: agynamix.com (http://www.agynamix.com)
 */
package com.agynamix.platform.infra;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateUtils {
  
  static Map<String, SimpleDateFormat> formatMaps = new HashMap<String, SimpleDateFormat>();

  public static String date2string(String format, Date date)
  {
    return getDateFormatter(formatMaps, format).format(date);
  }

  public static Date string2date(String format, String dateStr)
  {
    try {
      SimpleDateFormat df = getDateFormatter(formatMaps, format);
      Date date = df.parse(dateStr);
      return date;
    } catch (ParseException e) {
      throw new IllegalArgumentException("Cannot parse date: " + dateStr);
    }
  }

  private static SimpleDateFormat getDateFormatter(Map<String, SimpleDateFormat> formatMaps, String format)
  {
    SimpleDateFormat df = formatMaps.get(format);
    if (df == null) {
      df = new SimpleDateFormat(format);
      formatMaps.put(format, df);
    }
    return df;
  }



}
