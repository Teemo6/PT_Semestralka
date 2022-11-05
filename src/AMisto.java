/**
 * Instance třídy {@code AMisto} představuje libovolné místa na mapě
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.20 06-11-2022
 */
public abstract class AMisto {
    protected DoubleVector2D pozice;
    protected int pocetKosu;

    protected static int pocetMist;
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
}
