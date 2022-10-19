import java.util.Arrays;

/**
 * Instance třídy {@code MaticeTrojuhelnikova} představuje symetrickou matici s lepším využitím paměti
 * @author Mikuláš Mach, Štěpán Faragula
 * @version 1.10 18-10-2022
 */
public class MaticeTrojuhelnikova implements IMaticeSymetricka {
    private int velikost;
    private double[] obsahMatice;

    /**
     * @param n velikost čtvercové matice
     */
    public MaticeTrojuhelnikova(int n){
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
     * Nastaví všechny hodnoty na INF
     */
    public void vyplnNulyNaDiagonalu(){
        for(int x = 0; x < velikost; x++){
            setCislo(x, 0);
        }
    }

    /**
     * Vrátí index v 1D poli podle souřadnic X, Y
     * @param x x
     * @param y y
     * @return index v 1D poli
     */
    public int getIndex(int x, int y){
        int i;
        if (x <= y) {
            i = x * velikost - ((x - 1) * x >> 1) + y - x;
        } else {
            i = y * velikost - ((y - 1) * y >> 1) + x - y;
        }
        return i;
    }

    /**
     * Nastaví číslo podle souřadnic X, Y
     * matice je symetrická, nastaví stejné číslo na Y, X
     * @param x x
     * @param y y
     * @param cislo hodnota
     */
    public void setCislo(int x, int y, double cislo){
        obsahMatice[getIndex(x, y)] = cislo;
    }

    /**
     * Nastaví číslo na diagonálu X, X
     * @param x x
     * @param cislo hodnota
     */
    public void setCislo(int x, double cislo){
        obsahMatice[getIndex(x, x)] = cislo;
    }

    /**
     * Vrátí číslo na souřadnicích X, Y
     * matice je symetrická, vrátí stejné číslo z Y, X
     * @param x x
     * @param y y
     * @return hodnota
     */
    public double getCislo(int x, int y){
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
