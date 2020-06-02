package com.astrocoders.selectabletext;

import android.graphics.Rect;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.MotionEvent;
import android.text.Spannable;
import android.text.Selection;
import android.widget.TextView;
import android.view.View;

import java.util.Map;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.text.ReactTextView;
import com.facebook.react.views.text.ReactTextViewManager;
import com.facebook.react.views.text.ReactTextUpdate;

import java.util.List;
import java.util.ArrayList;


public class RNSelectableTextManager extends ReactTextViewManager {
    public static final String REACT_CLASS = "RNSelectableText";
    private ActionMode mActionMode;
    private ReadableArray menuItems;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public ReactTextView createViewInstance(ThemedReactContext context) {
        RNSelectableTextView view = new RNSelectableTextView(context) {
            private Spannable mSpanned;
            private ReadableArray highlights;

            @Override
            public void setText(ReactTextUpdate update) {
                this.mSpanned = update.getText();
                super.setText(update);
            }

            @Override
            public Spannable getSpanned() {
                return this.mSpanned;
            }

            @Override
            public void onAttachedToWindow() {
                if (this.isEnabled()) {
                    this.setEnabled(false);
                    this.setEnabled(true);
                }
                super.onAttachedToWindow();
            }

            @Override
            protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
                if (mActionMode != null) {
                    mActionMode.finish();
                }
                super.onFocusChanged(focused, direction, previouslyFocusedRect);
            }            
        };
        

        return view;
    }

    @ReactProp(name = "menuItems")
    public void setMenuItems(ReactTextView textView, ReadableArray items) {
        this.menuItems = items;
        List < String > result = new ArrayList < String > (items.size());
        for (int i = 0; i < items.size(); i++) {
            result.add(items.getString(i));
        }        

        registerSelectionListener(result.toArray(new String[items.size()]), textView);
    }

    @ReactProp(name = "highlights")
    public void setHighlights(ReactTextView textView, ReadableArray items) {
        RNSelectableTextView rnView = (RNSelectableTextView)textView;
        rnView.setHighlights(items);             
    }

    public void registerSelectionListener(final String[] menuItems, final ReactTextView view) {
        
        view.setCustomSelectionActionModeCallback(new Callback() {
            RNSelectableTextView rnView = (RNSelectableTextView)view;

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Called when action mode is first created. The menu supplied
                // will be used to generate action buttons for the action mode
                // Android Smart Linkify feature pushes extra options into the menu
                // and would override the generated menu items
                menu.clear();

                // boolean hasHighlight = false;

                // int selectionStart = view.getSelectionStart();
                // int selectionEnd = view.getSelectionEnd();
                
                // for (int i = 0; i < highlights.size(); i++) {
                //     final ReadableMap currentItem = highlights.getMap(i);
                //     if (currentItem.getInt("end") >= selectionStart && currentItem.getInt("end") <= selectionEnd) {
                //         hasHighlight = true;
                //         break;
                //     }
                // }
                for (int i = 0; i < menuItems.length; i++) {
                    // String value = hasHighlight && i == 0 ? "Desmarcar" : menuItems[i];
                    menu.add(0, i, 0, menuItems[i]);
                }
                return true;
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {                
                Integer selectionStart = view.getSelectionStart();
                Integer selectionEnd = view.getSelectionEnd();
                ReadableArray highlights = rnView.getHighlights();                
                
                for (int i = 0; i < highlights.size(); i++) {
                    final ReadableMap currentItem = highlights.getMap(i);               

                    if (selectionStart >= currentItem.getInt("start")  && selectionEnd <= currentItem.getInt("end")) {
                        Selection.setSelection((Spannable) view.getText(), currentItem.getInt("start"), currentItem.getInt("end"));
                        return true;
                    }
                }

                Selection.setSelection((Spannable) view.getText(), 0, view.getText().length());
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {}

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                int selectionStart = view.getSelectionStart();
                int selectionEnd = view.getSelectionEnd();
                String selectedText = view.getText().toString().substring(selectionStart, selectionEnd);

                // Dispatch event
                onSelectNativeEvent(view, menuItems[item.getItemId()], selectedText, selectionStart, selectionEnd, "");

                mode.finish();

                return true;
            }

        });
    }

    public void onSelectNativeEvent(ReactTextView view, String eventType, String content, int selectionStart, int selectionEnd, String highlightId) {
        WritableMap event = Arguments.createMap();
        event.putString("eventType", eventType);
        event.putString("content", content);
        event.putInt("selectionStart", selectionStart);
        event.putInt("selectionEnd", selectionEnd);
        event.putString("highlightId", highlightId);

        // Dispatch
        ReactContext reactContext = (ReactContext) view.getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
            view.getId(),
            "topSelection",
            event
        );
    }

    @Override
    public Map getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.builder()
            .put(
                "topSelection",
                MapBuilder.of(
                    "registrationName", "onSelection"))
            .build();
    }
}