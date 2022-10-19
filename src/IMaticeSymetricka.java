/**
 * Interface pro symetrickou matici
 * @author Mikuláš Mach, Štěpán Faragula
 * @version 1.11 18-10-2022
 */
public interface IMaticeSymetricka {
    /**
     * Nastaví všechny hodnoty na INF
     */
    void vyplnNekonecnem();

    /**
     * Nastaví hodnotu 0 na diagonálu matice
     */
    void vyplnNulyNaDiagonalu();

    /**
     * Nastaví číslo podle souřadnic X, Y
     * matice je symetrická, nastaví stejné číslo na Y, X
     * @param x x
     * @param y y
     * @param cislo hodnota
     */
    void setCislo(int x, int y, double cislo);

    /**
     * Nastaví číslo na diagonálu X, X
     * @param x x
     * @param cislo hodnota
     */
    void setCislo(int x, double cislo);

    /**
     * Vrátí číslo na souřadnicích X, Y
     * matice je symetrická, vrátí stejné číslo z Y, X
     * @param x x
     * @param y y
     * @return hodnota
     */
    double getCislo(int x, int y);

    /**
     * Vypíše do konzole čtvercovou, symetrickou matici
     */
    void printMatice();

    /**
     * Velikost čtvercové matice
     * @return N
     */
    int getVelikost();
}
