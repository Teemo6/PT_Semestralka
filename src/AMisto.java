/**
 * Instance třídy {@code AMisto} představuje libovolné místa na mapě
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.21 12-11-2022
 */
public abstract class AMisto {
    /** souřadnice místa */
    protected DoubleVector2D pozice;
    /** počet uskladněných košů */
    protected int pocetKosu;

    /** počítadlo míst */
    protected static int pocetMist;
    /** ID místa */
    protected final int ID;

    /**
     * Konstruktor
     * Při vytvoření entity začíná ID od 1 podle zadání
     */
    public AMisto(){
        zvysID();
        this.ID = pocetMist;
    }

    /**
     * Vrátí pozici X, Y místa
     * @return pozice X, Y
     */
    public DoubleVector2D getPozice(){
        return this.pozice;
    }

    /**
     * Vrátí ID místa
     * @return ID místa
     */
    public int getID(){
        return ID;
    }

    /**
     * Vrátí počet uskladněných košů
     * @return uskladněné koše
     */
    public int getPocetKosu(){
        return pocetKosu;
    }

    //////////////////////
    //* Private metody *//
    //////////////////////

    /**
     * Navýší počítadlo instancí
     */
    private void zvysID(){
        pocetMist++;
    }

    /**
     * toString
     * @return string s ID místa
     */
    @Override
    public String toString() {
        return "AMisto{" +
                "ID=" + ID +
                '}';
    }
}
