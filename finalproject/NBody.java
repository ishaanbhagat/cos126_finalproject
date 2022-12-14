import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Class creating simulation of the orbit between the Earth and Sun, and
// a corresponding Java Swing GUI to customize said simulation
public class NBody implements ChangeListener, ActionListener {

    // In data type to read in input parameters for Sun and Earth
    private In inputParameters;
    // Number of bodies in this system (code is only compatible with 2 as of now)
    private int n;
    // Radius of universe
    private double radius;
    // Array to hold x-position of bodies
    private double[] px;
    // Array to hold y-position of bodies
    private double[] py;
    // Array to hold x-velocity of bodies
    private double[] vx;
    // Array to hold y-velocity of bodies
    private double[] vy;
    // Array to hold masses of bodies
    private double[] mass;
    // Array to hold filenames of images of bodies
    private String[] image;
    // Angle between Sun and Earth at all times
    private double theta;
    // Angle between North and current direction of net velocity of Earth
    private double alpha;

    // Array to hold x-component of forces on bodies
    private double[] fx;
    // Array to hold y-component of forces on bodies
    private double[] fy;

    // Boolean used to establish when setup of all Objects and GUI elements
    // are complete
    private boolean initialized = false;

    // String representing name of orbiting 'planet'
    private String planetName = "Earth";
    // String representing name of orbiting 'star'
    private String starName = "Sun";
    // String representing filename of current background music to be played
    private String song = "2001.wav";

    // Double representing figurative interval of seconds elapsed between frames
    private double interval;
    // Int representing literal interval in milliseconds paused between frames
    private int simulationSpeedPause = 10;
    // Double representing seconds in one day on Earth. To be used to calculate
    // number of Earth days elapsed since the simulation started.
    private double SECONDS_PER_DAY = 86400;
    // Double representing G, the gravitational constant used in the equation
    // representing Newton's Law of Gravitation
    private double BIG_G = 6.67E-11;
    // Boolean representing whether to 'play' or 'pause' simulation. Starts off
    // false or 'paused'.
    private boolean simulate = false;
    // Boolean representing whether to display 'Normal' or Star Wars visuals. Starts
    // off false or 'Normal'.
    private boolean normalMode = true;

    // Draw data type to draw simulation onto and export as JLabel
    private Draw draw = new Draw();
    // JFrame holding entire simulation window (visuals + GUI)
    private JFrame frame = new JFrame();
    // Customized JSlider to control current mass of Earth in simulation
    private LabeledSlider earthSlider;
    // Customized JSlider to control current mass of Sun in simulation
    private LabeledSlider sunSlider;
    // JButton to 'play' or 'pause' the simulation
    private JButton playPauseButton;

    // Icon to show 'play' state of playPauseButton
    private final ImageIcon play = new ImageIcon("play.png");
    // Icon to show 'pause' state of playPauseButton
    private final ImageIcon pause = new ImageIcon("pause.png");

    // JRadio Button to set simulation 'speed' to slow
    private JRadioButton slowSpeed;
    // JRadio Button to set simulation 'speed' to medium; default option
    private JRadioButton mediumSpeed;
    // JRadio Button to set simulation 'speed' to fast
    private JRadioButton fastSpeed;

    // JButton to reset simulation to original starting state
    private JButton resetButton;
    // JCheckbox to have background music play during simulation
    private JCheckBox playMusic;
    // JButton to toggle between 'Normal' and 'Star Wars' visual mode
    private JButton starWars;

    // Icon to show 'Star Wars' state of starWars
    private final ImageIcon vader = new ImageIcon("vader.png");
    // Icon to show 'Normal' state of starWars
    private final ImageIcon normal = new ImageIcon("normal.png");

    // JCheckbox to toggle effect of gravity in simulation; selected by default
    private JCheckBox gravityEffect;
    // JCheckbox to toggle  in simulation
    private JCheckBox forceVector;
    // JCheckbox to toggle visualization of translational velocity vector in simulation
    private JCheckBox velocityVector;
    // JCheckbox to toggle visualization of orbital path trace in simulation
    private JCheckBox orbit;

