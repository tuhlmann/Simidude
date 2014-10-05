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

import org.eclipse.swt.widgets.MenuItem;


/**
 * @version $Revision: 10 $ $Date: 2004-11-17 13:30:10 +0100 (Mi, 17 Nov 2004) $
 * @author tuhlmann
 */
public interface IPluginMenuAction {
    /**
     * execute a menu action.
     * @throws PluginException If something goes wrong
     *
     */
    void run(MenuItem menuItem);
}
