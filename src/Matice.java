import java.util.Arrays;

/**
 * Instance třídy {@code Matice} představuje jedináčka, která obsahuje matici sousednosti
 * @author Mikuláš Mach, Štěpán Faragula
 * @version 1.00 17-10-2022
 */
public class Matice {
    private int velikostX;
    private int velikostY;
    private double[][] obsahMatice;

    public Matice(int velikost){
        this.velikostX = velikost;
        this.velikostY = velikost;
        obsahMatice = new double[velikost][velikost];
    }

    public void vyplnNekonecnem(){
        for (double[] x : obsahMatice) {
            Arrays.fill(x, Double.MAX_VALUE);
        }
    }

    public void setCisloXY(int x, int y, double cislo){
        obsahMatice[x][y] = cislo;
        obsahMatice[y][x] = cislo;
    }

    public double getCisloXY(int x, int y){
        return obsahMatice[x][y];
    }

    public int getVelikostX() {
        return velikostX;
    }

    public int getVelikostY() {
        return velikostY;
    }

    public double[][] getObsahMatice() {
        return obsahMatice;
    }

    public void printMatice(){
        for (int x = 0; x < velikostX; x++){
            for (int y = 0; y < velikostY; y++){
                if(obsahMatice[x][y] == Double.MAX_VALUE){
                    System.out.print("INF, ");
                    continue;
                }
                System.out.print(obsahMatice[x][y] + ", ");
            }
            System.out.println();
        }
    }
}
