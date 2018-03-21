package ml.Neighborhood;

public interface NeighborhoodFunction {
    double function(int currentNeuron, int bestNeuron);
    double getRadius();
    void setRadius(double radius);
}
