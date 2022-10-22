import java.util.Arrays;

/**
 * Instance třídy {@code MaticeCtvercova} představuje jedináčka, která obsahuje matici sousednosti
 * @author Mikuláš Mach, Štěpán Faragula
 * @version 1.11 18-10-2022
 */
public class MaticeCtvercova implements IMaticeSymetricka{
    private int velikost;
    private double[][] obsahMatice;

    public MaticeCtvercova(int velikost){
        this.velikost = velikost;
        obsahMatice = new double[velikost][velikost];
    }

    /**
     * Nastaví všechny hodnoty na INF
     */
    public void vyplnNekonecnem(){
        for (double[] x : obsahMatice) {
            Arrays.fill(x, Double.MAX_VALUE);
        }
    }

    /**
     * Nastaví všechny hodnoty na diagonále na 0
     */
    public void vyplnNulyNaDiagonalu(){
        for(int i = 0; i < velikost; i++){
            setCislo(i, i, 0);
        }
    }

    /**
     * Nastaví číslo podle souřadnic X, Y
     * matice je symetrická, nastaví stejné číslo na Y, X
     * @param x x
     * @param y y
     * @param cislo hodnota
     */
    public void setCislo(int x, int y, double cislo){
        obsahMatice[x][y] = cislo;
    }

    /**
     * Nastaví číslo podle souřadnic X, Y
     * matice je symetrická, nastaví stejné číslo na Y, X
     * @param x x
     * @param y y
     * @param cislo hodnota
     */
    public void setCisloSymetricky(int x, int y, double cislo){
        obsahMatice[x][y] = cislo;
        obsahMatice[y][x] = cislo;
    }

    /**
     * Vrátí číslo na souřadnicích X, Y
     * matice je symetrická, vrátí stejné číslo z Y, X
     * @param x x
     * @param y y
     * @return hodnota
     */
    public double getCislo(int x, int y){
        return obsahMatice[x][y];
    }

    /**
     * Vypíše do konzole čtvercovou, symetrickou matici
     */
    public void printMatice(){
        for(int i = 0; i < velikost; i++){
            for(int j = 0; j < velikost; j++){
                double cislo = obsahMatice[i][j];
                if(cislo == Double.MAX_VALUE){
                    System.out.print("INF");
                } else {
                    System.out.print(cislo);
                }
                if(j != velikost - 1){
                    System.out.print(" - ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Velikost čtvercové matice
     * @return N
     */
    public int getVelikost() {
        return velikost;
    }

    public double[][] getObsahMatice() {
        return obsahMatice;
    }
}
