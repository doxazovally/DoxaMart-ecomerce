package com.team.my_gorcery;

import android.widget.Filter;

import com.team.my_gorcery.adapters.AdapterOrderShop;
import com.team.my_gorcery.model.ModelOrderShop;

import java.util.ArrayList;

public class FilterOrderShop extends Filter {

    private AdapterOrderShop adapter;
    private ArrayList<ModelOrderShop> filterList;

    public FilterOrderShop(AdapterOrderShop adapter, ArrayList<ModelOrderShop> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        // Validate data for search query
        if(constraint != null && constraint.length() > 0){
            // Search field not empty, searching something

            // Change upper case to make case insensitive
            constraint = constraint.toString().toUpperCase();

            // Store our filtered list
            ArrayList<ModelOrderShop> filteredModels = new ArrayList<>();
            for(int i=0; i<filterList.size(); i++){
                // Check search by title and category
                if(filterList.get(i).getOrderStatus().toUpperCase().contains(constraint)){
                    //Add filtered to list
                    filteredModels.add(filterList.get(i));

                }
            }

            results.count = filteredModels.size();
            results.values = filteredModels;

        }
        else {
            // Search field empty, not searching, return complete and original list
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.orderShopList = (ArrayList<ModelOrderShop>) results.values;
        //Refresh adapter
        adapter.notifyDataSetChanged();


    }
}
