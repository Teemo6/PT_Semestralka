/**
 * Instance třídy {@code GrafMapa} představuje vrchol neorientovaného grafu
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.22 14-11-2022
 */
public class GrafVrchol {
    AMisto vrchol;
    double vzdalenost;

    /**
     * Konstruktor
     * @param vr místo na mapě
     * @param vz vzdálenost od spojeného bodu
     */
    GrafVrchol(AMisto vr, double vz){
        this.vrchol = vr;
        this.vzdalenost = vz;
    }

    /**
     * Vrátí místo na mapě
     * @return místo na mapě
     */
    public AMisto getVrchol() {
        return vrchol;
    }

    /**
     * Vrátí vzdálenost od počátku
     * @return vzdálenost od počátku
     */
    public double getVzdalenost() {
        return vzdalenost;
    }

    @Override
    public String toString() {
        return "Misto = " + vrchol + ", dist = " + vzdalenost;
    }
}
