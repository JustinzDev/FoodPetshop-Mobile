package th.ac.kmutnb.foodpetshop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.ViewHolder>{

    private ArrayList<ListItemModel> listitems;

    public ListItemAdapter(ArrayList<ListItemModel> listitems){
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
        holder.imageViewItem.setImageResource(currentItem.getItemImage());
        holder.textViewItem.setText(currentItem.getItemName());
        holder.detailViewItem.setText(currentItem.getItemDetail());
    }

    @Override
    public int getItemCount() {
        return listitems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageViewItem;
        TextView textViewItem;
        TextView detailViewItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewItem = itemView.findViewById(R.id.imageItem);
            textViewItem = itemView.findViewById(R.id.textName);
            detailViewItem = itemView.findViewById(R.id.textDetail);
        }
    }
}