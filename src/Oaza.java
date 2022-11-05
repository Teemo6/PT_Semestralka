/**
 * Instance třídy {@code Oaza} představuje oázu kterou je potřeba zásobovat koši
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.20 06-11-2022
 */
public class Oaza extends AMisto {
    protected static int pocetOaza;
    protected int IDOaza;

    /**
     * Konstruktor
     * @param pozice pozice X, Y
     */
    public Oaza(DoubleVector2D pozice){
        this.pozice = pozice;

        zvysIDOaza();
        this.IDOaza = pocetOaza;
    }

    /**
     * Vrátí speciální ID oázy
     * @return ID oázy
     */
    public int getIDOaza() {
        return IDOaza;
    }

    /**
     * toString
     * @return string s atributy oázy
     */
    @Override
    public String toString() {
        return "Oaza{" +
                "ID=" + ID +
                ", pozice=" + pozice +
                '}';
    }

    //////////////////////
    //* Private metody *//
    //////////////////////

    /**
     * Navýší počítadlo instancí
     */
    private void zvysIDOaza(){
        pocetOaza++;
    }
}
