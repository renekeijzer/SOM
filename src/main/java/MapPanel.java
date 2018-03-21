import Jama.Matrix;

import javax.swing.*;
import java.awt.*;
import ml.BasicSelfOrganizingMap;

public class MapPanel extends JPanel {
    private final BasicSelfOrganizingMap network;
    private final int width;
    private final int height;
    private final int cellSize;
    private boolean displayNumbers;

    public MapPanel(BasicSelfOrganizingMap theNetwork, int theCellSize, int theWidth, int theHeight)
    {
        this.network = theNetwork;
        this.width = theWidth;
        this.height = theHeight;
        this.cellSize = theCellSize;
    }

    private int convertColor(double d)
    {
        double result = 128*d;
        result+=128;
        result = Math.min(result, 255);
        result = Math.max(result, 0);
        return (int)result;
    }

    public boolean isDisplayNumbers() {
        return this.displayNumbers;
    }

    public void setDisplayNumbers(final boolean displayNumbers) {
        this.displayNumbers = displayNumbers;
    }

    @Override
    public void paint(Graphics g)
    {
        FontMetrics fm = g.getFontMetrics();

        int idx = 0;
        Matrix weights = this.network.getWeights();
        for(int y = 0; y< this.height; y++)
        {
            for(int x = 0; x< this.width; x++)
            {
                int index = (y*this.width)+x;
                int red = convertColor(weights.get(index,0));
                int green = convertColor(weights.get(index,1));
                int blue = convertColor(weights.get(index,2));
                g.setColor(new Color(red,green,blue));
                g.fillRect(x*this.cellSize, y*this.cellSize, this.cellSize, this.cellSize);

                if( isDisplayNumbers() ) {
                    g.setColor(Color.BLACK);
                    g.drawString(""+idx,x*this.cellSize, (y*this.cellSize)+fm.getHeight());
                    idx++;
                }
            }
        }
    }
}