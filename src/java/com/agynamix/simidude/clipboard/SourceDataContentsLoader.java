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
package com.agynamix.simidude.clipboard;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.agynamix.simidude.remote.ContentsLoaderException;
import com.agynamix.simidude.source.SourceDataContents;
import com.agynamix.simidude.source.impl.FileSourceData;

public class SourceDataContentsLoader {

  private static final int BYTE_CHUNK_SIZE        = 51200;

  final FileSourceData     sourceData;
  final File               itsFileRoot;
  final List<File>         fileList;

  int                      currentFileListIndex   = 0;
  InputStream              currentFileInputStream = null;
  long                     currentFileBytesRead   = 0;
  long                     currentFileSize        = 0;

  boolean                  lastPackageSent        = false;

  public SourceDataContentsLoader(FileSourceData sourceData)
  {
    this.sourceData = sourceData;
    itsFileRoot = new File(sourceData.getFilename());
    if (!itsFileRoot.exists())
    {
      // FIXME: We need robust error handling throughout the application.
      throw new IllegalStateException("File " + itsFileRoot.getAbsolutePath() + " does not exist but should.");
    }
    fileList = listFiles(itsFileRoot);
//    System.out.println("File list:");
//    for (File f : fileList)
//    {
//      System.out.println(f.getAbsolutePath());
//    }
  }

  /**
   * Load the contents of this proxy object into memory.
   * 
   * @return
   * @throws IOException 
   */
  public SourceDataContents loadContents()
  {
    SourceDataContents contents = null;
    if (lastPackageSent)
    {
      return null;
    }
    if (currentFileListIndex >= fileList.size())
    {
      contents = new SourceDataContents(sourceData.getSourceId(), true);
      lastPackageSent = true;
    } else
    {
      File currentFile = fileList.get(currentFileListIndex);
      if (currentFile.isDirectory())
      {
        contents = new SourceDataContents(sourceData.getSourceId(), currentFile.getAbsolutePath());
        currentFileListIndex++;
      } else
      {
        contents = loadFileContents(currentFile);
        if ((contents.isEndOfFile()) || (contents.isAborted()))
        {
          currentFileListIndex++;
        }
      }
    }
    return contents;
  }

  /**
   * Load the contents of a file.
   * 
   * @throws ContentsLoaderException
   * 
   */
  private SourceDataContents loadFileContents(File file)
  {
    SourceDataContents contents = null;
    try {
      byte[] bytes = new byte[BYTE_CHUNK_SIZE]; // our transport buffer
      if (currentFileInputStream == null)
      {
        currentFileInputStream = new BufferedInputStream(new FileInputStream(file));
        currentFileSize = file.length();
        currentFileBytesRead = 0;
      }
      byte[] buffer = bytes;
      int bytesRead = 0;
      if (currentFileSize > 0)
      {
        bytesRead = currentFileInputStream.read(bytes);
        // System.out.println("Read "+bytesRead+" bytes");
        if (bytesRead > -1)
        {
          if (bytesRead < BYTE_CHUNK_SIZE)
          {
            buffer = new byte[bytesRead];
            System.arraycopy(bytes, 0, buffer, 0, bytesRead);
          }
          currentFileBytesRead += bytesRead;
        } else
        {
          System.out.println("Read EOF of " + file.getAbsolutePath());
          buffer = new byte[0];
        }
      } else {
        buffer = new byte[0];
      }
      contents = new SourceDataContents(sourceData.getSourceId(), file.getAbsolutePath(), buffer);
      if ((currentFileBytesRead >= currentFileSize) || (bytesRead < 0))
      {
        currentFileInputStream.close();
        currentFileInputStream = null;
        contents.setEndOfFile(true);
      }
      return contents;
    } catch (IOException e)
    {
      //throw new ContentsLoaderException(file, e);
      contents = new SourceDataContents(sourceData.getSourceId(), file.getAbsolutePath());
      contents.setEndOfFile(true);
      contents.setAborted(new ContentsLoaderException(file, e));      
    }
    return contents;
  }

  /**
   * Creates a flat list of all entries of this file/directory. If fileRoot is just a file the list contains only one
   * entry, otherwise it will contain the flattened down content of the recursive file structure denoted by fileRoot
   * 
   * @param fileRoot
   *          the root entry to check;
   * @return a List of all file/directory entries starting with the file root.
   */
  private List<File> listFiles(File fileRoot)
  {
    List<File> allFilesList = new ArrayList<File>();
    allFilesList.add(fileRoot);
    if (fileRoot.isDirectory())
    {
      File[] includedFiles = fileRoot.listFiles();
      for (File f : includedFiles)
      {
        allFilesList.addAll(listFiles(f));
      }
    }
    return allFilesList;
  }

}
