import java.util.LinkedList;
import java.util.Queue;

public class VelbloudSimulace{
    private AMisto pozice;
    private final Sklad domovskySklad;
    private final double rychlost;
    private final double maxVzdalenost;
    private final double dobaPiti;
    private final int maxPocetKosu;
    private final VelbloudTyp typ;

    private double energie;
    private int pocetKosu;
    private double casManipulaceKose;
    private Queue<VelbloudPozadavek> frontaPozadavku;

    private VelbloudAkce vykonavanaAkce;
    private double casNaAkci;
    private int aktualniUsek = 0;

    private double zacatekNakladani;
    private double zacatekVykladani;
    private double casOdchodu = 0;
    private double konecVykladani;
    private int vylozenoKosu;
    private static int pocet;
    private final int ID;

    public VelbloudSimulace(Sklad pozice, double rychlost, double maxVzdalenost, VelbloudTyp typ){
        this.pozice = pozice;
        this.domovskySklad = pozice;
        this.rychlost = rychlost;
        this.maxVzdalenost = maxVzdalenost;
        this.typ = typ;
        this.maxPocetKosu = typ.getMaxKose();

        this.energie = maxVzdalenost;
        this.pocetKosu = 0;

        this.vykonavanaAkce = VelbloudAkce.VOLNY;
        this.casManipulaceKose = pozice.getCasNalozeni();
        this.dobaPiti = typ.getCasNapit();

        this.frontaPozadavku = new LinkedList<>();

        pocet++;
        this.ID = pocet;
    }

    /**
     * Nalož koše
     * @param pozadavek kolik se má naložit na velblouda
     */
    public void nalozKose(int pozadavek){
        if(pozice instanceof Sklad){
            vykonavanaAkce = VelbloudAkce.NAKLADANI;

            int kolikNalozis = Math.min(maxPocetKosu, pozadavek);   // Naloží na velblouda tolik kolik unese, nebo jaký je požadavek
            casManipulaceKose = ((Sklad) pozice).getCasNalozeni();

            if(pozice.uberKose(kolikNalozis)){
                pocetKosu = kolikNalozis;
                // Část s časem


            } else {
                System.out.println("Ve skladu "+ pozice.getID() + " není požadovaný počet košů");
            }
        } else {
            System.out.println("Velbloud "+ ID + " nemůže naložit koše, není ve skladu");
        }
    }

    /**
     * Dá napít velbloudovi
     */
    public void napiSe(){
        setEnergie(maxVzdalenost);
        casNaAkci += dobaPiti;
    }

