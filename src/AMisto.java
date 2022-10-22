import java.util.ArrayList;
import java.util.List;

/**
 * Instance třídy {@code AMisto} představuje abstraktní třídu libovolného místa v mapě
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.12 21-10-2022
 */
public abstract class AMisto {
    protected DoubleVector2D pozice;
    protected static int pocet;
    protected int ID;

    protected List<VelbloudSimulace> seznamVelbloudu;

    /**
     * Při vytvoření místa začíná ID od 1 podle zadání
     */
    public AMisto(){
        pocet++;
        this.ID = pocet;
        this.seznamVelbloudu = new ArrayList<>();
    }

    /**
     * Přidá velblouda do seznamu
     * @param velbloud velbloud na přidání
     */
    public void pridejVelblouda(VelbloudSimulace velbloud){
        seznamVelbloudu.add(velbloud);
    }

    /**
     * Odebere požadovaného velblouda ze seznamu
     * @param velbloud velbloud na odebrání
     */
    public void odeberVelblouda(VelbloudSimulace velbloud){
        seznamVelbloudu.remove(velbloud);
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

    @Override
    public String toString() {
        return "AMisto{" +
                "pozice=" + pozice +
                ", ID=" + ID +
                ", seznamVelbloudu=" + seznamVelbloudu +
                '}';
    }
}
