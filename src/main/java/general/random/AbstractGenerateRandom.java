package general.random;

public abstract class AbstractGenerateRandom implements GenerateRandom {

    @Override
    public int nextInt(final int low, final int high) {
        return (low + (int) (nextDouble() * ((high - low))));
    }

    @Override
    public double nextDouble(final double high) {
        return nextDouble(0, high);
    }

    @Override
    public double nextDouble(final double low, final double high) {
        return (low + (nextDouble() * ((high - low))));
    }

    @Override
    public int nextInt(final int range) {
        return nextInt(0, range);
    }
}
