public class Pozadavek {
    private double casPozadavku;
    private int indexOazy;
    private int pozadavekKosu;
    private int casDoruceni;

    public Pozadavek(double casPozadavku, int indexOazy, int pozadavekKosu, int casDoruceni){
        this.casPozadavku = casPozadavku;
        this.indexOazy = indexOazy;
        this.pozadavekKosu = pozadavekKosu;
        this.casDoruceni = casDoruceni;
    }

    public double getCasPozadavku() {
        return casPozadavku;
    }

    public int getIndexOazy() {
        return indexOazy;
    }

    public int getPozadavekKosu() {
        return pozadavekKosu;
    }

    public int getCasDoruceni() {
        return casDoruceni;
    }

    @Override
    public String toString() {
        return "Pozadavek{" +
                "casPozadavku=" + casPozadavku +
                ", indexOazy=" + indexOazy +
                ", pozadavekKosu=" + pozadavekKosu +
                ", casDoruceni=" + casDoruceni +
                '}';
    }
}
