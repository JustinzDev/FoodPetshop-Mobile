package th.ac.kmutnb.foodpetshop;

public class ListItemModel {
    private String _id;
    private String itemname;
    private String itemdetail;
    private int itemprice;
    private int itemamount;
    private int itempopular;
    private String itemcategory;
    private String itemimg;

//    public ListItemModel(int itemImage, String itemName, String itemDetail){
//        this.itemImage = itemImage;
//        this.itemName = itemName;
//        this.itemDetail = itemDetail;
//    }

    public String getItemid() { return _id; }

    public void setID(String _id) { this._id = _id; }

    public String getItemimage(){
        return itemimg;
    }

    public void setItemimage(String itemimg) { this.itemimg = itemimg; }

    public String getItemname(){
        return itemname;
    }

    public void setItemname(String itemname) { this.itemname = itemname; }

    public String getItemdetail(){
        return itemdetail;
    }

    public void setItemdetail(String itemdetail) { this.itemdetail = itemdetail; }

    public int getItemprice() { return itemprice; }

    public void setItemprice(int itemprice) { this.itemprice = itemprice; }

    public int getItemamount() { return itemamount; }

    public void setItemamount(int itemamount) { this.itemamount = itemamount; }

    public int getItempopular() { return itempopular; }

    public void setItempopular(int itempopular) { this.itempopular = itempopular; }
}
