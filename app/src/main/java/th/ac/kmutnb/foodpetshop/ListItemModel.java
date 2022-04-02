package th.ac.kmutnb.foodpetshop;

public class ListItemModel {
    private int itemImage;
    private String itemName;
    private String itemDetail;

    public ListItemModel(int itemImage, String itemName, String itemDetail){
        this.itemImage = itemImage;
        this.itemName = itemName;
        this.itemDetail = itemDetail;
    }

    public int getItemImage(){
        return itemImage;
    }

    public String getItemName(){
        return itemName;
    }

    public String getItemDetail(){
        return itemDetail;
    }
}
