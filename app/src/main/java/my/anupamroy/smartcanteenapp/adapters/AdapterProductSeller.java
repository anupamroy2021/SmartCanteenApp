package my.anupamroy.smartcanteenapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import my.anupamroy.smartcanteenapp.R;
import my.anupamroy.smartcanteenapp.activities.EditProductActivity;
import my.anupamroy.smartcanteenapp.FilterProduct;
import my.anupamroy.smartcanteenapp.models.ModelProducts;

public class AdapterProductSeller extends RecyclerView.Adapter<AdapterProductSeller.HolderProductSeller> implements Filterable {

    private Context context;
    public ArrayList<ModelProducts>productList,filterList;
    private FilterProduct filter;

    public AdapterProductSeller(Context context, ArrayList<ModelProducts> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList = productList;
    }

    @NonNull
    @Override
    public HolderProductSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view= LayoutInflater.from(context).inflate(R.layout.row_product_seller,parent,false);
        return new HolderProductSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductSeller holder, int position) {
        //get data
        ModelProducts modelProducts=productList.get(position);
        String id= modelProducts.getProductId();
        String uid= modelProducts.getUid();
        String productCategory= modelProducts.getProductCategory();
        String icon= modelProducts.getProductIcon();
        String quantity= modelProducts.getProductQuantity();
        String title= modelProducts.getProductTitle();
        String productDescription= modelProducts.getProductDescription();
        String timestamp= modelProducts.getTimestamp();
        String originalPrice= modelProducts.getOriginalPrice();


        //set data
        holder.titleTv.setText(title);
        holder.quantityTv.setText(quantity);
        holder.originalPriceTv.setText("₹"+originalPrice);

        try {
            Picasso.get().load(icon).placeholder(R.drawable.ic_add_shopping_primary).into(holder.productIconTv);
        }
        catch (Exception e){
            holder.productIconTv.setImageResource(R.drawable.ic_add_shopping_primary);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //handle item clicks, show item details in bottom sheet
                detailsBottomSheet(modelProducts);//here model products contains details of clicked products
            }
        });
    }

    private void detailsBottomSheet(ModelProducts modelProducts) {
        //bottom sheet
        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(context);
        //inflate view for bottom sheet
        View view=LayoutInflater.from(context).inflate(R.layout.bs_product_details_seller,null);
        //set view to bottom sheet
        bottomSheetDialog.setContentView(view);



        //init views of bottom sheet
        ImageButton backBtn=view.findViewById(R.id.backBtn);
        ImageButton deleteBtn=view.findViewById(R.id.deleteBtn);
        ImageButton editBtn=view.findViewById(R.id.editBtn);
        ImageView productIconIv=view.findViewById(R.id.productIconIv);
        TextView titleTv=view.findViewById(R.id.titleTv);
        TextView descriptionTv=view.findViewById(R.id.descriptionTv);
        TextView categoryTv=view.findViewById(R.id.categoryTv);
        TextView quantityTv=view.findViewById(R.id.quantityTv);
        TextView originalPriceTv=view.findViewById(R.id.originalPriceTv);

        //get data
        final String id=modelProducts.getProductId();
        String uid= modelProducts.getUid();
        String productCategory= modelProducts.getProductCategory();
        String productDescription=modelProducts.getProductDescription();
        String icon= modelProducts.getProductIcon();
        String quantity= modelProducts.getProductQuantity();
        final String title= modelProducts.getProductTitle();
        String timestamp= modelProducts.getTimestamp();
        String originalPrice= modelProducts.getOriginalPrice();

        //set data
        titleTv.setText(title);
        descriptionTv.setText(productDescription);
        categoryTv.setText(productCategory);
        quantityTv.setText(quantity);
        originalPriceTv.setText("₹"+originalPrice);

        try {
            Picasso.get().load(icon).placeholder(R.drawable.ic_add_shopping_primary).into(productIconIv);
        }
        catch (Exception e){
            productIconIv.setImageResource(R.drawable.ic_add_shopping_primary);
        }

        //show dialog
        bottomSheetDialog.show();

        //edit click
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                //open edit product activity, pass id of the product
                Intent intent=new Intent(context, EditProductActivity.class);
                intent.putExtra("productId",id);
                context.startActivity(intent);
            }
        });
        //delete click
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                //show delete confirm dialog
                AlertDialog.Builder builder= new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete product"+title+"?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //delete
                                deleteProduct(id);//id is the product id
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                //cancel dismiss dialog
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        //back click
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dismiss bottom sheet
                bottomSheetDialog.dismiss();
            }
        });

    }

    private void deleteProduct(String id) {
        //delete product using its id
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //product Deleted
                        Toast.makeText(context, "Product Deleted...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed deleting Projects
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter==null) {
            filter = new FilterProduct(this, filterList);
        }
        return filter;
    }

    class HolderProductSeller extends RecyclerView.ViewHolder{
        /*holds view of recylerview*/

        private ImageView productIconTv;
        private TextView titleTv,quantityTv,originalPriceTv;

        public HolderProductSeller(@NonNull View itemView){
            super(itemView);
            productIconTv=itemView.findViewById(R.id.productIconIv);
            titleTv=itemView.findViewById(R.id.titleTv);
            quantityTv=itemView.findViewById(R.id.quantityTv);
            originalPriceTv=itemView.findViewById(R.id.originalPriceTv);

        }
    }
}