    public void vykonejDalsiAkci(){
        switch (vykonavanaAkce){



            /*  -------------------- */
            /*  Nakladani Velblouda  */
            /*  -------------------- */
            case NAKLADANI:

                //Kdyz velbloud neni plne nalozen, tak nalozi dalsi kos
                if((pocetKosu < frontaPozadavku.peek().getPozadavek().getPozadavekKosu() || pocetKosu < maxPocetKosu) && domovskySklad.getPocetKosu() > 0){

                    if(pocetKosu == 0){
                        zacatekNakladani = casNaAkci;
                        casOdchodu = zacatekNakladani + domovskySklad.getCasNalozeni() * (Math.min(maxPocetKosu, frontaPozadavku.peek().getPozadavek().getPozadavekKosu()));
                    }

                    vypisNakladani();

                    pocetKosu++;
                    domovskySklad.odeperKos();
                    casNaAkci += domovskySklad.getCasNalozeni();

                    //Kdyz velbloud ceka na nalozeni posledniho kose, tak pak rovnou bude vyslal na cestu
                    if(pocetKosu == frontaPozadavku.peek().getPozadavek().getPozadavekKosu() || pocetKosu == maxPocetKosu){
                        vypisNakladani();
                        vykonavanaAkce = VelbloudAkce.CESTA;
                    }
                }

                //Ceka na doplneni skladu
                else{
                    //TODO
                    casNaAkci++;
                }
                break;

            /*  -------------  */
            /*  Cesta do cile  */
            /*  -------------  */
            case CESTA:

                //Kdyz nema energii na dalsi usek trasy, tak se napije
                if(energie < frontaPozadavku.peek().getCestaPoCastech().get(aktualniUsek).getVzdalenost() ){
                    vypisPiti(pozice);
                    napiSe();

                }

                //Pokud muze, tak se premisti do dalsi zastavky
                else {
                    energie -= frontaPozadavku.peek().getCestaPoCastech().get(aktualniUsek).getVzdalenost();
                    pozice = frontaPozadavku.peek().getCestaPoCastech().get(aktualniUsek).getMistoB();
                    casNaAkci += frontaPozadavku.peek().getCestaPoCastech().get(aktualniUsek).getVzdalenost() / rychlost;

                    vypisPruchodu();

                    aktualniUsek++;

                    //Kdyz pri urazeni aktualniho useku bude v cili, tak zacne vykladat
                    if(aktualniUsek == frontaPozadavku.peek().getCestaPoCastech().size()){
                        vykonavanaAkce = VelbloudAkce.VYKLADANI;

                        aktualniUsek--;
                    }
                }

                break;

            /*  ---------  */
            /*  Vykladani  */
            /*  ---------  */

            case VYKLADANI:

                if(vylozenoKosu == 0){
                    zacatekVykladani = casNaAkci;
                    konecVykladani = zacatekVykladani + domovskySklad.getCasNalozeni() * (Math.min(maxPocetKosu, frontaPozadavku.peek().getPozadavek().getPozadavekKosu()));
                }

                vypisVykladani();

                casNaAkci += domovskySklad.getCasNalozeni();
                frontaPozadavku.peek().doruceneKose(1);
                pocetKosu--;
                vylozenoKosu++;

                if(frontaPozadavku.peek().zkontrolujSplnenyPozadavek()){
                    frontaPozadavku.remove();
                    vypisVykladani();
                    vylozenoKosu = 0;
                }

                //Kdyz nema uz kose na obslouzeni dalsiho pozadavku, tak se vrati
                if(pocetKosu == 0){
                    vykonavanaAkce = VelbloudAkce.CESTAZPATKY;
                    vylozenoKosu = 0;
                }

                //Pokracuje v obsluze dalsich pozadavku
                else if(true){
                    //vykonavanaAkce = VelbloudAkce.CESTAZPATKY;
                    //TODO
                }

                break;

            /*  ---------------  */
            /*  Cesta do zpatky  */
            /*  ---------------  */

            case CESTAZPATKY:

                if(energie < frontaPozadavku.peek().getCestaPoCastech().get(aktualniUsek).getVzdalenost() ){
                    vypisPiti(pozice);
                    napiSe();
                }

                else {
                    energie -= frontaPozadavku.peek().getCestaPoCastech().get(aktualniUsek).getVzdalenost();
                    pozice = frontaPozadavku.peek().getCestaPoCastech().get(aktualniUsek).getMistoA();
                    casNaAkci += frontaPozadavku.peek().getCestaPoCastech().get(aktualniUsek).getVzdalenost() / rychlost;

                    vypisPruchodu();

                    aktualniUsek--;

                    //Pokud velbloud obslouzil zadane pozadavky, uvolni ho
                    if(aktualniUsek == -1){
                        vypisNavratu();
                        aktualniUsek++;
                        if(frontaPozadavku.isEmpty()){
                            casNaAkci = Double.MAX_VALUE;
                            vykonavanaAkce = VelbloudAkce.VOLNY;
                        }
                        else {
                            vykonavanaAkce = VelbloudAkce.NAKLADANI;
                        }
                        //TODO

                    }
                }
               break;
        }
    }

    /**
     * Prida do fronty dalsi pozadavek co ma velbloud obslouzit
     * @param vp pozadavek
     */
    public void priradPozadavek(VelbloudPozadavek vp , double simulacniCas){
        this.frontaPozadavku.add(vp);
        if(vykonavanaAkce == VelbloudAkce.VOLNY){
            casNaAkci = simulacniCas;
            vykonavanaAkce = VelbloudAkce.NAKLADANI;
        }
    }

