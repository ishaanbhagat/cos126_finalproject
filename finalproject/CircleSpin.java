import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class CircleSpin extends JPanel {

    // radius of the circle
    int r = 50;

    // coordinates of the center of the circle
    int x = r;
    int y = r;

    // angle of the circle's rotation
    double theta = 0;

    // speed of the circle's rotation
    double dTheta = 0.1;

    // size of the window
    int width = 500;
    int height = 500;

    // panel to hold the slider
    JPanel sliderPanel = new JPanel();

    // slider to adjust the circle's radius
    JSlider radiusSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, r);

    public CircleSpin() {
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);

        // add the radius slider to the panel
        sliderPanel.add(radiusSlider);

        // add a listener to the radius slider to update the circle's radius
        radiusSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                r = radiusSlider.getValue();
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // update the coordinates and angle of the circle
        x = (int) (r * Math.cos(theta) + width / 2);
        y = (int) (r * Math.sin(theta) + height / 2);
        theta += dTheta;

        // draw the circle at the updated coordinates
        g.setColor(Color.WHITE);
        g.fillOval(x - r, y - r, 2 * r, 2 * r);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CircleSpin panel = new CircleSpin();
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);

        // update the animation constantly
        while (true) {
            panel.repaint();
        }

    }

}
