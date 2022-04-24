package th.ac.kmutnb.foodpetshop;

public class MyOrderInfoModel {
    private String _id;
    private String itemownerid;
    private String itemname;
    private String itemid;
    private int itemamount;
    private double itemtotalprice;
    private String itemstate;
    private String itempayment;
    private String itemimg;

    public String get_id() { return this._id; }
    public String getItemownerid() { return this.itemownerid; }
    public String getItemid() { return this.itemid; }
    public String getItemname() { return this.itemname; }
    public int getItemamount() { return this.itemamount; }
    public double getItemtotalprice() { return this.itemtotalprice; }
    public String getItemstate() { return this.itemstate; }
    public String getItempayment() { return this.itempayment; }
    public String getItemimg() { return this.itemimg; }

    public void set_id(String _id) { this._id = _id; }
    public void setItemownerid(String itemownerid) { this.itemownerid = itemownerid; }
    public void setItemname(String itemname) { this.itemname = itemname; }
    public void setItemid(String itemid) { this.itemid = itemid; }
    public void setItemamount(int itemamount) { this.itemamount = itemamount; }
    public void setItemtotalprice(double itemtotalprice) { this.itemtotalprice = itemtotalprice; }
    public void setItemstate(String itemstate) { this.itemstate = itemstate; }
    public void setItempayment(String itempayment) { this.itempayment = itempayment; }
}
