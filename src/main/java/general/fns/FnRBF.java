package general.fns;

public interface FnRBF extends Fn {

    double getCenter(int dimension);

    void setCenter(int dimension, double value);

    int getDimensions();

    double getWidth();

    void setWidth(double theWidth);
}