    // Boolean representing whether to display visualization of gravitational
    // force vectors
    private boolean showForceVector = false;
    // Boolean representing whether to display visualization of translational
    // velocity vector
    private boolean showVelocityVector = false;
    // Boolean representing whether to display orbital path trace
    private boolean showOrbit = false;

    // Queue to hold coordinates of last <maxOrbitQueueSize> points in simulation
    // to draw orbital path
    private Queue<Double[]> orbitPoints = new Queue<>();

    // int representing max size of Queue, i.e. the Queue will hold the coordinates
    // of a maximum of these many last points
    private int maxOrbitQueueSize = 2500;

    // double array to hold current mass ratios altered by the two LabeledSliders
    private double[] massRatios = { 1.0, 1.0 };
    // double to hold total simulation time elapsed (interval * no. of iterations)
    private double t;
    // private double holding x-component of current acceleration of Earth
    private double ax;
    // private double holding x-component of current acceleration of Earth
    private double ay;
    // String to be used to display number of figurative Earth days passed.
    private String daysPassed = "Number of Earth days passed: ";
    // int representation of current number of Earth days passed since
    // simulation started (SECONDS_PER_DAY/t)
    private int earthDays;
    // double array holding ratios of x & y parameters to draw planets
    private double[] planetPixels;
    // double holding dimension of long side of visualization arrows
    private double arrowLong;
    // double holding dimension of short side of visualization arrows
    private double arrowShort;
    // constant representing normal mass of Earth
    private double EARTH_NORMAL_MASS;
    // constant representing normal mass of Sun
    private double SUN_NORMAL_MASS;
    // constant representing approximate normal gravitational force between Earth and
    // Sun in reality
    private double NORMAL_F = 3.53E22;
    // double to store ratio of current gravitational force to the normal; used
    // to scale size of gravitational force vector
    private double forceRatio = 1.0;
    // constant representing approximate normal translational velocity between Earth and
    // Sun in reality
    private double NORMAL_V = 29750;
    // double to store ratio of current translational velocity to the normal; used
    // to scale size of translational velocity vector
    private double velocityRatio = 1.0;


