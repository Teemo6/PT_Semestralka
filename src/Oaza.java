/**
 * Instance třídy {@code Oaza} představuje oázu která vysílá požadavky
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.19 02-11-2022
 */
public class Oaza extends AMisto {
    protected static int pocetOaza;
    protected int IDOaza;

    public Oaza(DoubleVector2D pozice){
        this.pozice = pozice;

        pocetOaza++;
        this.IDOaza = pocetOaza;
    }

    public int getIDOaza() {
        return IDOaza;
    }

    @Override
    public String toString() {
        return "Oaza{" +
                "ID=" + ID +
                ", pozice=" + pozice +
                '}';
    }
}
