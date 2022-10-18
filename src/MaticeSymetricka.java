import java.util.Arrays;

/**
 * Instance třídy {@code MaticeSymetricka} představuje symetrickou matici s lepším využitím paměti
 * @author Mikuláš Mach, Štěpán Faragula
 * @version 1.10 18-10-2022
 */
public class MaticeSymetricka {
    private int velikost;
    private double[] obsahMatice;

    /**
     * @param n velikost čtvercové matice
     */
    public MaticeSymetricka(int n){
        velikost = n;
        obsahMatice = new double[n*(n+1)/2];
    }

    /**
     * Nastaví všechny hodnoty na INF
     */
    public void vyplnNekonecnem(){
        Arrays.fill(obsahMatice, Double.MAX_VALUE);
    }

    /**
     * Vrátí index v 1D poli podle souřadnic X, Y
     * @param x x
     * @param y y
     * @return index v 1D poli
     */
    public int getIndex(int x, int y){
        if (x <= y) {
            return x * velikost - (x - 1) * ((x - 1) + 1) / 2 + y - x;
        }
        return y * velikost - (y-1)*((y-1) + 1)/2 + x - y;
    }

    /**
     * Nastaví číslo podle souřadnic X, Y
     * @param x x
     * @param y y
     * @param cislo hodnota
     */
    public void setCisloXY(int x, int y, double cislo){
        obsahMatice[getIndex(x, y)] = cislo;
    }

    /**
     * Vrátí číslo na souřadnicích X, Y
     * @param x x
     * @param y y
     * @return hodnota
     */
    public double getCisloXY(int x, int y){
        return obsahMatice[getIndex(x, y)];
    }

    /**
     * Vypíše do konzole čtvercovou, symetrickou matici
     */
    public void printMatice(){
        for(int i = 0; i < velikost; i++){
            for(int j = 0; j < velikost; j++){
                double cislo = obsahMatice[getIndex(i, j)];
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

    /**
     * Vrátí array matice
     * @return 1D pole
     */
    public double[] getObsahMatice() {
        return obsahMatice;
    }

}
