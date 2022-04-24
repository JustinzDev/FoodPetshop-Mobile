package th.ac.kmutnb.foodpetshop;

import android.app.ProgressDialog;
import android.os.CountDownTimer;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyOrderListAdapter extends RecyclerView.Adapter<MyOrderListAdapter.ViewHolder>{
    private static final String TAG = "my_app";
    private List<MyOrderListModel> listitems;
    private String baseURL = "http://154.202.2.5/foodpetshop/img/";

    public MyOrderListAdapter(List<MyOrderListModel> listitems){
        this.listitems = listitems;
    }

    @NonNull
    @Override
    public MyOrderListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.static_rv_myorderlist, parent, false);
        MyOrderListAdapter.ViewHolder listitemViewHolder = new MyOrderListAdapter.ViewHolder(view);
        return listitemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderListAdapter.ViewHolder holder, int position) {
        MyOrderListModel currentItem = listitems.get(position);
        String itemName = currentItem.getItemkey();
        holder.itemName.setText("orderID: " + itemName);
        String amountformatter = String.format("%,d", currentItem.getItemCount());
        holder.itemTotalAmount.setText("" + amountformatter + " items");
        String priceformatter = String.format("%,.2f", currentItem.getItemtotalprice());
        holder.itemTotalPrice.setText("รวม: " + priceformatter + " บาท");

        if(currentItem.getItempayment().matches("keep_destination")){
            holder.itempayment.setText("- เก็บปลายทาง");
        } else if(currentItem.getItempayment().matches("transfer")){
            holder.itempayment.setText("- โอนผ่านบัญชีธนาคาร");
        }

        String itemstatetext = currentItem.getItemstate();
        if(itemstatetext.matches("wait")) holder.itemstate.setText("สถานะ: รอดำเนินการ");
        else if(itemstatetext.matches("process")) holder.itemstate.setText("สถานะ: กำลังจัดส่ง");
        else if(itemstatetext.matches("finish")) holder.itemstate.setText("สถานะ: จัดส่งสำเร็จ");

        String imgUrl = baseURL + currentItem.getItemimgpreview();

        holder.cardViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentItem.getItemstate().matches("finish")){
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    FragmentManager manager = activity.getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.navHostFragment, OrderFinishFragment.newInstance(currentItem.getItemkey(), currentItem.getItemownerid()));
                    transaction.commit();
                } else {
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    FragmentManager manager = activity.getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.navHostFragment, MyOrderInfoFragment.newInstance(currentItem.getItemkey(), currentItem.getItemownerid(), currentItem.getItempayment(), currentItem.getItemstate(), currentItem.getItemtotalprice()));
                    transaction.commit();
                }
            }
        });

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
        TextView itemstate;
        TextView itempayment;
        CardView cardViewItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImg = itemView.findViewById(R.id.imageItem);
            itemName = itemView.findViewById(R.id.myorderitemName);
            itemTotalPrice = itemView.findViewById(R.id.totalprice);
            itemTotalAmount = itemView.findViewById(R.id.countitem);
            itemstate = itemView.findViewById(R.id.itemstate);
            itempayment = itemView.findViewById(R.id.itempayment);
            cardViewItem = itemView.findViewById(R.id.cardviewlist);
        }
    }
}
