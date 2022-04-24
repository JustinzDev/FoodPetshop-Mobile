package th.ac.kmutnb.foodpetshop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ManageOrderInfoAdapter extends RecyclerView.Adapter<ManageOrderInfoAdapter.ViewHolder>{
    private static final String TAG = "my_app";
    private List<MyOrderInfoModel> listitems;
    private String baseURL = "http://154.202.2.5/foodpetshop/img/";

    public ManageOrderInfoAdapter(List<MyOrderInfoModel> listitems){
        this.listitems = listitems;
    }

    @NonNull
    @Override
    public ManageOrderInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.static_rv_myorderinfolist, parent, false);
        ManageOrderInfoAdapter.ViewHolder listitemViewHolder = new ManageOrderInfoAdapter.ViewHolder(view);
        return listitemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ManageOrderInfoAdapter.ViewHolder holder, int position) {
        MyOrderInfoModel currentItem = listitems.get(position);
        String itemName = currentItem.getItemname();
        holder.itemName.setText(itemName);
        String amountformatter = String.format("%,d", currentItem.getItemamount());
        holder.itemTotalAmount.setText("รวม: " + amountformatter + " ชิ้น");
        String priceformatter = String.format("%,.2f", currentItem.getItemtotalprice());
        holder.itemTotalPrice.setText("รวม: " + priceformatter + " บาท");

        String imgUrl = baseURL + currentItem.getItemimg();

//        holder.cardViewItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, currentItem.getItemid());
//
//                AppCompatActivity activity = (AppCompatActivity) view.getContext();
//                FragmentManager manager = activity.getSupportFragmentManager();
//                FragmentTransaction transaction = manager.beginTransaction();
//                transaction.replace(R.id.navHostFragment, CartItemInfoFragment.newInstance(currentItem.getItemid(), null));
//                transaction.commit();
//            }
//        });

        Picasso.get()
                .load(imgUrl)
                .memoryPolicy(MemoryPolicy.NO_CACHE )
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .placeholder(R.mipmap.ic_launcher).fit()
                .error(R.mipmap.ic_launcher)
                .into(holder.itemImg);
    }

    @Override
    public int getItemCount() {
        return listitems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView itemImg;
        TextView itemName;
        TextView itemTotalPrice;
        TextView itemTotalAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImg = itemView.findViewById(R.id.imageItem);
            itemName = itemView.findViewById(R.id.myorderitemName);
            itemTotalPrice = itemView.findViewById(R.id.totalprice);
            itemTotalAmount = itemView.findViewById(R.id.countitem);
        }
    }
}
