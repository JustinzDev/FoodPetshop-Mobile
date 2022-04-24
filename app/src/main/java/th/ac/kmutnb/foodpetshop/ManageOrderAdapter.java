package th.ac.kmutnb.foodpetshop;

import android.app.ProgressDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ManageOrderAdapter extends RecyclerView.Adapter<ManageOrderAdapter.ViewHolder>{
    private static final String TAG = "my_app";
    private List<MyOrderListModel> listitems;
    private String baseURL = "http://154.202.2.5/foodpetshop/img/";

    public static final String REQUEST_TAG = "myrequest";
    private RequestQueue mQueue;

    public ManageOrderAdapter(List<MyOrderListModel> listitems){
        this.listitems = listitems;
    }

    @NonNull
    @Override
    public ManageOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.static_rv_manageorderlist, parent, false);
        ManageOrderAdapter.ViewHolder listitemViewHolder = new ManageOrderAdapter.ViewHolder(view);
        return listitemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ManageOrderAdapter.ViewHolder holder, int position) {
        MyOrderListModel currentItem = listitems.get(position);
        String itemName = currentItem.getItemkey();
        holder.itemName.setText("orderID: " + itemName);
        String amountformatter = String.format("%,d", currentItem.getItemCount());
        holder.itemTotalAmount.setText("" + amountformatter + " items");
        String priceformatter = String.format("%,.2f", currentItem.getItemtotalprice());
        holder.itemTotalPrice.setText("รวม: " + priceformatter + " บาท");

        String itemstatetext = currentItem.getItemstate();
        if(itemstatetext.matches("wait")) holder.itemState.setText("สถานะ: รอดำเนินการ");
        else if(itemstatetext.matches("process")) holder.itemState.setText("สถานะ: กำลังจัดส่ง");
        else if(itemstatetext.matches("finish")) holder.itemState.setText("สถานะ: จัดส่งสำเร็จ");

        String imgUrl = baseURL + currentItem.getItemimgpreview();

        holder.cardViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                FragmentManager manager = activity.getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.navHostFragment, ManageOrderInfoFragment.newInstance(currentItem.getItemkey(), currentItem.getItemownerid(), currentItem.getItempayment(), currentItem.getItemstate(), currentItem.getItemtotalprice()));
                transaction.commit();
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
        TextView itemState;
        CardView cardViewItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImg = itemView.findViewById(R.id.imageItem);
            itemName = itemView.findViewById(R.id.myorderitemName);
            itemTotalPrice = itemView.findViewById(R.id.textPrice);
            itemTotalAmount = itemView.findViewById(R.id.textAmount);
            itemState = itemView.findViewById(R.id.textState);
            cardViewItem = itemView.findViewById(R.id.cardviewlist);
        }
    }
}
