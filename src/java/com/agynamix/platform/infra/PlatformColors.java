package com.agynamix.platform.infra;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;


public class PlatformColors {

  private static ColorRegistry colorRegistry = null;
  
  public static final String TRANSFER_TABLE_ALT_COLOR = "transferTableAltColor";
  
  public static final Map<String, RGB> colorMap = new HashMap<String, RGB>();
  
  static {
    colorMap.put(TRANSFER_TABLE_ALT_COLOR, new RGB(251, 255, 223));
  }

  public static Color get(String colordef)
  {
    Color c = getColorRegistry().get(colordef);
    if (c == null)
    {
      RGB rgb = colorMap.get(colordef);
      if (rgb == null)
      {
        throw new IllegalStateException("Color for "+colordef+" not defined.");
      }
      c = new Color(Display.getDefault(), rgb);
      getColorRegistry().put(colordef, c.getRGB());
    }
    return c;
  }
  
  private static ColorRegistry getColorRegistry()
  {
    if (colorRegistry == null)
    {
      colorRegistry = ApplicationBase.getContext().getColorRegistry();
    }
    return colorRegistry;
  }

  private PlatformColors()
  {
  }
}