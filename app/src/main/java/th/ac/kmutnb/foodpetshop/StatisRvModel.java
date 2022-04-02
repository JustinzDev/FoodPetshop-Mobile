package th.ac.kmutnb.foodpetshop;

public class StatisRvModel {
    private int img;
    private String text;

    public StatisRvModel(int img, String text){
        this.img = img;
        this.text = text;
    }

    public int getImage() {
        return img;
    }

    public String getText(){
        return text;
    }
}
