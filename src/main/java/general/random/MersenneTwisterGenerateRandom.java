package general.random;

public class MersenneTwisterGenerateRandom extends AbstractBoxMuller {
    private static final int N = 624;
    private static final int M = 397;
    private static final int MATRIX_A = 0x9908b0df;
    private static final int UPPER_MASK = 0x80000000;
    private static final int LOWER_MASK = 0x7fffffff;
    private static final int TEMPERING_MASK_B = 0x9d2c5680;
    private static final int TEMPERING_MASK_C = 0xefc60000;

    private int stateVector[];
    private int mti;
    private int mag01[];

    public MersenneTwisterGenerateRandom() {
        this(System.currentTimeMillis());
    }

    public MersenneTwisterGenerateRandom(final long seed) {
        setSeed(seed);
    }

    public MersenneTwisterGenerateRandom(final int[] array) {
        setSeed(array);
    }

    public void setSeed(final long seed) {
        this.stateVector = new int[N];

        this.mag01 = new int[2];
        this.mag01[0] = 0x0;
        this.mag01[1] = MATRIX_A;

        this.stateVector[0] = (int) seed;
        for (this.mti = 1; this.mti < N; this.mti++) {
            this.stateVector[this.mti] =
                    (1812433253 * (this.stateVector[this.mti - 1] ^ (this.stateVector[this.mti - 1] >>> 30)) + this.mti);
        }
    }

    public void setSeed(final int[] array) {
        int i, j, k;
        setSeed(19650218);
        i = 1;
        j = 0;
        k = (N > array.length ? N : array.length);
        for (; k != 0; k--) {
            this.stateVector[i] = (this.stateVector[i] ^ ((this.stateVector[i - 1] ^ (this.stateVector[i - 1] >>> 30)) * 1664525)) + array[j] + j;
            i++;
            j++;
            if (i >= N) {
                this.stateVector[0] = this.stateVector[N - 1];
                i = 1;
            }
            if (j >= array.length) j = 0;
        }
        for (k = N - 1; k != 0; k--) {
            this.stateVector[i] = (this.stateVector[i] ^ ((this.stateVector[i - 1] ^ (this.stateVector[i - 1] >>> 30)) * 1566083941)) - i;
            i++;
            if (i >= N) {
                this.stateVector[0] = this.stateVector[N - 1];
                i = 1;
            }
        }
        this.stateVector[0] = 0x80000000;
    }

    protected int next(final int bits) {
        int y;

        if (this.mti >= N) {
            int kk;

            for (kk = 0; kk < N - M; kk++) {
                y = (this.stateVector[kk] & UPPER_MASK) | (this.stateVector[kk + 1] & LOWER_MASK);
                this.stateVector[kk] = this.stateVector[kk + M] ^ (y >>> 1) ^ this.mag01[y & 0x1];
            }
            for (; kk < N - 1; kk++) {
                y = (this.stateVector[kk] & UPPER_MASK) | (this.stateVector[kk + 1] & LOWER_MASK);
                this.stateVector[kk] = this.stateVector[kk + (M - N)] ^ (y >>> 1) ^ this.mag01[y & 0x1];
            }
            y = (this.stateVector[N - 1] & UPPER_MASK) | (this.stateVector[0] & LOWER_MASK);
            this.stateVector[N - 1] = this.stateVector[M - 1] ^ (y >>> 1) ^ this.mag01[y & 0x1];

            this.mti = 0;
        }

        y = this.stateVector[this.mti++];
        y ^= y >>> 11;
        y ^= (y << 7) & TEMPERING_MASK_B;
        y ^= (y << 15) & TEMPERING_MASK_C;
        y ^= (y >>> 18);

        return y >>> (32 - bits);
    }

    @Override
    public double nextDouble() {
        return (((long) next(26) << 27) + next(27))
                / (double) (1L << 53);
    }

    public long nextLong() {
        return ((long) next(32) << 32) + next(32);
    }

    @Override
    public boolean nextBoolean() {
        return nextDouble() > 0.5;
    }

    @Override
    public float nextFloat() {
        return (float) nextDouble();
    }

    @Override
    public int nextInt() {
        return (int) nextLong();
    }
}
