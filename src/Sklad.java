public class Sklad extends AMisto{
    private int pocetKosu;
    private int casPohybKosu;

    public Sklad(IntVector2D pozice, int pocetKosu, int casPohybKosu){
        this.pozice = pozice;
        this.pocetKosu = pocetKosu;
        this.casPohybKosu = casPohybKosu;
    }

    public int getPocetKosu() {
        return pocetKosu;
    }

    public int getCasPohybKosu() {
        return casPohybKosu;
    }
}
