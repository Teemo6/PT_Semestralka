public class Pozadavek {
    private double casPrichodu;
    private int indexOazy;
    private int pozadavekKosu;
    private int deadline;

    public Pozadavek(double casPrichodu, int indexOazy, int pozadavekKosu, int deadline){
        this.casPrichodu = casPrichodu;
        this.indexOazy = indexOazy;
        this.pozadavekKosu = pozadavekKosu;
        this.deadline = deadline;
    }

    public double getCasPrichodu() {
        return casPrichodu;
    }

    public int getIndexOazy() {
        return indexOazy;
    }

    public int getPozadavekKosu() {
        return pozadavekKosu;
    }

    public int getDeadline() {
        return deadline;
    }

    @Override
    public String toString() {
        return "Pozadavek{" +
                "casPozadavku=" + casPrichodu +
                ", indexOazy=" + indexOazy +
                ", pozadavekKosu=" + pozadavekKosu +
                ", casDoruceni=" + deadline +
                '}';
    }
}
