
import general.random.GenerateRandom;
import ml.BasicSelfOrganizingMap;
import ml.BasicTrainSom;
import general.random.MersenneTwisterGenerateRandom;
import general.RBFEnum;
import ml.Neighborhood.NeighborhoodRBF;
import general.normalize.DataSet;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import general.data.BasicData;

import javax.swing.*;

public class ClusterNations extends JFrame implements Runnable {

    public static final int WIDTH = 10;
    public static final int HEIGHT = 10;
    public static final int START_WIDTH = 9;
    public static final int END_WIDTH = 3;
    public static final int CYCLES = 1000;
    public static final double START_RATE = 0.8;
    public static final double END_RATE = 0.01;


    private final HexPanel map;
    private final BasicSelfOrganizingMap network;
    private final Thread thread;
    private final BasicTrainSom train;
    private NeighborhoodRBF gaussian;
    private final int buckets;
    private List<BasicData> trainingData;

    public ClusterNations() {
        this.setSize(750, 300);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        loadTraining();

        this.gaussian = new NeighborhoodRBF(RBFEnum.Gaussian, ClusterNations.WIDTH,
                ClusterNations.HEIGHT);

        this.buckets = WIDTH * HEIGHT;
        this.network = new BasicSelfOrganizingMap(9,this.buckets);
        this.network.reset();

        this.gaussian = new NeighborhoodRBF(RBFEnum.Gaussian,WIDTH,HEIGHT);
        this.gaussian.setHexagon(true);

        this.train = new BasicTrainSom(this.network, 0.01, this.trainingData, this.gaussian);
        this.train.setAutoDecay(CYCLES,START_RATE,END_RATE,START_WIDTH,END_WIDTH);
        this.train.setForceWinner(false);

        this.getContentPane().add(this.map = new HexPanel(this.network,32,WIDTH,HEIGHT));
        this.map.setDisplayNumbers(true);

        this.thread = new Thread(this);
        this.thread.start();
    }

    /**
     * Run the example.
     */
    public void loadTraining() {
        try {
            final InputStream istream = this.getClass().getResourceAsStream("/gamedata.csv");
            if (istream == null) {
                System.out.println("Cannot access data set, make sure the resources are available.");
                System.exit(1);
            }

            GenerateRandom rnd = new MersenneTwisterGenerateRandom();

            final DataSet ds = DataSet.load(istream);
            ds.deleteUnknowns();

            ds.normalizeRange(1, 0, 100);
            ds.normalizeRange(2, 0, 1);
            ds.normalizeRange(3, 0, 100);
            ds.normalizeRange(4, 0, 1);
            ds.normalizeRange(5, 0, 100);
            ds.normalizeRange(6, 0, 1);
            ds.normalizeRange(7, 0, 100);
            ds.normalizeRange(8, 0, 1);
            ds.normalizeRange(9, 0, 100);

            istream.close();

            this.trainingData = ds.extractUnsupervisedLabeled(0);
        } catch (Throwable t) {
            t.printStackTrace();
        }


    }

    public static void main(String[] args) {
        ClusterNations nations = new ClusterNations();
        nations.setVisible(true);
    }

    @Override
    public void run() {

        for (int i = 0; i < CYCLES; i++) {
            this.train.iteration();
            this.train.autoDecay();
            this.map.repaint();
            System.out.println("Iteration " + i + "," + this.train.toString());
        }


        for(int i = 0; i< this.buckets; i++) {
            List<String> nations = new ArrayList<>();
            for(BasicData nation: this.trainingData) {
                if(this.network.classify(nation.getInput())==i ) {
                    nations.add(nation.getLabel());
                }
            }
            System.out.println("Cluster #" + i + ": " + nations.toString());
        }
    }
}