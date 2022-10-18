import java.util.Arrays;

/**
 * Instance třídy {@code Matice} představuje jedináčka, která obsahuje matici sousednosti
 * @author Mikuláš Mach
 * @version 1.00 17-10-2022
 */
public class Matice {
    private int velikostX;
    private int velikostY;
    private double[][] obsahMatice;

    public Matice(int velikostX, int velikostY){
        this.velikostX = velikostX;
        this.velikostY = velikostY;
        obsahMatice = new double[velikostX][velikostY];
    }

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

    public void pridejObsah(AMisto bod1, int idX, AMisto bod2, int idY){
        double vzdalenost = bod1.getPozice().computeDistance(bod2.getPozice());
        obsahMatice[idX][idY] = vzdalenost;
        obsahMatice[idY][idX] = vzdalenost;
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
                System.out.print(obsahMatice[x][y] + ", ");
            }
            System.out.println();
        }
    }
}
