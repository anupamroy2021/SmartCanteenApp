package my.anupamroy.smartcanteenapp;

import android.widget.Filter;

import java.util.ArrayList;

import my.anupamroy.smartcanteenapp.adapters.AdapterProductSeller;
import my.anupamroy.smartcanteenapp.adapters.AdapterProductUser;
import my.anupamroy.smartcanteenapp.models.ModelProducts;

public class FilterProductUser extends Filter {

    private AdapterProductUser adapter;
    private ArrayList<ModelProducts> filterList;

    public FilterProductUser(AdapterProductUser adapter, ArrayList<ModelProducts> filterList){
        this.adapter=adapter;
        this.filterList=filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();
        //validate data for search query
        if (constraint!=null && constraint.length()>0){
            //search filled not empty,searching something,perfrom search

            //change to upper case to make case insenitive
            constraint=constraint.toString().toUpperCase();
            //store our filtered list
            ArrayList<ModelProducts> filteredModels =new ArrayList<>();
            for (int i=0;i<filterList.size();i++){
                //check search by title and category
                if (filterList.get(i).getProductTitle().toUpperCase().contains(constraint)||
                        filterList.get(i).getProductCategory().toUpperCase().contains(constraint)){
                    //add filtered data to data list
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count=filteredModels.size();
            results.values=filteredModels;
        }
        else{
            //serach filled empty,not searching, return original/all/complete list

            results.count=filterList.size();
            results.values=filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapter.productsList=(ArrayList<ModelProducts>) filterResults.values;
        //refresh adapter
        adapter.notifyDataSetChanged();
    }
}
