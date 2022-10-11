/**
 * Instance třídy {@code DoubleVector2D} představuje přepravku pro souřadnice x, y
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.01 11-10-2022
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
