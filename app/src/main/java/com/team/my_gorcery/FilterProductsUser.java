package com.team.my_gorcery;

import android.widget.Filter;

import com.team.my_gorcery.adapters.AdapterProductSeller;
import com.team.my_gorcery.adapters.AdapterProductUser;
import com.team.my_gorcery.model.ModelProduct;

import java.util.ArrayList;

public class FilterProductsUser extends Filter {

    private AdapterProductUser adapter;
    private ArrayList<ModelProduct> filterList;

    public FilterProductsUser(AdapterProductUser adapter, ArrayList<ModelProduct> filterList) {
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
            ArrayList<ModelProduct> filteredModels = new ArrayList<>();
            for(int i=0; i<filterList.size(); i++){
                // Check search by title and category
                if(filterList.get(i).getProductName().toUpperCase().contains(constraint) ||
                        filterList.get(i).getProductCategory().toUpperCase().contains(constraint) ){
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
        adapter.productList = (ArrayList<ModelProduct>) results.values;
        //Refresh adapter
        adapter.notifyDataSetChanged();


    }
}
