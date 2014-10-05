package com.agynamix.platform.icons;

import java.io.InputStream;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.agynamix.platform.infra.ApplicationBase;


public class PlatformIcons {

  private static ImageRegistry imageRegistry = null;
  
  public static final String SIMIDUDE_WND_ICN                        = "Icon_16.png";
  public static final String SIMIDUDE_MAC_ICN                        = "Icon_16_bw.png";

  public static final String HOME                                    = "home_nav.gif";
  public static final String HELP                                    = "linkto_help.gif";
  public static final String EXIT                                    = "exit.gif";
  public static final String SEARCH                                  = "search.gif";
  public static final String TRASH                                   = "trash.gif";
  public static final String TOGGLE_MONITOR_CLIPBOARD                = "toggle_monitor_clipboard.gif";
  public static final String SELECT_MONITORED_CLIPBOARD_ITEMS        = "select_monitored_items.gif";
  public static final String TOGGLE_SHOW_TOOLBAR                     = "show_toolbar.gif";
  public static final String CLEAR_CLIPBOARD                         = "clear_clipboard.gif";
  public static final String NETWORK_CLEAR_CLIPBOARD                 = "network_clear_clipboard.gif";
  public static final String REMOVE_SELECTED_CLIPBOARD_ENTRY         = "remove_selected_clipboard_entry.gif";
  public static final String NETWORK_REMOVE_SELECTED_CLIPBOARD_ENTRY = "network_remove_selected_clipboard_entry.gif";
  public static final String ACTIVATE_TABLE_ENTRY                 = "activate_table_entry.gif";
  public static final String INPUT_TEXT                           = "input_text.gif";
  public static final String DOWNLOAD_CONTENTS_TABLE_ENTRY        = "download_contents.gif";
  public static final String SAVE_CONTENTS_AS_TABLE_ENTRY         = "save_as.gif";
  public static final String SAVE_CONTENTS_COMPRESSED_TABLE_ENTRY = "save_compressed.gif";
  public static final String OPEN_TABLE_ENTRY                     = "open_entry.gif";
  public static final String UPDATE                               = "update.gif";
  public static final String COPY                                 = "copy.gif";
  public static final String CUT                                  = "cut.gif";

  public static final String COLIMG_TEXT                     = "Text-Edit.png";
  public static final String COLIMG_URI                      = "uri.png";
  public static final String COLIMG_FILE                     = "file.png";
  public static final String COLIMG_FOLDER                   = "folder.png";
  public static final String COLIMG_DOWNLOAD_NEEDED_FOLDER   = "folder_needs_download.png";
  public static final String COLIMG_DOWNLOAD_NEEDED_FILE     = "file_needs_download.png";
  public static final String COLIMG_IMAGE_UNDEF              = "undefined.png";
  public static final String DOWNLOAD_NEEDED_OVERLAY         = "download_needed_overlay.gif";

  public static final String SIMIDUDE_LOGO                   = "Simidude_Logo.jpg";

  public static final String BUGZSCOUT_DIALOG                = "bugzscout_dialog.gif";
  public static final String SUBMIT_BUG_REPORT               = "bug_report.gif";



















  public static void disposeAll()
  {
    getImageRegistry().dispose();
  }

  public static Image get(String filename)
  {
    Image image = getImageRegistry().get(filename);
    if (image == null)
    {
      InputStream in = PlatformIcons.class.getResourceAsStream(filename);
      if (in == null)
      {
        return null;
      }
      image = new Image(Display.getCurrent(), in);
      getImageRegistry().put(filename, image);
    }
    return image;
  }
  
  public static ImageDescriptor getDescriptor(String filename)
  {
    ImageDescriptor desc = getImageRegistry().getDescriptor(filename);
    if (desc == null)
    {
      desc = ImageDescriptor.createFromFile(PlatformIcons.class, filename);
      getImageRegistry().put(filename, desc);
    }
    return desc;    
  }

  public static Image getDisabled(String filename)
  {
    return get(filename.replaceFirst("\\.gif", "-d.gif"));
  }
  
  private static ImageRegistry getImageRegistry()
  {
    if (imageRegistry == null)
    {
      imageRegistry = ApplicationBase.getContext().getImageRegistry();
    }
    return imageRegistry;
  }

  private PlatformIcons()
  {
  }
}