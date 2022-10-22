/**
 * Instance třídy {@code Cesta} představuje cestu mezi dvěmi libovolnými body
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.12 21-10-2022
 */
public class Cesta {
    private final AMisto mistoA;
    private final AMisto mistoB;
    private final double vzdalenost;

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

    /**
     * Porovnává dva objekty jestli jsou stejné
     * pokud se jedná o cestu, porovnává jestli jsou zaměnitelné díky symetrii
     * cesta 1 -> 2 je zaměnitelná s cestou 2 -> 1
     * @param obj objekt k porovnání
     * @return true pokud jsou cesty zaměnitelné
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Cesta)) return false;
        if (obj == this) return true;

        if(this.mistoA == ((Cesta) obj).getMistoA() && this.mistoB == ((Cesta) obj).getMistoB()){
            return true;
        }
        if(this.mistoA == ((Cesta) obj).getMistoB() && this.mistoB == ((Cesta) obj).getMistoA()){
            return true;
        }

        return false;
    }

    /**
     * Vypočítá hashCode jako součet dvou míst
     * @return součet ID obou míst
     */
    @Override
    public int hashCode() {
        return mistoA.getID() + mistoB.getID();
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
