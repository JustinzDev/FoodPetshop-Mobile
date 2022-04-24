package th.ac.kmutnb.foodpetshop;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder>{
    private static final String TAG = "my_app";
    private List<CartListItemModel> listitems;
    private String baseURL = "http://154.202.2.5/foodpetshop/img/";

    public CartItemAdapter(List<CartListItemModel> listitems){
        this.listitems = listitems;
    }

    @NonNull
    @Override
    public CartItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.static_rv_cartlistitem, parent, false);
        CartItemAdapter.ViewHolder listitemViewHolder = new CartItemAdapter.ViewHolder(view);
        return listitemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemAdapter.ViewHolder holder, int position) {
        CartListItemModel currentItem = listitems.get(position);
        holder.textViewItem.setText(currentItem.getItemname());
        String amountformatter = String.format("%,d", currentItem.getItemamount());
        holder.amountViewItem.setText("จำนวน: " + amountformatter + " ชิ้น");
        String priceformatter = String.format("%,.2f", currentItem.getItemprice());
        holder.priceViewItem.setText("ราคา: " + priceformatter + " บาท");
        String imgUrl = baseURL + currentItem.getItemimage();

        holder.cardViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, currentItem.getItemid());

                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                FragmentManager manager = activity.getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.navHostFragment, CartItemInfoFragment.newInstance(currentItem.getItemid(), null));
                transaction.commit();
            }
        });

        Picasso.get()
                .load(imgUrl)
                .memoryPolicy(MemoryPolicy.NO_CACHE )
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .placeholder(R.mipmap.ic_launcher).fit()
                .error(R.mipmap.ic_launcher)
                .into(holder.imageViewItem);
    }

    @Override
    public int getItemCount() {
        return listitems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageViewItem;
        TextView textViewItem;
        TextView priceViewItem;
        TextView amountViewItem;
        CardView cardViewItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewItem = itemView.findViewById(R.id.imageItem);
            textViewItem = itemView.findViewById(R.id.myorderitemName);
            amountViewItem = itemView.findViewById(R.id.textAmount);
            priceViewItem = itemView.findViewById(R.id.textTotalPrice);
            cardViewItem = itemView.findViewById(R.id.cardviewlist);
        }
    }
}
