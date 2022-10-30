public class Sklad extends AMisto{
    private int pocetPrirustek;
    private int casDoplneniSkladu;
    private int casNalozeni;

    private int pocetKosu;

    public Sklad(DoubleVector2D pozice, int pocetPrirustek, int casDoplneniSkladu, int casNalozeni){
        this.pozice = pozice;
        this.pocetPrirustek = pocetPrirustek;
        this.casDoplneniSkladu = casDoplneniSkladu;
        this.casNalozeni = casNalozeni;

        this.pocetKosu = pocetPrirustek;
    }

    public void doplnSklad(){
        pocetKosu += pocetPrirustek;
    }

    public int getPocetKosu(){return pocetKosu;}

    public int getCasNalozeni() {
        return casNalozeni;
    }

    public int getPocetPrirustek() {
        return pocetPrirustek;
    }

    public int getCasDoplneniSkladu() {
        return casDoplneniSkladu;
    }

    @Override
    public String toString() {
        return "Sklad{" +
                "ID=" + ID +
                ", pozice=" + pozice +
                ", pocetKosu=" + pocetPrirustek +
                ", casPohybKosu=" + casDoplneniSkladu +
                ", casDoplneniTn=" + casNalozeni +
                '}';
    }
}