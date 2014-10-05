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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

import com.agynamix.platform.log.ApplicationLog;
import com.agynamix.simidude.impl.ContentsCacheInfo;
import com.agynamix.simidude.infra.CacheManagerFactory;
import com.agynamix.simidude.source.impl.ImageSourceData;
import com.agynamix.simidude.source.impl.TextSourceData;

public class FileUtils {
  
  static Logger log = ApplicationLog.getLogger(FileUtils.class);

  public final static String[] imageFileExtensions = new String[] { "*.jpg", "*.png", "*.bmp", "*.ico" };
  public final static String[] textFileExtensions  = new String[] { "*.txt" };
  public final static String[] zipExtension        = new String[] { "*.zip" };

  public static final String DEFAULT_TEXT_FILE_EXTENSION  = ".txt";
  public static final String DEFAULT_IMAGE_FILE_EXTENSION = ".jpg"; 
  public static final String DEFAULT_ZIP_EXTENSION        = ".zip";

  /**
   * This function will copy files or directories from one location to another. note that the source and the destination
   * must be mutually exclusive. This function can not be used to copy a directory to a sub directory of itself. The
   * function will also have problems if the destination files already exist.
   * 
   * @param src
   *          -- A File object that represents the source for the copy
   * @param dest
   *          -- A File object that represents the destination for the copy.
   * @throws IOException
   *           if unable to copy.
   */
  public static void copyRecursive(File src, File dest) throws IOException
  {
    // Check to ensure that the source is valid...
    if (!src.exists())
    {
      throw new IOException("copyFiles: Can not find source: " + src.getAbsolutePath() + ".");
    } else if (!src.canRead())
    { // check to ensure we have rights to the source...
      throw new IOException("copyFiles: No right to source: " + src.getAbsolutePath() + ".");
    }
    // is this a directory copy?
    if (src.isDirectory())
    {
      if (!dest.exists())
      { // does the destination already exist?
        // if not we need to make it exist if possible (note this is mkdirs not mkdir)
        if (!dest.mkdirs())
        {
          throw new IOException("copyFiles: Could not create direcotry: " + dest.getAbsolutePath() + ".");
        }
      }
      // get a listing of files...
      String list[] = src.list();
      // copy all the files in the list.
      for (int i = 0; i < list.length; i++)
      {
        File dest1 = new File(dest, list[i]);
        File src1 = new File(src, list[i]);
        copyRecursive(src1, dest1);
      }
    } else
    {
      // This was not a directory, so lets just copy the file
      FileInputStream fin = null;
      FileOutputStream fout = null;
      byte[] buffer = new byte[4096]; // Buffer 4K at a time (you can change this).
      int bytesRead;
      try
      {
        // open the files for input and output
        fin = new FileInputStream(src);
        fout = new FileOutputStream(dest);
        // while bytesRead indicates a successful read, lets write...
        while ((bytesRead = fin.read(buffer)) >= 0)
        {
          fout.write(buffer, 0, bytesRead);
        }
      } catch (IOException e)
      { // Error copying file...
        IOException wrapper = new IOException("copyFiles: Unable to copy file: " + src.getAbsolutePath() + " to "
            + dest.getAbsolutePath() + ".");
        wrapper.initCause(e);
        wrapper.setStackTrace(e.getStackTrace());
        throw wrapper;
      } finally
      { // Ensure that the files are closed (if they were open).
        if (fin != null)
        {
          fin.close();
        }
        if (fout != null)
        {
          fout.close();
        }
      }
    }
  }

  /**
   * Copies a file
   * 
   * @param in source file
   * @param out target file
   * @return boolean on success
   */
  public static synchronized boolean copyFile(final InputStream in, final OutputStream out) {
      try {
          byte[] buf = new byte[1024];
          int len;
          while ((len = in.read(buf)) > 0) {
              out.write(buf, 0, len);
          }
          in.close();
          out.close();
          return true;
      } catch (final Exception e) {
          log.log(Level.SEVERE, "Failed copying file: " + in + " -> " + out, e);
          return false;
      }
  }
  
