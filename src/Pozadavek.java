/**
 * Instance třídy {@code VelbloudTyp} představuje příchozí požadavek
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.27 20-11-2022
 */
public class Pozadavek{
    private final double casPrichodu;
    private final AMisto oaza;
    private final int pozadavekKosu;
    private final double deadline;
    private boolean jeSplnen;

    private static int pocetPozadavku;
    private final int ID;

    /**
     * Konstruktor
     * Defaultně není požadavek splněn
     * @param casPrichodu čas příchodu v simulačním čase
     * @param oaza jakou oázu je potřeba zásobovat
     * @param pozadavekKosu kolik oáza potřebuje kosů
     * @param deadline do kdy mají koše přijít
     */
    public Pozadavek(double casPrichodu, AMisto oaza, int pozadavekKosu, double deadline){
        this.casPrichodu = casPrichodu;
        this.oaza = oaza;
        this.pozadavekKosu = pozadavekKosu;
        this.deadline = deadline;

        jeSplnen = false;

        zvysID();
        this.ID = pocetPozadavku;
    }

    /**
     * Označí požadavek jako splněný
     */
    public void setSplnen() {
        jeSplnen = true;
        Simulace.oznacSplnenyPozadavek(this);
    }

    /**
     * Vrátí jestli se požadavek splnil
     * @return hodnota jeSplnen
     */
    public boolean jeSplnen() {
        return jeSplnen;
    }

    /**
     * Vrátí čas příchodu v simulačním čase
     * @return čas příchodu v simulačním čase
     */
    public double getCasPrichodu() {
        return casPrichodu;
    }

    /**
     * Vrátí oázu kterou je potřeba zásobovat
     * @return oáza kterou je potřeba zásobovat
     */
    public AMisto getOaza() {
        return oaza;
    }

    /**
     * Vrátí počet požadovaných košů
     * @return počet požadovaných košů
     */
    public int getPozadavekKosu() {
        return pozadavekKosu;
    }

    /**
     * Vrátí deadline v simulačním čase
     * @return deadline v simulačním čase
     */
    public double getDeadline() {
        return deadline;
    }

    /**
     * Vrátí ID požadavku
     * @return ID požadavku
     */
    public int getID() {
        return ID;
    }

    //////////////////////
    //* Private metody *//
    //////////////////////

    /**
     * Navýší počítadlo instancí
     */
    private void zvysID(){
        pocetPozadavku++;
    }

    @Override
    public String toString(){
        return "ID=" + ID + ", inc=" + casPrichodu + ", ded=" + deadline;
    }
}
