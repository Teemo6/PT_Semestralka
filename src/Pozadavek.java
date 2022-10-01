public class Pozadavek {
    private int casPozadavku;
    private int indexOazy;
    private int pozadavekKosu;
    private int casDoruceni;

    public Pozadavek(int casPozadavku, int indexOazy, int pozadavekKosu, int casDoruceni){
        this.casPozadavku = casPozadavku;
        this.indexOazy = indexOazy;
        this.pozadavekKosu = pozadavekKosu;
        this.casDoruceni = casDoruceni;
    }

    public int getCasPozadavku() {
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
}
