import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Instance třídy {@code VelbloudSimulace} představuje konkrétního velblouda vykonávajícího požadavky
 * @author Mikuláš Mach, Štěpán Faragula
 * @version 1.20 06-11-2022
 */
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
    private final double casManipulaceKose;
    private Queue<VelbloudPozadavek> frontaPozadavku;
    private List<Cesta> cestaPoCastech;

    private VelbloudAkce vykonavanaAkce;
    private double casNaAkci;
    private int aktualniUsek = 0;

    private double zacatekNakladani;
    private double zacatekVykladani;
    private double casOdchodu = 0;
    private double konecVykladani;
    private int vylozenoKosu;

    private static int pocetVelbloudu;
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
        this.cestaPoCastech = new ArrayList<>();

        zvysID();
        this.ID = pocetVelbloudu;
    }

    /**
     * Dá napít velbloudovi
     */
    private void napiSe(){
        setEnergie(maxVzdalenost);
        casNaAkci += dobaPiti;
    }

    /**
     * Funkce volana simulaci,
     * chova se jako Konecny automat, vstupni bod je NAKLADANI,
     * podle toho kde se nachazi a kolik ma nakladu vykonava dalsi akce
     */
    public void vykonejDalsiAkci(){

        switch (vykonavanaAkce){

            /*  -------------------- */
            /*  Nakladani Velblouda  */
            /*  -------------------- */
            case NAKLADANI:

                //Pokud je velbloud dostatecne/maximalne nalozen, tak vypise posledni vypis o nakladani a prejde do stavu cesta
                if(pocetKosu == frontaPozadavku.peek().getPozadavek().getPozadavekKosu() || pocetKosu == maxPocetKosu){
                    vypisNakladani();
                    vykonavanaAkce = VelbloudAkce.CESTA;
                }

                else{

                    //Kdyz velbloud neni plne nalozen, tak nalozi dalsi kos
                    if((pocetKosu < frontaPozadavku.peek().getPozadavek().getPozadavekKosu() || pocetKosu < maxPocetKosu) && domovskySklad.getPocetKosu() > 0) {

                        if (pocetKosu == 0) {

                            cestaPoCastech.addAll(frontaPozadavku.peek().getCestaPoCastech());

                            zacatekNakladani = casNaAkci;
                            casOdchodu = zacatekNakladani + domovskySklad.getCasNalozeni() * (Math.min(maxPocetKosu, frontaPozadavku.peek().getPozadavek().getPozadavekKosu()));
                        }

                        vypisNakladani();

                        pocetKosu++;
                        domovskySklad.odeberKos();
                        casNaAkci += domovskySklad.getCasNalozeni();
                    }

                    //Ceka na naplneni skladu
                    else{
                        //TODO
                        casNaAkci++;
                    }
                }

                break;

            /*  -------------  */
            /*  Cesta do cile  */
            /*  -------------  */
            case CESTA:

                //Kdyz nema energii na dalsi usek trasy, tak se napije
                if(energie < cestaPoCastech.get(aktualniUsek).getVzdalenost() ){
                    vypisPiti(pozice);
                    napiSe();
                }

                //Pokud muze, tak se premisti do dalsi zastavky
                else {
                    energie -= cestaPoCastech.get(aktualniUsek).getVzdalenost();
                    pozice = cestaPoCastech.get(aktualniUsek).getKonec();
                    casNaAkci += cestaPoCastech.get(aktualniUsek).getVzdalenost() / rychlost;

                    //Kdyz pozice na kterou dosel neni zacatek ani konec pozadavku, tak vypise pruchodovou hlasku
                    if(pozice != domovskySklad && pozice != frontaPozadavku.peek().getPozadavek().getOaza()){
                        vypisPruchodu();
                    }

                    aktualniUsek++;

                    //Kdyz pri urazeni aktualniho useku bude v cili, tak prejde do stavu vykladani
                    if(aktualniUsek == cestaPoCastech.size()){
                        vykonavanaAkce = VelbloudAkce.VYKLADANI;

                        aktualniUsek--;
                    }
                }

                break;

            /*  ---------  */
            /*  Vykladani  */
            /*  ---------  */

            case VYKLADANI:

                //Kdyz se zacne nakladani, ulozi si cas zacatku a vypocita konec
                if(vylozenoKosu == 0){
                    zacatekVykladani = casNaAkci;
                    konecVykladani = zacatekVykladani + domovskySklad.getCasNalozeni() * (Math.min(maxPocetKosu, frontaPozadavku.peek().getPozadavek().getPozadavekKosu()));
                }

                //Zkontroluje zda je pozadavek splnen
                if(frontaPozadavku.peek().zkontrolujSplnenyPozadavek()){
                    vypisVykladani();
                    frontaPozadavku.remove();
                    vylozenoKosu = 0;

                    //Kdyz nema uz kose na obslouzeni dalsiho pozadavku, tak se vrati
                    if(pocetKosu == 0){
                        vykonavanaAkce = VelbloudAkce.CESTAZPATKY;
                        vylozenoKosu = 0;
                    }

                }

                //Pokud pozadavek jeste neni splnen, proved
                else {

                    //Pokud pocet kosu je 0, tak se vrat zpatky pro dalsi kose
                    //TODO TADYBUDE PROBLEM BLBECKU
                    //TODO Pri obsluze jednoho pozadavku vice velbloudy to nebude fungovat
                    if(pocetKosu == 0){
                        vykonavanaAkce = VelbloudAkce.CESTAZPATKY;
                        vylozenoKosu = 0;
                    }
                    //TODO TADYBUDE PROBLEM BLBECKU

                    //Pokud ma velblouc co vykladat, tak to vylozi (vyklada po jednom kosi a udela vypis)
                    else {
                        vypisVykladani();

                        casNaAkci += domovskySklad.getCasNalozeni();
                        frontaPozadavku.peek().doruceneKose(1);
                        pocetKosu--;
                        vylozenoKosu++;
                    }

                }

                break;

            /*  ---------------  */
            /*  Cesta do zpatky  */
            /*  ---------------  */

            //Vicemene stejne jako CESTA
            case CESTAZPATKY:

                //Pokud velbloud obslouzil zadane pozadavky, uvolni ho
                if(aktualniUsek == -1){
                    vypisNavratu();

                    cestaPoCastech.clear();

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

                else {

                    if(energie < cestaPoCastech.get(aktualniUsek).getVzdalenost()){

                        vypisPiti(pozice);
                        napiSe();

                    }
                    else {
                        energie -= cestaPoCastech.get(aktualniUsek).getVzdalenost();
                        pozice = cestaPoCastech.get(aktualniUsek).getZacatek();
                        casNaAkci += cestaPoCastech.get(aktualniUsek).getVzdalenost() / rychlost;

                        if(pozice != domovskySklad){
                            vypisPruchodu();
                        }

                        aktualniUsek--;
                    }
                }
               break;
        }
    }

    /**
     * Přidá do fronty další požadavek co má velbloud obsloužit
     * @param vp definice požadavku
     * @param simulacniCas kdy požadavek přišel
     */
    public void priradPozadavek(VelbloudPozadavek vp , double simulacniCas){
        this.frontaPozadavku.add(vp);
        if(vykonavanaAkce == VelbloudAkce.VOLNY){
            casNaAkci = simulacniCas;
            vykonavanaAkce = VelbloudAkce.NAKLADANI;
        }
    }

    /**
     * Vypočítá jak dlouho bude velbloudovi trvat obsloužit požadavek bez návratu do skladu
     * @param celkovaVzdalenostCesty délka cesty
     * @param pocetKosuObsluha kolik košů má vyložit
     * @return čas kdy bude obsloužen
     */
    public double jakDlouhoBudeTrvatCestaTam(double celkovaVzdalenostCesty, int pocetKosuObsluha){
        int pocetCestSemTam = (int)Math.ceil(pocetKosuObsluha/(maxPocetKosu + 0.0));

        double casStravenyCestou = ((celkovaVzdalenostCesty * pocetCestSemTam * 2) - celkovaVzdalenostCesty) / rychlost;
        double casStravenyManipulaci = pocetKosuObsluha * casManipulaceKose * 2;
        double casStravenyPitim = Math.ceil(celkovaVzdalenostCesty/(maxVzdalenost + 0.0)) * dobaPiti;

        return casStravenyCestou + casStravenyManipulaci + casStravenyPitim;
    }

    /**
     * Vypočítá jak dlouho bude velbloudovi trvat obsloužit požadavek i s návratem do skladu
     * @param celkovaVzdalenostCesty délka cesty
     * @param pocetKosuObsluha kolik košů má vyložit
     * @return čas kdy bude obsloužen
     */
    public double jakDlouhoBudeTrvatCestaTamZpet(double celkovaVzdalenostCesty, int pocetKosuObsluha){
        int pocetCestSemTam = (int)Math.ceil(pocetKosuObsluha/(maxPocetKosu + 0.0));

        double casStravenyCestou = (celkovaVzdalenostCesty * pocetCestSemTam * 2) / rychlost;
        double casStravenyManipulaci = pocetKosuObsluha * casManipulaceKose * 2;
        double casStravenyPitim = Math.ceil(celkovaVzdalenostCesty/(maxVzdalenost + 0.0)) * dobaPiti;

        return casStravenyCestou + casStravenyManipulaci + casStravenyPitim;
    }


    /**
     * Vypočítá jak dlouho bude trvat splnit všechn požadavky, když se má velbloud vrátit do skladu
     * @return čas dokončení fronty i s návratem do skladu
     */
    public double kdySeSplniFronta(){
        double casVsechPozadavku = 0;
        for(VelbloudPozadavek vp : frontaPozadavku){
            casVsechPozadavku += jakDlouhoBudeTrvatCestaTamZpet(vp.getCelkovaVzdalenostCesty(), vp.getPocetPotrebnychKosu());
        }
        return casVsechPozadavku;
    }

    public void setPozice(AMisto pozice){
        this.pozice = pozice;
    }

    public void setEnergie(double energie){
        this.energie = energie;
    }

    public VelbloudAkce getVykonavanaAkce() {
        return vykonavanaAkce;
    }

    public double getCasNaAkci() {
        return casNaAkci;
    }

    private void vypisNakladani(){
        int zaokrouhlenyCas = (int)Math.round(casNaAkci);
        int zaokrouhlenyOdchod = (int)Math.round(casOdchodu);
        System.out.println("Velbloud naklada \t Cas: " + zaokrouhlenyCas +", Velbloud: " + ID + ", Sklad: " + domovskySklad.getID() + ", Nalozeno kosu: " + pocetKosu +", Odchod v: " + zaokrouhlenyOdchod );

    }

    private void vypisVykladani(){
        int zaokrouhlenyCas = (int)Math.round(casNaAkci);
        int zaokrouhlenyVykladani = (int)Math.round(konecVykladani);
        int zaokrouhlenaRezerva = (int)Math.round(frontaPozadavku.peek().getPozadavek().getDeadline() - casNaAkci);
        System.out.println("Velbloud vyklada \t Cas: " + zaokrouhlenyCas + ", Velbloud: "+ ID + ", Oaza: "+ pozice.getID() + ", Vylozeno kosu: "+ vylozenoKosu + ", Vylozeno v: " + zaokrouhlenyVykladani + ", Casova rezerva: " + zaokrouhlenaRezerva);
    }

    private void vypisPiti(AMisto pozice){
        int zaokrouhlenyCas = (int)Math.round(casNaAkci);
        int zaokrouhlenyOdchod = (int)Math.round(casNaAkci + dobaPiti);
        if(pozice instanceof Oaza){
            System.out.println("Velbloud pije \t Cas: " + zaokrouhlenyCas + ", Velbloud: " + ID + ", Oaza: " + pozice.getID() + ", Ziznivy " + typ.getNazev() + ", Pokracovani mozne v: " + zaokrouhlenyOdchod);
        }
        else {
            System.out.println("Velbloud pije \t Cas: " + zaokrouhlenyCas + ", Velbloud: " + ID + ", Sklad: " + pozice.getID() + ", Ziznivy " + typ.getNazev() + ", Pokracovani mozne v: " + zaokrouhlenyOdchod);
        }

    }

    private void vypisNavratu(){
        int zaokrouhlenyCas = (int)Math.round(casNaAkci);
        System.out.println("Velbloud se vratil \t Cas: " + zaokrouhlenyCas + ", Velbloud: " + ID + ", Navrat do skladu: " + domovskySklad.getID());
    }

    private void vypisPruchodu(){
        int zaokrouhlenyCas = (int)Math.round(casNaAkci);
        System.out.println("Kuk na velblouda \t Cas: " + zaokrouhlenyCas + ", Velbloud: " + ID + ", Oaza: " + pozice.getID() + ", Kuk na velblouda");
    }

    public void setCasNaAkci(double casNaAkci) {
        this.casNaAkci = casNaAkci;
    }

    @Override
    public String toString() {
        return "VelbloudSimulace{" +
                "ID=" + ID +
                ", casAkce=" + casNaAkci +
                ", pozice=" + pozice.getID() +
                ", rychlost=" + rychlost +
                ", maxVzdalenost=" + maxVzdalenost +
                ", dobaPiti=" + dobaPiti +
                ", maxPocetKosu=" + maxPocetKosu +
                ", energie=" + energie +
                ", pocetKosu=" + pocetKosu +
                '}';
    }

    //////////////////////
    //* Private metody *//
    //////////////////////

    /**
     * Navýší počítadlo instancí
     */
    private void zvysID(){
        pocetVelbloudu++;
    }
}
