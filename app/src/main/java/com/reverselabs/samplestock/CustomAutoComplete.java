package com.reverselabs.samplestock;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/** Customizing AutoCompleteTextView to return Country Name
 *  corresponding to the selected item
 */
public class CustomAutoComplete extends AutoCompleteTextView {

    public CustomAutoComplete(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** Returns the country name corresponding to the selected item */
    @Override
    protected CharSequence convertSelectionToString(Object selectedItem) {
        /** Each item in the autocompetetextview suggestion list is a hashmap object */
        //HashMap<String, String> hm = (HashMap<String, String>) selectedItem;
        return selectedItem.toString();
    }
}