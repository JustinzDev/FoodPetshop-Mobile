package th.ac.kmutnb.foodpetshop;

public class CartListItemModel {
    private String _id;
    private String itemname;
    private String itemdetail;
    private double itemtotalprice;
    private int itemamount;
    private String itemcategory;
    private String itemimg;
    private String itemownerid;

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

    public double getItemprice() { return itemtotalprice; }

    public void setItemprice(int itemprice) { this.itemtotalprice = itemprice; }

    public int getItemamount() { return itemamount; }

    public void setItemamount(int itemamount) { this.itemamount = itemamount; }

    public String getItemcategory() { return itemcategory; }

    public void setItemcategory(String itemcategory) { this.itemcategory = itemcategory; }

    public String getItemownerID() { return itemownerid; }

    public void setItemownerID(String itemOwnerID) { this.itemownerid = itemOwnerID; }
}
