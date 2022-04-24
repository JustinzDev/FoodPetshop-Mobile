package th.ac.kmutnb.foodpetshop;

import android.app.ProgressDialog;
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

import com.android.volley.RequestQueue;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class StorageItemListAdapter extends RecyclerView.Adapter<StorageItemListAdapter.ViewHolder>{
    private static final String TAG = "my_app";
    private List<ListItemModel> listitems;
    private String baseURL = "http://154.202.2.5/foodpetshop/img/";

    public StorageItemListAdapter(List<ListItemModel> listitems){
        this.listitems = listitems;
    }

    @NonNull
    @Override
    public StorageItemListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.static_rv_storageitemlist, parent, false);
        StorageItemListAdapter.ViewHolder listitemViewHolder = new StorageItemListAdapter.ViewHolder(view);
        return listitemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StorageItemListAdapter.ViewHolder holder, int position) {
        ListItemModel currentItem = listitems.get(position);
        holder.textViewItem.setText(currentItem.getItemname());
        String amountformatter = String.format("%,d", currentItem.getItemamount());
        holder.amountViewItem.setText("คงเหลือ: " + amountformatter + " ชิ้น");
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
                transaction.replace(R.id.navHostFragment, ManageStorageItemFragment.newInstance(currentItem.getItemid(), null));
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
        TextView amountViewItem;
        TextView priceViewItem;
        CardView cardViewItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewItem = itemView.findViewById(R.id.imageItem);
            textViewItem = itemView.findViewById(R.id.myorderitemName);
            amountViewItem = itemView.findViewById(R.id.textAmount);
            priceViewItem = itemView.findViewById(R.id.textPrice);
            cardViewItem = itemView.findViewById(R.id.cardviewlist);
        }
    }
}