  public static synchronized boolean copyFile(final String in, final String out) {
      try {
          File outFile = new File(out);
          if (!outFile.exists()) {
              if (!outFile.createNewFile()) {
                  return false;
              }
          }
          return copyFile(ClassLoader.getSystemClassLoader()
                  .getResourceAsStream(in), 
                  new FileOutputStream(outFile));
      } catch (final Exception e) {
          log.log(Level.CONFIG, "Failed copying file: " + in + " -> " + out, e);
          return false;
      }
  }
 
  
  /**
   * Writes a textstring to a file
   * @param f teh file to write to
   * @param text the text buffer to write
   * @param compress true if the contents should be compressed prior to writing.
   * @throws IOException
   */
  public static void writeTextToFile(File f, String entryName, String text, boolean compress) throws IOException
  {
    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
    if (compress)
    {
      byte[] buffer = ZipUtils.zipBuffer(entryName, text.getBytes());
      out.write(buffer);
    } else {
      out.write(text.getBytes());
    }
    out.close();
  }

  private void writeFile(File f, byte[] bytes)
  {
    try
    {
      BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(f));
      os.write(bytes);
      os.close();
    } catch (IOException e)
    {
      log.log(Level.WARNING, e.getMessage(), e); 
    }
  }

  public static void writeImageToFile(File f, String entryName, ImageData imageData, boolean compress) throws IOException
  {
    int imageType = getImageTypeFromFilename(entryName);
    byte[] imgBuffer = readImage(imageData, imageType);

    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
    if (compress)
    {
      byte[] bufferZipped = ZipUtils.zipBuffer(entryName, imgBuffer);
      out.write(bufferZipped);
    } else {
      out.write(imgBuffer);
    }
    out.close();    
  }
  
  public static byte[] readImage(ImageData imgData, int imgFormat)
  {
    ImageLoader imageLoader = new ImageLoader();
    imageLoader.data = new ImageData[] { imgData };
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    imageLoader.save(bos, imgFormat);
    
    byte[] buf = bos.toByteArray();
    try
    {
      bos.close();
    } catch (IOException IGNORE) { }

    return buf;
  }

  public static byte[] loadFile(InputStream inStream, String symbolicName)
  {
    if (inStream == null) {
      throw new IllegalStateException("Resource " + symbolicName + " not found.");
    }
    try {
      byte[] buffer = new byte[inStream.available()];
      inStream.read(buffer);
      inStream.close();
      return buffer;
    } catch (IOException e) {
      throw new IllegalStateException("Could not read file " + symbolicName + ": " + e.getMessage());
    }        
  }

  private static int getImageTypeFromFilename(String filename)
  {
    int pos = filename.lastIndexOf(".");
    if ((pos > -1) && (pos < filename.length() - 1))
    {
      String ext = filename.substring(pos + 1).toLowerCase();
      if ((ext.equals("jpg")) || (ext.equals("jpeg")) || (ext.equals("zip")))
      {
        return SWT.IMAGE_JPEG;
      } else if (ext.equals("png"))
      {
        return SWT.IMAGE_PNG;
      } else if ((ext.equals("tif")) || (ext.equals("tiff")))
      {
        return SWT.IMAGE_TIFF;
      } else if (ext.equals("bmp"))
      {
        return SWT.IMAGE_BMP;
      } else if (ext.equals("gif"))
      {
        return SWT.IMAGE_GIF;
      } else if (ext.equals("ico"))
      {
        return SWT.IMAGE_ICO;
      }
    }
    return SWT.IMAGE_COPY;
  }

  public static String[] getTextFileExtensions(boolean compress)
  {
    if (compress)
    {
      return zipExtension;
    } else {
      return textFileExtensions;      
    }
  }

  public static String[] getImageFileExtensions()
  {
    return imageFileExtensions;
  }

