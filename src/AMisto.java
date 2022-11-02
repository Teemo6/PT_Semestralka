/**
 * Instance třídy {@code AMisto} představuje abstraktní třídu libovolného místa v mapě
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.19 02-11-2022
 */
public abstract class AMisto {
    protected DoubleVector2D pozice;
    protected int pocetKosu = 0;

    protected static int pocet;
    protected int ID;

    /**
     * Při vytvoření místa začíná ID od 1 podle zadání
     */
    public AMisto(){
        pocet++;
        this.ID = pocet;
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

    public int getPocetKosu(){
        return pocetKosu;
    }

    @Override
    public String toString() {
        return "AMisto{" +
                "pozice=" + pozice +
                ", ID=" + ID +
                '}';
    }
}
