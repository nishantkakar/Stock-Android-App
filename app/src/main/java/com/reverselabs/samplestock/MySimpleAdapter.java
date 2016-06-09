package com.reverselabs.samplestock;

import android.content.Context;
import android.widget.Filter;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Nishant on 4/17/2016.
 */
public class MySimpleAdapter extends SimpleAdapter {

    private List<HashMap<String, String>> results;
    private List<String> suggestions=new ArrayList<>();
    public MySimpleAdapter(Context context, List<HashMap<String, String>> data, int resource, String[] from, int[] to) {
        super(context, data, R.layout.autocomplete_layout, from, to);
        this.results = data;
    }

    public int getCount() {
        return suggestions.size();
    }

    @Override
    public String getItem(int index) {
        return suggestions.get(index);
    }
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                JsonParse jp=new JsonParse();
                if (constraint != null) {
                    // A class that queries a web API, parses the data and
                    // returns an ArrayList<GoEuroGetSet>
                    List<SuggestGetSet> new_suggestions =jp.getParseJsonWCF(constraint.toString());
                    suggestions.clear();
                    MainActivity.aList.clear();
                    for (int i=0;i<new_suggestions.size();i++) {
                        suggestions.add(new_suggestions.get(i).getName());
                        HashMap<String, String> hm = new HashMap<String,String>();
                        hm.put("name", new_suggestions.get(i).getName());
                        hm.put("symbol", new_suggestions.get(i).getId() );
                        MainActivity.aList.add(hm);
                    }

                    // Now assign the values and count to the FilterResults
                    // object
                    filterResults.values = suggestions;
                    filterResults.count = suggestions.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence contraint,
                                          FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return myFilter;
    }
}