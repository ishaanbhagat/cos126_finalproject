import javax.swing.JSlider;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class LabeledSlider extends JSlider {
    private TitledBorder border = new TitledBorder(new EtchedBorder());

    public LabeledSlider(int min, int max, int val) {
        super(min, max, val);
        setBorder(border);
    }

    public void setLabel(String s) {
        border.setTitle(s);
    }
}

