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
