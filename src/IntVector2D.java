/**
 * Instance třídy {@code IntVector2D} představuje přepravku pro souřadnice x, y
 * @author Štěpán Faragula 01-10-2022
 * @version 1.00
 */

public class IntVector2D {
    private final int x;
    private final int y;

    /**
     * Nastaví atributy x, y
     * @param x x
     * @param y y
     */
    public IntVector2D(int x, int y){
        this.x = x;
        this.y = y;
    }

    /**
     * @return x
     */
    public int getX() {
        return x;
    }

    /**
     * @return y
     */
    public int getY() {
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
