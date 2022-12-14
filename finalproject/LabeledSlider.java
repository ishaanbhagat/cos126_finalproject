import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.util.Hashtable;

public class LabeledSlider extends JSlider {
    private TitledBorder border = new TitledBorder(new EtchedBorder());

    public LabeledSlider(int min, int max, int val) {
        super(min, max, val);
        setBorder(border);
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(5, new JLabel("0.5"));
        labelTable.put(10, new JLabel("1.0"));
        labelTable.put(15, new JLabel("1.5"));
        labelTable.put(20, new JLabel("2.0"));
        this.setPaintTicks(true);
        this.setPaintLabels(true);
        this.setMinorTickSpacing(1);
        this.setSnapToTicks(true);
        this.setMajorTickSpacing(5);
        this.setLabelTable(labelTable);
        this.setFocusable(false);
    }

    public void setLabel(String s) {
        border.setTitle(s);
    }
}

