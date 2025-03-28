/**
 * Instance třídy {@code Oaza} představuje oázu kterou je potřeba zásobovat koši
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.21 12-11-2022
 */
public class Oaza extends AMisto {
    private static int pocetOaza;
    private final int IDOaza;

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