    public double jakDlouhoBudeTrvatCesta(double celkovaVzdalenostCesty, int pocetKosuObsluha){
        int pocetCestSemTam = (int)Math.ceil(pocetKosuObsluha/(maxPocetKosu + 0.0));

        double casStravenyCestou = ((celkovaVzdalenostCesty * pocetCestSemTam * 2) - celkovaVzdalenostCesty) / rychlost;
        double casStravenyManipulaci = pocetKosuObsluha * casManipulaceKose * 2;
        double casStravenyPitim = Math.ceil(celkovaVzdalenostCesty/(maxVzdalenost + 0.0)) * dobaPiti;

        return casStravenyCestou + casStravenyManipulaci + casStravenyPitim;
    }

    public void setPozice(AMisto pozice){
        this.pozice = pozice;
    }

    public void setEnergie(double energie){
        this.energie = energie;
    }

    public AMisto getPozice() {
        return pozice;
    }

    public double getRychlost() {
        return rychlost;
    }

    public double getMaxVzdalenost() {
        return maxVzdalenost;
    }

    public double getDobaPiti() {
        return dobaPiti;
    }

    public int getMaxPocetKosu() {
        return maxPocetKosu;
    }

    public double getEnergie() {
        return energie;
    }

    public int getPocetKosu() {
        return pocetKosu;
    }

    public static int getPocet() {
        return pocet;
    }

    public int getID() {
        return ID;
    }

    public double getCasManipulaceKose() {
        return casManipulaceKose;
    }

    public VelbloudAkce getVykonavanaAkce() {
        return vykonavanaAkce;
    }

    public double getCasNaAkci() {
        return casNaAkci;
    }

    private void vypisNakladani(){

            System.out.println("Cas: " + casNaAkci +", Velbloud:" + ID + ", Sklad: " + domovskySklad.getID() + ", Nalozeno kosu: " + pocetKosu +", Odchod v: " + casOdchodu );

    }

    private void vypisVykladani(){
        System.out.println("Cas: " + casNaAkci + ", Velbloud: "+ ID + ", Oaza: "+ pozice.getID() + ", Vylozeno kosu: "+ vylozenoKosu + ", Vylozeno v: " + konecVykladani + ", Casova rezerva: " + (konecVykladani - frontaPozadavku.peek().getPozadavek().getDeadline()));
    }

    private void vypisPiti(AMisto pozice){

        if(pozice instanceof Oaza){
            System.out.println("Cas: " + casNaAkci + ", Velbloud: " + ID + ", Oaza: " + pozice.getID() + ", Ziznivy " + typ.getNazev() + ", Pokracovani mozne v: " + (casNaAkci + dobaPiti));
        }
        else {
            System.out.println("Cas: " + casNaAkci + ", Velbloud: " + ID + ", Sklad: " + pozice.getID() + ", Ziznivy " + typ.getNazev() + ", Pokracovani mozne v: " + (casNaAkci + dobaPiti));
        }

    }

    private void vypisNavratu(){
        System.out.println("Cas: " + casNaAkci + ", Velbloud: " + ID + ", Navrat do skladu: " + domovskySklad);
    }

    private void vypisPruchodu(){
        System.out.println("Cas: " + casNaAkci + ", Velbloud: " + ID + ", Oaza: " + pozice + ", Kuk na velblouda");
    }

    public void setCasNaAkci(double casNaAkci) {
        this.casNaAkci = casNaAkci;
    }

    @Override
    public String toString() {
        return "VelbloudSimulace{" +
                "ID=" + ID +
                ", pozice=" + pozice.getID() +
                ", rychlost=" + rychlost +
                ", maxVzdalenost=" + maxVzdalenost +
                ", dobaPiti=" + dobaPiti +
                ", maxPocetKosu=" + maxPocetKosu +
                ", energie=" + energie +
                ", pocetKosu=" + pocetKosu +
                '}';
    }
}
