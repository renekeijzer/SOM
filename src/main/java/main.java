import general.random.GenerateRandom;
import ml.BasicSelfOrganizingMap;
import ml.BasicTrainSom;
import general.random.MersenneTwisterGenerateRandom;
import general.RBFEnum;
import ml.Neighborhood.NeighborhoodRBF;

import javax.swing.*;

class SomColors extends JFrame implements Runnable {

    /**
     *
     */
    private static final long serialVersionUID = -6762179069967224817L;
    public static final int WIDTH = 10;
    public static final int HEIGHT = 10;
    private final MapPanel map;
    private final BasicSelfOrganizingMap network;
    private final Thread thread;
    private final BasicTrainSom train;
    private final NeighborhoodRBF gaussian;



    public SomColors() {
        this.setSize(640, 480);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.network = createNetwork();
        this.getContentPane().add(this.map = new MapPanel(this.network,30,WIDTH,HEIGHT));
        this.gaussian = new NeighborhoodRBF(RBFEnum.MexicanHat, WIDTH,HEIGHT);
        this.train = new BasicTrainSom(this.network, 0.01, null, this.gaussian);
        this.train.setForceWinner(false);
        this.thread = new Thread(this);
        this.thread.start();
    }

    public BasicSelfOrganizingMap getNetwork() {
        return this.network;
    }

    private BasicSelfOrganizingMap createNetwork() {
        BasicSelfOrganizingMap result = new BasicSelfOrganizingMap(3,WIDTH * HEIGHT);
        result.reset();
        return result;
    }

    public static void main(String[] args) {
        SomColors frame = new SomColors();
        frame.setVisible(true);
    }

    public void run() {
        GenerateRandom rnd = new MersenneTwisterGenerateRandom();
        this.map.setDisplayNumbers(true);
        double[][] samples = new double[15][3];
        for (int i = 0; i < 15; i++) {
            samples[i][0] = rnd.nextDouble(-1,1);
            samples[i][1] = rnd.nextDouble(-1,1);
            samples[i][2] = rnd.nextDouble(-1,1);
        }

        this.train.setAutoDecay(1000, 0.8, 0.003, 30, 5);

        for (int i = 0; i < 1000; i++) {
            int idx = (int) (Math.random() * samples.length);
            double[] c = samples[idx];

            this.train.trainPattern(c);
            this.train.autoDecay();
            this.map.repaint();

            System.out.println("Iteration " + i + "," + this.train.toString());
        }
    }
}