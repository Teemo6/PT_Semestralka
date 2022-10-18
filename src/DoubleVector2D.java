/**
 * Instance třídy {@code DoubleVector2D} představuje přepravku pro souřadnice x, y typu double
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.10 18-10-2022
 */

public class DoubleVector2D {
    private final double x;
    private final double y;

    /**
     * Nastaví atributy x, y
     * @param x x
     * @param y y
     */
    public DoubleVector2D(double x, double y){
        this.x = x;
        this.y = y;
    }

    /**
     * Vrátí vzdálenost mezi dvěmi body {@code DoubleVector2D}
     * @param otherVector instance druhého vektoru
     * @return vzdálenost mezi touto instancí {@code DoubleVector2D} a jinou instancí {@code DoubleVector2D}
     */
    public double computeDistance(DoubleVector2D otherVector){
        double posXDiff = otherVector.getX() - x;
        double posYDiff = otherVector.getY() - y;
        return Math.sqrt(posXDiff * posXDiff + posYDiff * posYDiff);
    }


    /**
     * @return x
     */
    public double getX() {
        return x;
    }

    /**
     * @return y
     */
    public double getY() {
        return y;
    }

    /**
     * @return {@code String} v podobě: (x, y)
     */
    @Override
    public String toString(){
        return "(" +x+ ", " +y+")";
    }
}