//  private static String[] appendExtension(String[] extensions, String extToAppend)
//  {
//    String[] re = new String[extensions.length];
//    for (int i = 0; i < extensions.length; i++)
//    {
//      re[i] = extensions[i] + extToAppend;
//    }
//    return re;
//  }

  public static File writeTextToTempFile(TextSourceData tsd) throws IOException
  {
    File f = null;
    if (!tsd.isCached())
    {
      f = CacheManagerFactory.createTempFile(".txt");
      writeTextToFile(f, "ClipboardText.txt", tsd.getText(), false);
      f.setReadOnly();
      tsd.setContentsCacheInfo(new ContentsCacheInfo(new Date(), f));
    } else {
      f = new File(tsd.getContentsCacheInfo().getFilenameInCache());
    }
    return f;
  }

  public static File writeImageToTempFile(ImageSourceData isd) throws IOException
  {
    File f = null;
    if (!isd.isCached())
    {
      f = CacheManagerFactory.createTempFile(".jpg");
      writeImageToFile(f, "ClipboardImage.jpg", isd.getImageData(), false);
      f.setReadOnly();
      isd.setContentsCacheInfo(new ContentsCacheInfo(new Date(), f));
    } else {
      f = new File(isd.getContentsCacheInfo().getFilenameInCache());
    }
    return f;
  }

  /**
   * This function will recursivly delete directories and files.
   * 
   * @param path
   *          File or Directory to be deleted
   * @return true indicates success.
   */  
  public static boolean deleteRecursive(File path)
  {
    if ((path.exists()) && (path.isDirectory()))
    {
      File[] files = path.listFiles();
      if (files != null)
      {
        for (int i = 0; i < files.length; i++)
        {
          if (files[i].isDirectory())
          {
            deleteRecursive(files[i]);
          } else
          {
            files[i].delete();
          }
        }
      }
    }
    return (path.delete());
  }
  
  public static String replaceLastExtension(String filename, String extension)
  {
    if (filename == null) return null;
    
    String re;
    
    int pos = filename.lastIndexOf(".");
    if (pos > -1)
    {
      re = filename.substring(0, pos) + extension;
    } else {
      re = filename + extension;
    }
    return re;
  }
  
  /**
   * Java on windows will change the slashes back to backslashes, so we replace again.
   * @param name the file path
   * @return the file path with backslashes replaced by slashes.
   */
  public static String getCommonSourcePath(String name)
  {
    File f = new File(name.replace('\\', '/'));
    String s = f.getParent();
    if (s == null)
    {
      s = "";
    }
    return s.replace('\\', '/');
  }

  /**
   * Extracts the part of the file name that needs to be written into the cache.
   * 
   * @param commonSourcePath
   *          the part of the name that is replaced by the cache directory name.
   * @param name
   *          the complete path to a file
   * @return the name part of the file
   */
  public static String getRelativePath(String commonSourcePath, String name)
  {
    // File f = new File();
    String nameReplaced = name.replace('\\', '/');
    if (!nameReplaced.startsWith(commonSourcePath))
    {
      log.config("The file " + name + " does not start with " + commonSourcePath);
      return nameReplaced;
    }
    String sub = nameReplaced.substring(commonSourcePath.length());
    if (sub.length() > 1)
    {
      if (sub.charAt(0) == '/')
      {
        sub = sub.substring(1);
      }
    }
    return sub;
  }

  /**
   * Serialize an Object to a file.
   * @param item
   * @param applicationDataDir
   * @param savedClpItemFileName
   */
  public static void serialize(Object obj, String filename)
  {
    try {
      FileOutputStream fout = new FileOutputStream(filename);
      ObjectOutputStream oos = new ObjectOutputStream(fout);
      oos.writeObject(obj);
      oos.close();
    } catch (Exception e) 
    { 
      log.log(Level.WARNING, e.getMessage(), e); 
    }
  }

  /**
   * Deserialize an object which was serialized before.
   * @param filename the file that is supposed to hold the serialized object
   * @return the object from the file or null if the file could not be found or read.
   */
  public static Object deserialize(String filename)
  {
    try {
      FileInputStream fin = new FileInputStream(filename);
      ObjectInputStream ois = new ObjectInputStream(fin);
      Object obj = ois.readObject();
      ois.close();
      return obj;
    } catch (Exception e) 
    { 
      log.log(Level.WARNING, e.getMessage(), e); 
    }
    return null;
  }

// Nicht fertig entwickelt.
//  /**
//   * Checks if the given file has an extension added. If not we add it here.
//   * @param file the file to check.
//   * @param extension the extension to add in case there is none. An extension is marked by a '.'.
//   * @return the old file if nothing was changed or a new File instance with the extension added.
//   */
//  public static File checkForMissingExtension(File file, String extension)
//  {
//    String filename = file.getAbsolutePath();
//    int len = filename.length();
//    int pos = filename.lastIndexOf(".");
//    if (pos > (len - 5))
//    {
//      return file;
//    }
//        
//    return new File()
//  }




}
