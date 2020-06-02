package com.astrocoders.selectabletext;

import android.content.Context;

import com.facebook.react.views.text.ReactTextView;
import com.facebook.react.bridge.ReadableArray;

public class RNSelectableTextView extends ReactTextView {

  private ReadableArray highlights;

  public RNSelectableTextView(Context context) {
    super(context);
  }

  public void setHighlights(ReadableArray highlights) {
    this.highlights = highlights;
  }

  public ReadableArray getHighlights() {
    return this.highlights;
  }
}