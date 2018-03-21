package general.random;

public interface GenerateRandom {

    double nextGaussian();

    boolean nextBoolean();

    long nextLong();

    float nextFloat();

    double nextDouble();
    double nextDouble(double high);
    double nextDouble(double low, double high);

    int nextInt();
    int nextInt(int high);
    int nextInt(int low, int high);

}
