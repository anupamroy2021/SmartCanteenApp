package my.anupamroy.smartcanteenapp;

import android.widget.Filter;

import java.util.ArrayList;

import my.anupamroy.smartcanteenapp.adapters.AdapterOrderShop;
import my.anupamroy.smartcanteenapp.adapters.AdapterProductSeller;
import my.anupamroy.smartcanteenapp.models.ModelOrderShop;
import my.anupamroy.smartcanteenapp.models.ModelProducts;

public class FilterOrderShop extends Filter {

    private AdapterOrderShop adapter;
    private ArrayList<ModelOrderShop> filterList;

    public FilterOrderShop(AdapterOrderShop adapter, ArrayList<ModelOrderShop> filterList){
        this.adapter=adapter;
        this.filterList=filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();
        //validate data for search query
        if (constraint!=null && constraint.length()>0){
            //search filled not empty,searching something,perform search

            //change to upper case to make case insensitive
            constraint=constraint.toString().toUpperCase();
            //store our filtered list
            ArrayList<ModelOrderShop> filteredModels =new ArrayList<>();
            for (int i=0;i<filterList.size();i++){
                //check search by title and category
                if (filterList.get(i).getOrderStatus().toUpperCase().contains(constraint)){
                    //add filtered data to data list
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count=filteredModels.size();
            results.values=filteredModels;
        }
        else{
            //search filled empty,not searching, return original/all/complete list

            results.count=filterList.size();
            results.values=filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults results) {
        adapter.orderShopArrayList=(ArrayList<ModelOrderShop>) results.values;
        //refresh adapter
        adapter.notifyDataSetChanged();
    }
}
