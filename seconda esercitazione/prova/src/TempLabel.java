import javax.swing.*;

public class TempLabel extends JLabel {

    @Override
    public void setText(String text) {
        super.setText(String.valueOf(cToF(text)));
    }

    private double cToF(String celsius){
        double c=Double.parseDouble(celsius);
        return c * (9.0/5.0) + 32.0;
    }

    public TempLabel(){
        super("0");
    }
}
