package com.agynamix.platform.infra;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.agynamix.platform.log.ApplicationLog;

public class ZipUtils {
  
  static Logger log = ApplicationLog.getLogger(ZipUtils.class.getName());

  public static synchronized byte[] zipBuffer(String entryName, byte[] buffer)
  {
    try
    {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ZipOutputStream zout = new ZipOutputStream(bos);

      zout.putNextEntry(new ZipEntry(entryName));
      zout.write(buffer);
      zout.closeEntry();
      zout.close();
      return bos.toByteArray();
    } catch (IOException e)
    {
      log.log(Level.WARNING, e.getMessage(), e);
      return null;
    }
  }

  /**
   * Zips a file or directory (recursively) and returns a reference to the
   * created file.
   * 
   * @param filename
   *          Name of the file to create
   * @param file
   *          The parent file to compress
   * @return a reference to the compressed file.
   */
  public static synchronized File zipFile(File inFile, File destFile)
  {
    try
    {
      // Create the ZIP file
      ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destFile));
      
      String commonSourcePath = FileUtils.getCommonSourcePath(inFile.getAbsolutePath());
      
      if (inFile.isDirectory())
      {
        zipDirectory(inFile, commonSourcePath, zos);
      } else {
        zipFileEntry(inFile, commonSourcePath, zos);
      }  
      
      zos.close();
      return destFile;
    } catch (IOException e)
    {
      e.printStackTrace();
      log.log(Level.WARNING, e.getMessage(), e);
      return null;
    }

  }

  public static synchronized void zipDirectory(File zipDir, String commonSourcePath, ZipOutputStream zos) throws IOException
  {
    String[] dirList = zipDir.list();
    if ((dirList == null) || (dirList.length == 0))
    {
      String path = FileUtils.getRelativePath(commonSourcePath, zipDir.getAbsolutePath()) + "/";
      zos.putNextEntry(new ZipEntry(path));
      zos.closeEntry();
    } else {
      for (int i = 0; i < dirList.length; i++)
      {
        File f = new File(zipDir, dirList[i]);
        if (f.isDirectory())
        {
          zipDirectory(f, commonSourcePath, zos);
        } else
        {
          zipFileEntry(f, commonSourcePath, zos);
        }
      }
    }
  }
  
  public static synchronized void zipFileEntry(File zipFile, String commonSourcePath, ZipOutputStream zos) throws IOException
  {
    byte[] readBuffer = new byte[4096];
    int bytesIn = 0;
    FileInputStream fis = new FileInputStream(zipFile);
    
    String path = FileUtils.getRelativePath(commonSourcePath, zipFile.getAbsolutePath());
    zos.putNextEntry(new ZipEntry(path));
    // now write the content of the file to the ZipOutputStream
    while ((bytesIn = fis.read(readBuffer)) > 0)
    {
      zos.write(readBuffer, 0, bytesIn);
    }
    fis.close();
    zos.closeEntry();
  }

  
}
