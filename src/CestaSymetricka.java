/**
 * Instance třídy {@code CestaSymetricka} představuje oboustrannou cestu mezi dvěmi libovolnými body
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.20 06-11-2022
 */
public class CestaSymetricka extends Cesta{

    /**
     * Konstruktor
     * @param mistoA místo A na mapě propojené s místem B
     * @param mistoB místo B na mapě propojené s místem A
     */
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
        if (obj == null){
            return false;
        }
        if (!(obj instanceof Cesta)){
            return false;
        }
        if (obj == this){
            return true;
        }

        if(this.zacatek == ((Cesta) obj).getZacatek() && this.konec == ((Cesta) obj).getKonec()){
            return true;
        }
        return this.zacatek == ((Cesta) obj).getKonec() && this.konec == ((Cesta) obj).getZacatek();
    }

    /**
     * Vypočítá hashCode jako součet ID dvou míst
     * @return součet ID obou míst
     */
    @Override
    public int hashCode() {
        return zacatek.getID() + konec.getID();
    }
}
