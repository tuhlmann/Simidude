/*
 * Copyright (c) 2004 agynamiX.com. All rights reserved. 
 * This file is made available under the terms of the
 * license this product is released under.
 * 
 * For details please see the license file you should have
 * received, or go to:
 * 
 * http://www.agynamix.com
 * 
 * Contributors: agynamiX.com (http://www.agynamix.com)
 */
package com.agynamix.platform.infra;

import java.util.Properties;

import org.eclipse.jface.util.IPropertyChangeListener;


/**
 * @version $Revision: 10 $ $Date: 2004-11-17 13:30:10 +0100 (Mi, 17 Nov 2004) $
 * @author tuhlmann
 * @since V0404
 */
public interface IPreferenceConfigAdapter {
  
  Properties loadDefaultProperties();
  Properties loadProperties();
  
  void saveProperties(Properties p);
  
  void addPropertyChangeListener(IPropertyChangeListener listener);
  void removePropertyChangeListener(IPropertyChangeListener listener);


}

