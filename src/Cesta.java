/**
 * Instance třídy {@code Cesta} představuje cestu mezi dvěmi libovolnými body
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.14 01-11-2022
 */
public class Cesta {
    protected final AMisto mistoA;
    protected final AMisto mistoB;
    protected final double vzdalenost;

    /**
     * Symterická cesta mezi dvěmi body
     * místa se ukládají tak, aby jejich ID mohli být souřadnice X, Y horního trojúhelníku matice
     * @param mistoA místo A propojené s místem B
     * @param mistoB místo B propojené s místem A
     */
    public Cesta(AMisto mistoA, AMisto mistoB){
        this.mistoA = mistoA;
        this.mistoB = mistoB;
        vzdalenost = mistoA.getPozice().computeDistance(mistoB.getPozice());
    }

    /**
     * Vrátí místo A
     * @return místo A
     */
    public AMisto getMistoA() {
        return mistoA;
    }

    /**
     * Vrátí místo B
     * @return místo B
     */
    public AMisto getMistoB() {
        return mistoB;
    }

    /**
     * Vrátí vypočítanou vzdálenost cesty
     * @return ohodnocení cesty
     */
    public double getVzdalenost(){
        return vzdalenost;
    }

    @Override
    public String toString() {
        return "Cesta{" +
                "ID mistoA=" + mistoA.getID() +
                ", ID mistoB=" + mistoB.getID() +
                ", vzdalenost=" + vzdalenost +
                '}';
    }
}
