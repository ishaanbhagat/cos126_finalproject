import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;


public class NBody implements ChangeListener, ActionListener {

    private In inputParameters;
    private int n;
    private double radius;
    private double[] px;
    private double[] py;
    private double[] vx;
    private double[] vy;
    private double[] mass;
    private String[] image;

    private boolean initialized = false;

    private double interval;
    private int simulationSpeedPause = 10;
    private double secondsInOneDay = 86400;
    private double bigG = 6.67E-11;
    private boolean simulate = false;

    private Draw draw = new Draw();
    private JFrame frame = new JFrame();
    private LabeledSlider earthSlider;
    private LabeledSlider sunSlider;
    private JButton playPauseButton;
    private final ImageIcon play = new ImageIcon("play.png");
    private final ImageIcon pause = new ImageIcon("pause.png");

    JRadioButton slowSpeed;
    JRadioButton mediumSpeed;
    JRadioButton fastSpeed;

    private JButton resetButton;

    private double[] massRatios = { 1.0, 1.0 };
    double t;
    double ax;
    double ay;
    String daysPassed = "Number of Earth days passed: ";
    int earthDays;
    double[] planetPixels;

    public void resetPlanets() {

        inputParameters = new In("earthsun.txt");
        n = inputParameters.readInt();
        radius = inputParameters.readDouble();
        px = new double[n];
        py = new double[n];
        vx = new double[n];
        vy = new double[n];
        mass = new double[n];
        image = new String[n];

        interval = 25000.0;

        for (int i = 0; i < n; i++) {
            px[i] = inputParameters.readDouble();
            py[i] = inputParameters.readDouble();
            vx[i] = inputParameters.readDouble();
            vy[i] = inputParameters.readDouble();
            mass[i] = inputParameters.readDouble();
            image[i] = inputParameters.readString();
        }

        t = 0.0;
        ax = 0.0;
        ay = 0.0;
        massRatios = new double[] { 1.0, 1.0 };
        planetPixels = new double[] { radius / 12, radius / 3 };
        if (initialized) {
            earthSlider.setValue(10);
            sunSlider.setValue(10);
            mediumSpeed.setSelected(true);
            simulate = false;
            playPauseButton.setIcon(play);
        }
    }

    public void calculationIncrement() {
        if (simulate) {
            double[] fx = new double[n];
            double[] fy = new double[n];

            for (int i = 0; i < n - 1; i++) {
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        // Using Law of Gravitation to determine forces BY each other body ON all n bodies
                        double dx = px[j] - px[i]; // make sure it's force ON body i
                        double dy = py[j] - py[i];
                        double r = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
                        double bigF = ((bigG * mass[i] * mass[j]) / Math.pow(r, 2));
                        fx[i] = fx[i] + (bigF * (dx / r));
                        fy[i] = fy[i] + (bigF * (dy / r));
                    }
                }
            }


