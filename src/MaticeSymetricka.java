import java.util.Arrays;

/**
 * Instance třídy {@code Matice} představuje jedináčka, která obsahuje matici sousednosti
 * @author Mikuláš Mach, Štěpán Faragula
 * @version 1.00 17-10-2022
 */
public class MaticeSymetricka {
    private int velikost;
    private double[] obsahMatice;

    public MaticeSymetricka(int n){
        velikost = n;
        obsahMatice = new double[n*(n+1)/2];
    }

    public void vyplnNekonecnem(){
        Arrays.fill(obsahMatice, Double.MAX_VALUE);
    }

    public int getIndex(int x, int y){
        if (x <= y) {
            return x * velikost - (x - 1) * ((x - 1) + 1) / 2 + y - x;
        }
        return y * velikost - (y-1)*((y-1) + 1)/2 + x - y;
    }

    public void setCisloXY(int x, int y, double cislo){
        obsahMatice[getIndex(x, y)] = cislo;
    }

    public double getCisloXY(int x, int y){
        return obsahMatice[getIndex(x, y)];
    }

    public int getVelikost() {
        return velikost;
    }

    public double[] getObsahMatice() {
        return obsahMatice;
    }
}
