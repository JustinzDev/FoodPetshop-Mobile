package th.ac.kmutnb.foodpetshop;

public class StatisRvModel {
    private int img;
    private String text;
    private String model;

    public StatisRvModel(int img, String text, String model){
        this.img = img;
        this.text = text;
        this.model = model;
    }

    public int getImage() {
        return img;
    }

    public String getText(){
        return text;
    }

    public String getModel() { return model; }
}
