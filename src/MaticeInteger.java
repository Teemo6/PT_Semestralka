import java.util.Arrays;

/**
 * Instance třídy {@code MaticeInteger} čtvercovou matici intů
 * @author Mikuláš Mach, Štěpán Faragula
 * @version 1.20 06-11-2022
 */
public class MaticeInteger {
    private final int velikost;
    private final int[][] obsahMatice;

    public MaticeInteger(int velikost){
        this.velikost = velikost;
        obsahMatice = new int[velikost][velikost];
    }

    /**
     * Nastaví všechny hodnoty na INF
     */
    public void vyplnNekonecnem(){
        for (int[] x : obsahMatice) {
            Arrays.fill(x, Integer.MAX_VALUE);
        }
    }

    /**
     * Nastaví všechny hodnoty na diagonále na 0
     */
    public void vyplnNulyNaDiagonalu(){
        for(int i = 0; i < velikost; i++){
            setCislo(i, i,  0);
        }
    }

    /**
     * Nastaví číslo podle souřadnic X, Y
     * @param x x
     * @param y y
     * @param cislo hodnota
     */
    public void setCislo(int x, int y, int cislo){
            obsahMatice[x][y] = cislo;
        }

    /**
     * Nastaví číslo podle souřadnic X, Y
     * @param x x
     * @param y y
     * @param cislo hodnota
     */
    public void setCisloSymetricky(int x, int y, int cislo){
        obsahMatice[x][y] = cislo;
        obsahMatice[y][x] = cislo;
    }

    /**
     * Vrátí číslo na souřadnicích X, Y
     * @param x x
     * @param y y
     * @return hodnota
     */
    public int getCislo(int x, int y){
            return obsahMatice[x][y];
        }

    /**
     * Vypíše do konzole obsah matice
     */
    public void printMatice(){
        for(int i = 0; i < velikost; i++){
            for(int j = 0; j < velikost; j++){
                int cislo = obsahMatice[i][j];
                if(cislo == Integer.MAX_VALUE){
                    System.out.print("INF");
                } else {
                    System.out.printf("%3d", cislo);
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
}
