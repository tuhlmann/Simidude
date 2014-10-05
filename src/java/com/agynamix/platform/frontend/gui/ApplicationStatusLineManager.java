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
package com.agynamix.platform.frontend.gui;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

public class ApplicationStatusLineManager extends StatusLineManager {
  
  public enum MessageType {error, message};
  
  Image       currentImage       = null;
  String      currentMessage     = null;
  MessageType currentMessageType = null;
  

  public ApplicationStatusLineManager()
  {
    ColorRegistry cr = JFaceResources.getColorRegistry();
    cr.put(JFacePreferences.ERROR_COLOR, new RGB (205, 2, 4)); // schoen rot
  }
    
  private void setCurrentMessage(Image image, String message)
  {
//    System.out.println("set message"+message);
//    Exception e = new Exception();
//    e.printStackTrace();
    currentImage = image;
    currentMessage = message;
    currentMessageType = MessageType.message;    
  }
  
  private void setCurrentError(Image image, String message)
  {
//    System.out.println("set error "+message);
//    Exception e = new Exception();
//    e.printStackTrace();
    currentImage = image;
    currentMessage = message;
    currentMessageType = MessageType.error;    
  }
  
  @Override
  public void setErrorMessage(Image image, String message)
  {
    setCurrentError(image, message);
    super.setErrorMessage(image, message);
  }
  
  @Override
  public void setErrorMessage(String message)
  {
    setCurrentError(null, message);
    super.setErrorMessage(message);
    JFaceColors.getErrorText(null);
  }
  
  @Override
  public void setMessage(Image image, String message)
  {
    setCurrentMessage(image, message);
    super.setMessage(image, message);
  }
  
  
  @Override
  public void setMessage(String message)  
  {    
    setCurrentMessage(null, message);
    super.setMessage(message);
  }

  public void restoreStatusLine()
  {
//    System.out.println("Restore "+currentMessage);
    if (currentMessageType != null)
    {
      switch (currentMessageType)
      {
        case message:
          if (currentImage != null)
          {
            super.setMessage(currentImage, currentMessage);
          } else {
            super.setMessage(currentMessage);
          }
          break;
        case error:
          if (currentImage != null)
          {
            super.setErrorMessage(currentImage, currentMessage);
          } else {
            super.setErrorMessage(currentMessage);
          }
          break;
      }
    }
    
  }

}