            // Step 5B. Update velocities and positions.
            for (int i = 0; i < n - 1; i++) {
                ax = (fx[i] / mass[i]);
                ay = (fy[i] / mass[i]);
                vx[i] = vx[i] + ax * interval;
                vy[i] = vy[i] + ay * interval;
                px[i] = px[i] + vx[i] * interval;
                py[i] = py[i] + vy[i] * interval;
            }
        }
    }

    public void drawSimulation() {
        draw.picture(0, 0, "starfield.jpg");
        for (int i = 0; i < n; i++) {
            draw.picture(px[i], py[i], image[i], planetPixels[i] * Math.sqrt(massRatios[i]),
                         planetPixels[i] * Math.sqrt(massRatios[i]));
        }

        earthDays = (int) (t / secondsInOneDay);
        draw.text(0.4 * radius, -0.9 * radius, daysPassed + earthDays);
        draw.show();
        frame.repaint();
        draw.pause(simulationSpeedPause);

        if (simulate)
            t = t + interval;
    }

    public NBody() {

        // Setting up planet parameters

        resetPlanets();

        initialized = true;

        // Setting up main JFrame

        frame.setLayout(null);
        frame.setSize(850, 700);
        frame.setTitle("Earth-Sun Orbit Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setBackground(Color.black);

        draw.setXscale(-radius, radius);
        draw.setYscale(-radius, radius);
        draw.enableDoubleBuffering();
        draw.setPenColor(Color.white);

        // Setting up actual simulation JLabel & JPanel
        JLabel canvas = draw.getJLabel();
        JPanel sketch = new JPanel();
        sketch.setLayout(new BorderLayout());
        canvas.setVerticalAlignment(JLabel.CENTER);
        canvas.setHorizontalAlignment(JLabel.CENTER);
        sketch.add(canvas);

        sketch.setBounds(0, 0, 550, 550);
        Border canvasBorder = BorderFactory.createLineBorder(Color.blue, 3, false);
        sketch.setBorder(canvasBorder);
        frame.add(sketch);
        frame.getContentPane().setBackground(Color.white);

        // Setting up Sun and Earth mass Sliders and Panels

        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(5, new JLabel("0.5"));
        labelTable.put(10, new JLabel("1.0"));
        labelTable.put(15, new JLabel("1.5"));
        labelTable.put(20, new JLabel("2.0"));

        earthSlider = new LabeledSlider(5, 20, 10);
        earthSlider.addChangeListener(this);
        earthSlider.setLabel("Earth Mass = " + earthSlider.getValue() / 10.0 + "x");

        sunSlider = new LabeledSlider(5, 20, 10);
        sunSlider.addChangeListener(this);
        sunSlider.setLabel("Sun Mass = " + sunSlider.getValue() / 10.0 + "x");

        JPanel earthSliderPanel = new JPanel();
        earthSliderPanel.setLayout(new BorderLayout());
        earthSliderPanel.setBounds(550, 350, 300, 100);
        earthSliderPanel.add(earthSlider);
        earthSlider.setPaintTicks(true);
        earthSlider.setPaintLabels(true);
        earthSlider.setMinorTickSpacing(1);
        earthSlider.setSnapToTicks(true);
        earthSlider.setMajorTickSpacing(5);
        earthSlider.setLabelTable(labelTable);

        JPanel sunSliderPanel = new JPanel();
        sunSliderPanel.setLayout(new BorderLayout());
        sunSliderPanel.setBounds(550, 450, 300, 100);
        sunSliderPanel.add(sunSlider);
        sunSlider.setPaintTicks(true);
        sunSlider.setPaintLabels(true);
        sunSlider.setMinorTickSpacing(1);
        sunSlider.setSnapToTicks(true);
        sunSlider.setMajorTickSpacing(5);
        sunSlider.setLabelTable(labelTable);

        frame.add(earthSliderPanel);
        frame.add(sunSliderPanel);

        // Setting up play pause button

        playPauseButton = new JButton();

        JPanel playPauseButtonPanel = new JPanel();
        playPauseButton.setIcon(play);
        playPauseButtonPanel.setLayout(new BorderLayout());
        playPauseButtonPanel.setBounds(220, 562, 100, 100);

        playPauseButton.setHorizontalAlignment(JButton.CENTER);
        playPauseButton.setVerticalAlignment(JButton.CENTER);
        playPauseButton.addActionListener(this);
        playPauseButtonPanel.add(playPauseButton);
        frame.add(playPauseButtonPanel);

        slowSpeed = new JRadioButton("Slow");
        mediumSpeed = new JRadioButton("Medium");
        fastSpeed = new JRadioButton("Fast");

        slowSpeed.addActionListener(this);
        mediumSpeed.addActionListener(this);
        fastSpeed.addActionListener(this);


        ButtonGroup speedButtons = new ButtonGroup();
        speedButtons.add(slowSpeed);
        speedButtons.add(mediumSpeed);
        speedButtons.add(fastSpeed);

        slowSpeed.setAlignmentY(Component.CENTER_ALIGNMENT);
        mediumSpeed.setAlignmentY(Component.CENTER_ALIGNMENT);
        fastSpeed.setAlignmentY(Component.CENTER_ALIGNMENT);


        JPanel speedRadioButtons = new JPanel();

        speedRadioButtons.setBounds(50, 575, 100, 75);

        BoxLayout radioPanelLayout = new BoxLayout(speedRadioButtons, BoxLayout.Y_AXIS);
        speedRadioButtons.setLayout(radioPanelLayout);

        speedRadioButtons.add(slowSpeed);
        speedRadioButtons.add(mediumSpeed);
        speedRadioButtons.add(fastSpeed);

        mediumSpeed.setSelected(true);

        frame.add(speedRadioButtons);
        ImageIcon reset = new ImageIcon("reset.png");
        resetButton = new JButton();
        JPanel resetButtonPanel = new JPanel();
        resetButton.setIcon(reset);
        resetButtonPanel.setLayout(new BorderLayout());
        resetButtonPanel.setBounds(650, 562, 100, 100);

        resetButton.setHorizontalAlignment(JButton.CENTER);
        resetButton.setVerticalAlignment(JButton.CENTER);
        resetButton.addActionListener(this);
        resetButtonPanel.add(resetButton);
        frame.add(resetButtonPanel);

        frame.setVisible(true);
        stateChanged(null);
        // StdAudio.playInBackground("2001.wav");

        drawSimulation();

        while (true) {
            calculationIncrement();
            drawSimulation();
        }

    }

    public void stateChanged(ChangeEvent e) {
        massRatios[0] = earthSlider.getValue() / 10.0;
        earthSlider.setLabel("Earth Mass = " + earthSlider.getValue() / 10.0 + "x");
        mass[0] *= massRatios[0];

        massRatios[1] = sunSlider.getValue() / 10.0;
        sunSlider.setLabel("Sun Mass = " + sunSlider.getValue() / 10.0 + "x");
        mass[1] *= massRatios[1];

        frame.repaint();   // repaints sliders and canvas
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == playPauseButton) {
            if (simulate) {
                simulate = false;
                playPauseButton.setIcon(play);
            }
            else {
                simulate = true;
                playPauseButton.setIcon(pause);
            }
        }

        if (e.getSource() == slowSpeed)
            simulationSpeedPause = 20;
        else if (e.getSource() == mediumSpeed)
            simulationSpeedPause = 10;
        else if (e.getSource() == fastSpeed)
            simulationSpeedPause = 1;

        if (e.getSource() == resetButton) {
            resetPlanets();
        }

    }

    public static void main(String[] args) {
        new NBody();
    }
}

