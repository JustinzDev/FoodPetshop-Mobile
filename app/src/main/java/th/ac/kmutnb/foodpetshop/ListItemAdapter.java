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

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.ViewHolder>{
    private static final String TAG = "my_app";
    private List<ListItemModel> listitems;
    private String baseURL = "http://154.202.2.5/foodpetshop/img/";

    public ListItemAdapter(List<ListItemModel> listitems){
        this.listitems = listitems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.static_rv_listitem, parent, false);
        ViewHolder listitemViewHolder = new ViewHolder(view);
        return listitemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListItemModel currentItem = listitems.get(position);
        holder.textViewItem.setText(currentItem.getItemname());
        String priceformatter = String.format("%,.2f", currentItem.getItemprice());
        holder.priceViewItem.setText("ราคา: " + priceformatter + " บาท");
        String imgUrl = baseURL + currentItem.getItemimage();

        int starReview = currentItem.getItempopular();
        if(starReview >= 1) holder.star1VewItem.setVisibility(View.VISIBLE);
        if(starReview >= 2) holder.star2VewItem.setVisibility(View.VISIBLE);
        if(starReview >= 3) holder.star3VewItem.setVisibility(View.VISIBLE);
        if(starReview >= 4) holder.star4VewItem.setVisibility(View.VISIBLE);
        if(starReview >= 5) holder.star5VewItem.setVisibility(View.VISIBLE);

        holder.cardViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, currentItem.getItemid());

                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                FragmentManager manager = activity.getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.navHostFragment, ItemInfoFragment.newInstance(currentItem.getItemid(), null));
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
        CardView cardViewItem;
        ImageView star1VewItem;
        ImageView star2VewItem;
        ImageView star3VewItem;
        ImageView star4VewItem;
        ImageView star5VewItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewItem = itemView.findViewById(R.id.imageItem);
            textViewItem = itemView.findViewById(R.id.myorderitemName);
            priceViewItem = itemView.findViewById(R.id.textAmount);
            cardViewItem = itemView.findViewById(R.id.cardviewlist);

            star1VewItem = itemView.findViewById(R.id.star1);
            star2VewItem = itemView.findViewById(R.id.star2);
            star3VewItem = itemView.findViewById(R.id.star3);
            star4VewItem = itemView.findViewById(R.id.star4);
            star5VewItem = itemView.findViewById(R.id.star5);
        }
    }
}