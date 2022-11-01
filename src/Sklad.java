public class Sklad extends AMisto{
    private final int pocetPrirustek;
    private final double casDoplneniSkladu;
    private final double casNalozeni;

    private int pocetKosu;
    private double casDalsiAkce;

    public Sklad(DoubleVector2D pozice, int pocetPrirustek, double casDoplneniSkladu, double casNalozeni){
        this.pozice = pozice;
        this.pocetPrirustek = pocetPrirustek;
        this.casDoplneniSkladu = casDoplneniSkladu;
        this.casNalozeni = casNalozeni;

        this.pocetKosu = pocetPrirustek;
        this.casDalsiAkce = casNalozeni;
    }

    public void doplnSklad(){
        pocetKosu += pocetPrirustek;
        casDalsiAkce += casNalozeni;
    }

    public void odeperKos() { pocetKosu--;}

    public int getPocetKosu(){return pocetKosu;}

    public double getCasNalozeni() {
        return casNalozeni;
    }

    public int getPocetPrirustek() {
        return pocetPrirustek;
    }

    public double getCasDoplneniSkladu() {
        return casDoplneniSkladu;
    }

    public double getCasDalsiAkce() {
        return casDalsiAkce;
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