    // Method to essentially set simulation to starting position. Re-reads
    // all input parameters, sets all working constants to default values, all
    // Swing GUI elements to their default states, only if first having made sure
    // they have been initialized first.
    @SuppressWarnings("checkstyle:StringLiteralCount")
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
            if (mass[i] <= 0.0)
                throw new IllegalArgumentException("Error: Input body has zero"
                                                           + " or negative mass.");
        }

        EARTH_NORMAL_MASS = mass[0];
        SUN_NORMAL_MASS = mass[1];

        theta = Math.toDegrees(Math.atan(py[0] / px[0]));

        t = 0.0;
        ax = 0.0;
        ay = 0.0;
        massRatios = new double[] { 1.0, 1.0 };
        planetPixels = new double[] { radius / 12, radius / 3 };
        arrowLong = radius / 5;
        arrowShort = radius / 15;
        if (initialized) {
            earthSlider.setValue(10);
            sunSlider.setValue(10);
            mediumSpeed.setSelected(true);
            simulationSpeedPause = 10;
            simulate = false;
            normalMode = true;
            playPauseButton.setIcon(play);
            playMusic.setSelected(false);
            StdAudio.stopInBackground();
            starWars.setIcon(vader);
            planetName = "Earth";
            starName = "Sun";
            song = "2001.wav";
            earthSlider.setLabel(planetName + " Mass = " +
                                         earthSlider.getValue() / 10.0 + "x");
            sunSlider.setLabel(starName + " Mass = " +
                                       sunSlider.getValue() / 10.0 + "x");
            gravityEffect.setSelected(true);
            forceVector.setSelected(false);
            velocityVector.setSelected(false);
            orbit.setSelected(false);
            showForceVector = false;
            showVelocityVector = false;
            showOrbit = false;
            forceRatio = 1.0;
            velocityRatio = 1.0;
            orbitPoints = new Queue<>();
        }
    }

    // Method representing changes in calculations of body parameters per
    // 'frame' or iteration of the simulation
    public void calculationIncrement() {
        // Only operates if simulation is not 'paused'
        if (simulate) {
            fx = new double[n];
            fy = new double[n];

            for (int i = 0; i < n - 1; i++) {
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        // Using Law of Gravitation to determine forces BY
                        // each other body ON all n bodies
                        double dx = px[j] - px[i]; // make sure it's force ON body i
                        double dy = py[j] - py[i];
                        double r = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
                        double bigF = ((BIG_G * mass[i] * mass[j]) / Math.pow(r, 2));
                        fx[i] = fx[i] + (bigF * (dx / r));
                        fy[i] = fy[i] + (bigF * (dy / r));
                    }
                }
            }

            forceRatio = Math.sqrt(Math.pow(fx[0], 2) + Math.pow(fy[0], 2)) / NORMAL_F;
            velocityRatio = Math.sqrt(Math.pow(vx[0], 2) + Math.pow(vy[0], 2)) / NORMAL_V;

            // Angle between star and planet
            theta = Math.toDegrees(Math.atan(py[0] / px[0]));
            if (px[0] < 0)
                theta = 180 + theta;

            // Update velocities and positions.
            for (int i = 0; i < n - 1; i++) {
                ax = (fx[i] / mass[i]);
                ay = (fy[i] / mass[i]);
                vx[i] = vx[i] + ax * interval;
                vy[i] = vy[i] + ay * interval;
                px[i] = px[i] + vx[i] * interval;
                py[i] = py[i] + vy[i] * interval;
            }

            // Angle between North and current direction of translational velocity
            alpha = Math.toDegrees(Math.atan(vy[0] / vx[0]));
            if (vx[0] > 0)
                alpha = 180 + alpha;

        }
    }

    // Function to represent orbital trail drawn for each iteration of simulation.
    public void iterateOrbit() {

        Double[] coordinate = new Double[] { px[0], py[0] };

        if (simulate) {
            // Enqueue newest coordinate
            orbitPoints.enqueue(coordinate);

            // If Queue is at capacity, dequeue oldest coordinate
            if (orbitPoints.size() > maxOrbitQueueSize)
                orbitPoints.dequeue();
        }

        // Iterate through queue and draw all points
        for (Double[] currentCoordinate : orbitPoints)
            draw.point(currentCoordinate[0], currentCoordinate[1]);

        // This function takes linear time with respect to time, as it depends on
        // maxOrbitQueueSize or the number of points currently stored in the queue
        // We settled on 2500 as the size, as we felt it struck a good balance
        // between being long enough to provide a visually noticeable trail
        // as well as keeping the simulation animation smooth
        // A doubling test was performed on the following function (EXTRA CREDIT
        // ITEM) with maxOrbitQueueSize = 2500 & = 5000. The average time elapsed
        // between the drawing step in the for loop above for each iteration was
        // 3.42E-4 seconds and 6.86E-4 seconds for mOQS = 2500 and 5000 respectively.
        // The times can be seen to have doubled when the Queue size was doubled.
        // Hence, it can be seen how the function's computation time varies
        // linearly with the maximum size of the orbitPoints Queue used.
    }

    // Method to draw all components in one iteration of simulation
    @SuppressWarnings("checkstyle:IllegalToken")
    public void drawSimulation() {
        // Draw universe on top, essentially 'blank canvas'
        draw.picture(0, 0, "starfield.jpg");

        for (int i = 0; i < n; i++) {
            // Drawing bodies at current positions, scaled to size based on their
            // current mass ratios and rotated based on angle theta (only for orbiting
            // planet)
            draw.picture(px[i], py[i], image[i], planetPixels[i] *
                                 Math.sqrt(massRatios[i]),
                         planetPixels[i] * Math.sqrt(massRatios[i]), theta * (i ^ 1));

            // If toggled, drawing blue gravitational force vector scaled to size based
            // on current force ratio, rotated to be always pointing towards center of
            // other body
            if (showForceVector)
                draw.picture(px[i], py[i], "blue_arrow.png", arrowLong *
                                     Math.sqrt(forceRatio),
                             arrowShort, (theta + 180 * i));

            // If toggled, drawing green translational velocity vector scaled to size
            // based on current velocity ratio, rotated to be always pointing towards
            // direction of velocity
            if (showVelocityVector && i == 0)
                draw.picture(px[i], py[i], "green_arrow.png", arrowShort,
                             arrowLong * velocityRatio, (alpha + 90));

            // If toggled, drawing orbit trace for all points currently stored
            // in Queue up until current point
            if (showOrbit)
                iterateOrbit();
        }

        // Recalculating and displaying number of Earth days passed
        earthDays = (int) (t / SECONDS_PER_DAY);
        draw.text(0.4 * radius, -0.9 * radius, daysPassed + earthDays);

        // Updating drawing and frame, pausing based on current speed toggled
        draw.show();
        frame.repaint();
        draw.pause(simulationSpeedPause);

        // Incrementing interval if simulation is 'not paused'
        if (simulate)
            t = t + interval;
    }

    // Constructor class, initializes and sets up canvas, Swing GUI and starts
    // simulation loop
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
        draw.setPenRadius(.004);

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

        earthSlider = new LabeledSlider(5, 20, 10);
        earthSlider.addChangeListener(this);
        earthSlider.setLabel(planetName + " Mass = " +
                                     earthSlider.getValue() / 10.0 + "x");

        sunSlider = new LabeledSlider(5, 20, 10);
        sunSlider.addChangeListener(this);
        sunSlider.setLabel(starName + " Mass = " +
                                   sunSlider.getValue() / 10.0 + "x");

        JPanel earthSliderPanel = new JPanel();
        earthSliderPanel.setLayout(new BorderLayout());
        earthSliderPanel.setBounds(550, 350, 300, 100);
        earthSliderPanel.add(earthSlider);

        JPanel sunSliderPanel = new JPanel();
        sunSliderPanel.setLayout(new BorderLayout());
        sunSliderPanel.setBounds(550, 450, 300, 100);
        sunSliderPanel.add(sunSlider);

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
        playPauseButton.setFocusable(false);
        playPauseButtonPanel.add(playPauseButton);
        frame.add(playPauseButtonPanel);

        // Adding radio button panel to control simulation speed

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

        BoxLayout radioPanelLayout = new BoxLayout(speedRadioButtons,
                                                   BoxLayout.Y_AXIS);
        speedRadioButtons.setLayout(radioPanelLayout);

        speedRadioButtons.add(slowSpeed);
        speedRadioButtons.add(mediumSpeed);
        speedRadioButtons.add(fastSpeed);

        mediumSpeed.setSelected(true);
        frame.add(speedRadioButtons);

        // Adding reset button to set simulation back to original starting state

        ImageIcon reset = new ImageIcon("reset.png");
        resetButton = new JButton();
        JPanel resetButtonPanel = new JPanel();
        resetButton.setIcon(reset);
        resetButtonPanel.setLayout(new BorderLayout());
        resetButtonPanel.setBounds(560, 562, 100, 100);

        resetButton.setHorizontalAlignment(JButton.CENTER);
        resetButton.setVerticalAlignment(JButton.CENTER);
        resetButton.addActionListener(this);
        resetButton.setFocusable(false);
        resetButtonPanel.add(resetButton);
        frame.add(resetButtonPanel);

        // Adding checkbox to play according background music during simulation

        playMusic = new JCheckBox();
        playMusic.setText("Background Music");
        playMusic.setFocusable(false);
        playMusic.addActionListener(this);
        JPanel musicCheckBox = new JPanel();
        musicCheckBox.setBounds(355, 595, 160, 35);
        musicCheckBox.add(playMusic);
        frame.add(musicCheckBox);

        // Adding Star Wars button to change visuals of simulation

        starWars = new JButton();
        JPanel starWarsButton = new JPanel();
        starWars.setIcon(vader);
        starWarsButton.setLayout(new BorderLayout());
        starWarsButton.setBounds(710, 562, 100, 100);

        starWars.setHorizontalAlignment(JButton.CENTER);
        starWars.setVerticalAlignment(JButton.CENTER);
        starWars.addActionListener(this);
        starWars.setFocusable(false);
        starWarsButton.add(starWars);
        frame.add(starWarsButton);

        // Adding checkbox panel containing options to display visualizations of
        // gravitational force vectors, translational velocity vectors, and a
        // trace of the planet's orbital path

        gravityEffect = new JCheckBox("Effect of Gravity");
        forceVector = new JCheckBox("Gravitational Force Vectors (Blue)");
        velocityVector = new JCheckBox("Translational Velocity Vector (Green)");
        orbit = new JCheckBox("Orbit Trace (White)");

        gravityEffect.setFocusable(false);
        forceVector.setFocusable(false);
        velocityVector.setFocusable(false);
        orbit.setFocusable(false);

        gravityEffect.addActionListener(this);
        forceVector.addActionListener(this);
        velocityVector.addActionListener(this);
        orbit.addActionListener(this);

        gravityEffect.setSelected(true);

        JPanel gravityParameters = new JPanel();
        BoxLayout gravityParametersLayout = new BoxLayout(gravityParameters,
                                                          BoxLayout.Y_AXIS);
        gravityParameters.setLayout(gravityParametersLayout);

        gravityParameters.setBounds(565, 220, 275, 95);
        gravityParameters.add(gravityEffect);
        gravityParameters.add(forceVector);
        gravityParameters.add(velocityVector);
        gravityParameters.add(orbit);

        frame.add(gravityParameters);

        // Adding text panel to provide brief info about project

        JPanel topTextPanel = new JPanel();
        topTextPanel.setLayout(new BorderLayout());
        topTextPanel.setBounds(550, 50, 300, 80);

        JLabel topText = new JLabel();
        topText.setHorizontalTextPosition(JLabel.CENTER);
        topText.setVerticalTextPosition(JLabel.CENTER);
        topText.setHorizontalAlignment(JLabel.CENTER);
        topText.setVerticalAlignment(JLabel.CENTER);
        topTextPanel.add(topText);

        Font current = draw.getFont();
        topText.setFont(new Font(current.getName(), Font.BOLD, 22));
        topText.setText(
                "<HTML> Customizable Earth-Sun<br> &nbsp; &nbsp; &nbsp; Orbit Simulation</HTML>");

        JPanel bottomTextPanel = new JPanel();
        bottomTextPanel.setLayout(new BorderLayout());
        bottomTextPanel.setBounds(550, 130, 300, 50);

        JLabel bottomText = new JLabel();
        bottomText.setHorizontalTextPosition(JLabel.CENTER);
        bottomText.setVerticalTextPosition(JLabel.CENTER);
        bottomText.setHorizontalAlignment(JLabel.CENTER);
        bottomText.setVerticalAlignment(JLabel.CENTER);
        bottomTextPanel.add(bottomText);

        bottomText.setFont(new Font(current.getName(), Font.PLAIN, 12));
        bottomText.setText(
                "<HTML> Made as a final project for COS 126<br> &nbsp; &nbsp; "
                        + "Fall '22 at Princeton University.</HTML>");


        frame.add(topTextPanel);
        frame.add(bottomTextPanel);

        frame.setVisible(true);
        stateChanged(null);
        drawSimulation();

        // Actual simulation process

        while (true) {
            calculationIncrement();
            drawSimulation();
        }

    }

    // Method processing changes in LabeledSlider Swing GUI elements
    public void stateChanged(ChangeEvent e) {
        // Updating slider value and label, as well as current mass of Earth
        massRatios[0] = earthSlider.getValue() / 10.0;
        earthSlider.setLabel(planetName + " Mass = " +
                                     earthSlider.getValue() / 10.0 + "x");
        mass[0] = EARTH_NORMAL_MASS * massRatios[0];

        // Updating slider value and label, as well as current mass of Sun
        massRatios[1] = sunSlider.getValue() / 10.0;
        sunSlider.setLabel(starName + " Mass = " +
                                   sunSlider.getValue() / 10.0 + "x");
        mass[1] = SUN_NORMAL_MASS * massRatios[1];

        frame.repaint();   // repaints sliders and canvas
    }

    // Method processing all other changes in Swing GUI elements
    public void actionPerformed(ActionEvent e) {
        // Toggling between play and pause button, updating button icon and
        // boolean simulate's value
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

        // Adjusting simulationSpeedPause duration, i.e. simulation speed based
        // on current level toggled
        if (e.getSource() == slowSpeed)
            simulationSpeedPause = 20;
        else if (e.getSource() == mediumSpeed)
            simulationSpeedPause = 10;
        else if (e.getSource() == fastSpeed)
            simulationSpeedPause = 1;

        // Resetting simulation if button is pressed
        if (e.getSource() == resetButton) {
            resetPlanets();
        }

        // Switching music on and off if checkbox is toggled
        if (e.getSource() == playMusic) {
            if (playMusic.isSelected()) {
                StdAudio.playInBackground(song);
            }
            else
                StdAudio.stopInBackground();
        }

        // Processing toggle between StarWars and Normal modes
        if (e.getSource() == starWars) {
            // When toggled between Normal and Star Wars, updating planet
            // and star images,updating song filename, updating slider labels.
            if (normalMode) {
                starWars.setIcon(normal);
                normalMode = false;
                image[0] = "falcon.png";
                image[1] = "deathstar.png";
                song = "imperialmarch.wav";
                planetName = "Millenium Falcon";
                starName = "Death Star";
                earthSlider.setLabel(planetName + " Mass = " +
                                             earthSlider.getValue() / 10.0 + "x");
                sunSlider.setLabel(starName + " Mass = " +
                                           sunSlider.getValue() / 10.0 + "x");
                if (playMusic.isSelected()) {
                    StdAudio.stopInBackground();
                    StdAudio.playInBackground(song);
                }
            }
            else {
                starWars.setIcon(vader);
                normalMode = true;
                image[0] = "earth.png";
                image[1] = "sun.png";
                song = "2001.wav";
                planetName = "Earth";
                starName = "Sun";
                earthSlider.setLabel(planetName + " Mass = " +
                                             earthSlider.getValue() / 10.0 + "x");
                sunSlider.setLabel(starName + " Mass = " +
                                           sunSlider.getValue() / 10.0 + "x");
                if (playMusic.isSelected()) {
                    StdAudio.stopInBackground();
                    StdAudio.playInBackground(song);
                }
            }
        }

        // Switching effect of gravity on and off when checkbox is toggled
        if (e.getSource() == gravityEffect) {
            if (!gravityEffect.isSelected()) {
                // Setting BIG_G and gravitational force to zero to truly
                // remove effect of gravity
                BIG_G = 0.0;
                fx[0] = 0.0;
                fy[0] = 0.0;
            }
            else if (gravityEffect.isSelected()) {
                BIG_G = 6.67E-11;
            }
        }

        // Switching visualization of gravitational force vector on and off
        // when checkbox is toggled
        if (e.getSource() == forceVector) {
            if (forceVector.isSelected()) {
                showForceVector = true;
            }
            else if (!forceVector.isSelected()) {
                showForceVector = false;
            }
        }

        // Switching visualization of translational velocity vector on and off
        // when checkbox is toggled
        if (e.getSource() == velocityVector) {
            if (velocityVector.isSelected()) {
                showVelocityVector = true;
            }
            else if (!velocityVector.isSelected()) {
                showVelocityVector = false;
            }
        }

        // Switching visualization of orbital path trace on and off
        // when checkbox is toggled
        if (e.getSource() == orbit) {
            if (orbit.isSelected()) {
                showOrbit = true;
            }
            else if (!orbit.isSelected()) {
                showOrbit = false;
                orbitPoints = new Queue<>();
            }
        }

    }

    // Main method to create instance of NBody() and start simulation
    public static void main(String[] args) {
        new NBody();
    }
}

