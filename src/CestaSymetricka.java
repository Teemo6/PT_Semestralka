/**
 * Instance třídy {@code CestaSymetricka} představuje oboustrannou cestu mezi dvěmi libovolnými body
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.14 01-11-2022
 */
public class CestaSymetricka extends Cesta{
    public CestaSymetricka(AMisto mistoA, AMisto mistoB){
        super(mistoA, mistoB);
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
}
