package my.anupamroy.smartcanteenapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import my.anupamroy.smartcanteenapp.FilterProductUser;
import my.anupamroy.smartcanteenapp.R;
import my.anupamroy.smartcanteenapp.activities.ShopDetailsActivity;
import my.anupamroy.smartcanteenapp.models.ModelProducts;
import my.anupamroy.smartcanteenapp.models.ModelShop;
import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterProductUser extends RecyclerView.Adapter<AdapterProductUser.HolderProductUser> implements Filterable {

    private Context context;
    public ArrayList<ModelProducts> productsList,filterList;
    private FilterProductUser filter;

    public AdapterProductUser(Context context, ArrayList<ModelProducts> productsList) {
        this.context = context;
        this.productsList = productsList;
        this.filterList = productsList;

    }

    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view= LayoutInflater.from(context).inflate(R.layout.row_product_user,parent,false);
        return new HolderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductUser holder, int position) {
        //get data
        final ModelProducts modelProducts=productsList.get(position);
        String productCategory=modelProducts.getProductCategory();
        String originalPrice=modelProducts.getOriginalPrice();
        String productTitle=modelProducts.getProductTitle();
        String productDescription=modelProducts.getProductDescription();
        String productQuantity=modelProducts.getProductQuantity();
        String productId=modelProducts.getProductId();
        String timestamp=modelProducts.getTimestamp();
        String productIcon=modelProducts.getProductIcon();

        //set data
        holder.titleTv.setText(productTitle);
        holder.originalPriceTv.setText("₹"+originalPrice);

        try {
            Picasso.get().load(productIcon).placeholder(R.drawable.ic_store_gray).into(holder.productIconIv);
        }
        catch (Exception e){
            holder.productIconIv.setImageResource(R.drawable.ic_store_gray);
        }
        holder.addToCartTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add product to cart
                showQuantityDialog(modelProducts);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show product details
            }
        });
    }
    private double cost=0;
    private double finalCost=0;
    private int quantity=0;

    private void showQuantityDialog(ModelProducts modelProducts) {
        //inflate layout for dialog
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_quantity,null);
        //init layout views
        ImageView productIv=view.findViewById(R.id.productIv);
        TextView titleTv=view.findViewById(R.id.titleTv);
        TextView pQuantity=view.findViewById(R.id.pQuantityTv);
        TextView originalPriceTv=view.findViewById(R.id.originalPriceTv);
        TextView finalPriceTv=view.findViewById(R.id.finalPriceTv);
        ImageButton decrementBtn=view.findViewById(R.id.decrementBtn);
        TextView quantityTv=view.findViewById(R.id.quantityTv);
        ImageButton incrementBtn=view.findViewById(R.id.incrementBtn);
        Button continueBtn=view.findViewById(R.id.continueBtn);

        //get data from model
        String productId=modelProducts.getProductId();
        String title=modelProducts.getProductTitle();
        String productQuantity=modelProducts.getProductQuantity();
        String image=modelProducts.getProductIcon();

        String price=modelProducts.getOriginalPrice();

        cost=Double.parseDouble(price.replaceAll("₹",""));
        finalCost=Double.parseDouble(price.replaceAll("₹",""));
        quantity=1;

        //dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(view);

        //set data
        try{
            Picasso.get().load(image).placeholder(R.drawable.ic_cart_gray).into(productIv);
        }
        catch (Exception e){
            productIv.setImageResource(R.drawable.ic_cart_gray);
        }
        titleTv.setText(""+title);
        pQuantity.setText(""+productQuantity);
        quantityTv.setText(""+quantity);
        originalPriceTv.setText("₹"+modelProducts.getOriginalPrice());
        finalPriceTv.setText("₹"+finalCost);

        AlertDialog dialog=builder.create();
        dialog.show();

        //increase quantity of the product
        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalCost=finalCost+cost;
                quantity++;

                finalPriceTv.setText("₹"+finalCost);
                quantityTv.setText(""+quantity);
            }
        });
        //decrease quantity of product,only if quantity>1
        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity>1){
                    finalCost=finalCost-cost;
                    quantity--;

                    finalPriceTv.setText("₹"+finalCost);
                    quantityTv.setText(""+quantity);
                }
            }
        });
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title=titleTv.getText().toString().trim();
                String priceEach=price;
                String totalPrice=finalPriceTv.getText().toString().trim().replace("₹","");
                String quantity=quantityTv.getText().toString().trim();

                //add to db(SQLite)
                addToCart(productId,title,priceEach,totalPrice,quantity);
                dialog.dismiss();

            }
        });

    }
    private int itemId=1;
    private void addToCart(String productId, String title, String priceEach, String price, String quantity) {
        itemId++;

        EasyDB easyDB= EasyDB.init(context,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text","not null"}))
                .doneTableColumn();

        Boolean b=easyDB.addData("Item_Id",itemId)
                .addData("Item_PID",productId)
                .addData("Item_Name",title)
                .addData("Item_Price_Each",priceEach)
                .addData("Item_Price",price)
                .addData("Item_Quantity",quantity)
                .doneDataAdding();
        Toast.makeText(context, "Added to cart...", Toast.LENGTH_SHORT).show();

        //update cart count
        ((ShopDetailsActivity)context).cartCount();
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter==null){
            filter=new FilterProductUser(this,filterList);
        }
        return filter;
    }

    class HolderProductUser extends RecyclerView.ViewHolder{

        //uid views
        private ImageView productIconIv;
        private TextView titleTv,addToCartTv,originalPriceTv;
        public HolderProductUser(@NonNull View itemView) {
            super(itemView);

            //init ui
            productIconIv=itemView.findViewById(R.id.productIconIv);
            titleTv=itemView.findViewById(R.id.titleTv);
            addToCartTv=itemView.findViewById(R.id.addToCartTv);
            originalPriceTv=itemView.findViewById(R.id.originalPriceTv);

        }
    }
}
