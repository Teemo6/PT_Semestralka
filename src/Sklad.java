/**
 * Instance třídy {@code Sklad} představuje sklad která zásobuje okolní oázy
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.20 06-11-2022
 */
public class Sklad extends AMisto{
    private final int pocetPrirustek;
    private final double casDoplneniSkladu;
    private final double casNalozeni;

    private int pocetKosu;
    private int pocetRezervace;
    private double casDalsiAkce;

    /**
     * Konstruktor
     * @param pozice pozice X, Y
     * @param pocetPrirustek kolik přibude košů za jedno naskladnění
     * @param casDoplneniSkladu za jakou dobu má být sklad naskladněn, periodicky
     * @param casManipulaceKose jak dlouho trvá naložit/vyložit koš na/z velblouda
     */
    public Sklad(DoubleVector2D pozice, int pocetPrirustek, double casDoplneniSkladu, double casManipulaceKose){
        this.pozice = pozice;
        this.pocetPrirustek = pocetPrirustek;
        this.casDoplneniSkladu = casDoplneniSkladu;
        this.casNalozeni = casManipulaceKose;
        this.pocetRezervace = 0;

        // Sklad se doplní za čas 0 a doplní se o nenulový počet
        if(casDoplneniSkladu <= 0 && pocetPrirustek > 0){
            this.pocetKosu = Integer.MAX_VALUE;
            this.casDalsiAkce = Double.MAX_VALUE;
        } else {
            this.pocetKosu = pocetPrirustek;
            this.casDalsiAkce = casDoplneniSkladu;
        }
    }

    /**
     * Doplní sklad
     */
    public void doplnSklad(){
        pocetKosu += pocetPrirustek;
        casDalsiAkce += casDoplneniSkladu;
    }

    public double kdyBudeNalozenaRezervace(int rezervace){
        return casDoplneniSkladu * (Math.ceil((rezervace - pocetKosu - pocetRezervace) / (double)pocetPrirustek));
    }

    public double kolikBudeCelkemKosuVCase(double simCas){
        return pocetPrirustek * Math.ceil(simCas / casDoplneniSkladu);
    }


    public void rezervujKose(int pocet){
        pocetRezervace += pocet;
    }

    /**
     * Odebere 1 koš ze skladu
     */
    public void odeberKos() {
        pocetKosu--;
        pocetRezervace--;
    }

    /**
     * Vrátí počet naskladněných košů
     * @return počet naskladněných košů
     */
    public int getPocetKosu(){return pocetKosu;}

    /**
     * Vrátí dobu manipulace s košem
     * @return doba manipulace s košem
     */
    public double getCasNalozeni() {
        return casNalozeni;
    }

    /**
     * Vrátí kdy má být znovu sklad doplněn
     * @return simulační čas naskladnění
     */
    public double getCasDalsiAkce() {
        return casDalsiAkce;
    }
}