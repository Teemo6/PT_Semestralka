public class Oaza extends AMisto {

    public Oaza(DoubleVector2D pozice){
        this.pozice = pozice;
    }

    @Override
    public String toString() {
        return "Oaza{" +
                "ID=" + ID +
                ", pozice=" + pozice +
                '}';
    }
}
