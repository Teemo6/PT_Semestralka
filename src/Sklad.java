public class Sklad extends AMisto{
    private int pocetKosu;
    private int casPohybKosu;
    private int casDoplneniTn;

    public Sklad(IntVector2D pozice, int pocetKosu, int casPohybKosu, int casDoplneniTn){
        this.pozice = pozice;
        this.pocetKosu = pocetKosu;
        this.casPohybKosu = casPohybKosu;
        this.casDoplneniTn = casDoplneniTn;
    }

    public int getCasDoplneniTn() {
        return casDoplneniTn;
    }

    public int getPocetKosu() {
        return pocetKosu;
    }

    public int getCasPohybKosu() {
        return casPohybKosu;
    }
}