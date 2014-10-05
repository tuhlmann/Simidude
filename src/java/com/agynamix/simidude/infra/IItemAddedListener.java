package com.agynamix.simidude.infra;

import com.agynamix.simidude.clipboard.IClipboardItem;

public interface IItemAddedListener {

  void itemAdded(int insertPos, IClipboardItem item);

}
