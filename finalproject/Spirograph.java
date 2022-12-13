/******************************************************************************
 *  Compilation:  javac Spirograph.java
 *  Execution:    java Spirograph
 *  Dependencies: Draw.java
 *
 *  Draw a curve formed by rolling a smaller circle of radius r inside
 *  a larger circle or radius R. If the pen offset of the pen point in
 *  the moving circle is a, then the equation of the resulting curve
 *  at time t is
 *
 *     x = (R+r)*cos(t) - (r+a)*cos(((R+r)/r)*t)
 *     y = (R+r)*sin(t) - (r+a)*sin(((R+r)/r)*t)
 *
 *  Mathematically, these are hypotrochoids.
 *
 *  Reference: http://www.wordsmith.org/~anu/java/spirograph.html
 *
 ******************************************************************************/

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;

public class Spirograph implements ChangeListener {
    private Draw draw = new Draw();
    private JFrame frame = new JFrame();
    private LabeledSlider sliderR = new LabeledSlider(1, 100, 37);
    private LabeledSlider sliderS = new LabeledSlider(-50, 50, 3);
    private LabeledSlider sliderA = new LabeledSlider(0, 100, 71);

    // create the frame with an empty image
    public Spirograph() {

        // a panel for the 3 sliders
        draw.setXscale(-4, +4);
        draw.setYscale(-4, +4);
        draw.enableDoubleBuffering();
        JPanel sliders = new JPanel();
        sliders.add(sliderR);
        sliders.add(sliderS);
        sliders.add(sliderA);
        sliderR.addChangeListener(this);
        sliderS.addChangeListener(this);
        sliderA.addChangeListener(this);

        // add components to the frame
        JLabel canvas = draw.getJLabel();
        frame.add(canvas, BorderLayout.CENTER);
        frame.add(sliders, BorderLayout.NORTH);

        frame.setTitle("Spirograph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        stateChanged(null);   // draw default spirograph
        frame.setVisible(true);
    }

    public void stateChanged(ChangeEvent e) {
        double R = Math.toRadians(sliderR.getValue());
        double r = Math.toRadians(sliderS.getValue());
        double a = Math.toRadians(sliderA.getValue());
        sliderR.setLabel("R = " + sliderR.getValue());
        sliderS.setLabel("r = " + sliderS.getValue());
        sliderA.setLabel("a = " + sliderA.getValue());
        draw.clear(Color.BLACK);
        draw.setPenColor(Color.getHSBColor((float) Math.random(), 1.0f, 1.0f));
        draw.show();

        for (double t = 0.0; t < 80; t += 0.02) {
            if (r == 0) r = 0.01;  // avoid divide-by-zero
            double x = (R + r) * Math.cos(t) - (r + a) * Math.cos(((R + r) / r) * t);
            double y = (R + r) * Math.sin(t) - (r + a) * Math.sin(((R + r) / r) * t);
            draw.point(x, y);
        }
        draw.show();
        frame.repaint();   // repaints sliders and canvas
    }


    // create one frame object
    public static void main(String[] args) {
        new Spirograph();
    }
}
