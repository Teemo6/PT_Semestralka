/**
 * Instance třídy {@code Cesta} představuje jednosměrnou cestu mezi dvěmi libovolnými body
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.22 14-11-2022
 */
public class Cesta {
    private final AMisto zacatek;
    private final AMisto konec;
    private final double vzdalenost;

    /**
     * Konstruktor
     * @param zacatek začátek cesty
     * @param konec konec cesty
     */
    public Cesta(AMisto zacatek, AMisto konec){
        this.zacatek = zacatek;
        this.konec = konec;
        vzdalenost = zacatek.getPozice().computeDistance(konec.getPozice());
    }

    /**
     * Vrátí cestu v opačném směru
     * @return cesta z konce do začátku
     */
    public Cesta prohodSmer(){
        return new Cesta(konec, zacatek);
    }

    /**
     * Vrátí vypočítanou vzdálenost cesty
     * @return ohodnocení cesty
     */
    public double getVzdalenost(){
        return vzdalenost;
    }

    /**
     * Vrátí místo A
     * @return místo A
     */
    public AMisto getZacatek() {
        return zacatek;
    }

    /**
     * Vrátí místo B
     * @return místo B
     */
    public AMisto getKonec() {
        return konec;
    }

    @Override
    public String toString() {
        return getZacatek().getID() + " -> " + getKonec().getID();
    }
}
