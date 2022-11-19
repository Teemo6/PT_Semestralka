import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Instance třídy {@code VelbloudSimulace} představuje konkrétního velblouda vykonávajícího požadavky
 * @author Mikuláš Mach, Štěpán Faragula
 * @version 1.23 15-11-2022
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
    /** Určuje kolik aktuálně velbloud nese košů*/
    private int pocetKosu;
    /** Určuje jak dlouho trvá naložit/vyložit koš*/
    private final double casManipulaceKose;
    /** Fronta všech požadavků co má velbloud obloužit*/
    private final Queue<VelbloudPozadavek> frontaPozadavku;
    /** ArrayList všech jednotlivých úseků, které vedou ze skladu do cílové oázy*/
    private final List<Cesta> cestaPoCastech;

    private VelbloudAkce vykonavanaAkce;
    /** Simulační čas kdy má být velbloud znovu obsloužen*/
    private double casNaAkci;
    private int aktualniUsek = 0;

    /** Simulační čas začátku nakládání*/
    private double zacatekNakladani;
    /** Simulační čas začátku vykládání*/
    private double zacatekVykladani;
    /** Vypočtený simulační čas chvíle, kdy velbloud dokončí nakládání a vyrazí na cestu*/
    private double casOdchodu = 0;
    /** Vypočtený simulační čas chvíle, kdy velbloud vyloží všechny koše, které má doručit na dané místo*/
    private double konecVykladani;
    /** Počet košů, které velbloud vyložil během jedné cesty na daném místě*/
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
     * Funkce volana simulaci,
     * chova se jako Konecny automat, vstupni bod je NAKLADANI,
     * podle toho kde se nachazi a kolik ma nakladu vykonava dalsi akce
     */
    public void vykonejDalsiAkci(){

        switch (vykonavanaAkce){

            case NAKLADANI:

                obsluhaNakladani();
                break;

            case CESTA:

                obsluhaCesty();
                break;

            case VYKLADANI:

                obsluhaVykladani();
                break;

            //Vicemene stejne jako CESTA
            case CESTAZPATKY:

                obsluhaCestyZpet();
               break;

            case VOLNY:
                //casNaAkci = Double.MAX_VALUE;
                casNaAkci = domovskySklad.getCasDalsiAkce();
                break;

            //Nemelo by nikdy nastat
            default:

                System.out.println("CHYBA");
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
     * @param cesta délka cesty
     * @param pocetKosuObsluha kolik košů má vyložit
     * @return čas kdy bude obsloužen
     */
    public double kdySplniPozadavek(CestaCasti cesta, int pocetKosuObsluha){
        double celkovaVzdalenostCesty = cesta.getVzdalenost();
        int pocetOpakovaniCesty = (int)Math.ceil(pocetKosuObsluha/(maxPocetKosu + 0.0));

        double simEnergie = energie;
        double casPitiTam = 0;
        double casPitiZpet= 0;
        for(Cesta c : cesta.getSeznamCest()){
            if(simEnergie - c.getVzdalenost() <= 0){
                casPitiTam += dobaPiti;
                simEnergie = maxVzdalenost;
            } else {
                simEnergie -= c.getVzdalenost();
            }
        }
        for(Cesta c : cesta.prohodSmer().getSeznamCest()){
            if(simEnergie - c.getVzdalenost() <= 0){
                casPitiZpet += dobaPiti;
                simEnergie = maxVzdalenost;
            } else {
                simEnergie -= c.getVzdalenost();
            }
        }

        casPitiTam *= pocetOpakovaniCesty;
        casPitiZpet *= (pocetOpakovaniCesty - 1);

        double casStravenyPitim = casPitiTam + casPitiZpet;

        double casStravenyCestou = ((celkovaVzdalenostCesty * pocetOpakovaniCesty * 2) - celkovaVzdalenostCesty) / rychlost;
        double casStravenyManipulaci = pocetKosuObsluha * casManipulaceKose * 2;

        return casStravenyCestou + casStravenyManipulaci + casStravenyPitim;
    }

    /**
     * Vypočítá jak dlouho bude velbloudovi trvat obsloužit požadavek i s návratem do skladu
     * @param cesta délka cesty
     * @param pocetKosuObsluha kolik košů má vyložit
     * @return čas kdy bude obsloužen
     */
    public double jakDlouhoBudeTrvatCestaTamZpet(CestaCasti cesta, int pocetKosuObsluha){
        double celkovaVzdalenostCesty = cesta.getVzdalenost();
        int pocetOpakovani = (int)Math.ceil(pocetKosuObsluha/(maxPocetKosu + 0.0));

        double simEnergie = energie;
        double casStravenyPitim = 0;
        for(Cesta c : cesta.getSeznamCest()){
            if(simEnergie - c.getVzdalenost() <= 0){
                casStravenyPitim += dobaPiti;
                simEnergie = maxVzdalenost;
            } else {
                simEnergie -= c.getVzdalenost();
            }
        }
        for(Cesta c : cesta.prohodSmer().getSeznamCest()){
            if(simEnergie - c.getVzdalenost() <= 0){
                casStravenyPitim += dobaPiti;
                simEnergie = maxVzdalenost;
            } else {
                simEnergie -= c.getVzdalenost();
            }
        }

        double casStravenyCestou = (celkovaVzdalenostCesty * pocetOpakovani * 2) / rychlost;
        double casStravenyManipulaci = pocetKosuObsluha * casManipulaceKose * 2;

        casStravenyPitim *= pocetOpakovani;

        return casStravenyCestou + casStravenyManipulaci + casStravenyPitim;
    }


    /**
     * Vypočítá jak dlouho bude trvat splnit všechn požadavky, když se má velbloud vrátit do skladu
     * @return čas dokončení fronty i s návratem do skladu
     */
    public double kdySeSplniFronta(){
        double casVsechPozadavku = 0;
        for(VelbloudPozadavek vp : frontaPozadavku){
            casVsechPozadavku += jakDlouhoBudeTrvatCestaTamZpet(vp.getCestaCasti(), vp.getPocetPotrebnychKosu());
        }
        // zezacatku max energie
        casVsechPozadavku -= dobaPiti;
        return casVsechPozadavku;
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

    public double getDobaPiti() {
        return dobaPiti;
    }

    public Queue<VelbloudPozadavek> getFrontaPozadavku() {
        return frontaPozadavku;
    }

    public void setCasNaAkci(double casNaAkci) {
        this.casNaAkci = casNaAkci;
    }

    public double getMaxVzdalenost() {
        return maxVzdalenost;
    }

    @Override
    public String toString() {
        return "ID=" + ID + ", typ=" + typ.getNazev();
    }

    //////////////////////
    //* Private metody *//
    //////////////////////

    /**
     * Kontroluje zda má být velbloud naložen, pokud ano a jsou ve skladu koše, tak ho naloží,
     * pokud nejsou koše, tak na ně počká a pokud už je naložen tak ho přepne do stavu CESTA
     */
    private void obsluhaNakladani(){

        //Pokud je velbloud dostatecne/maximalne nalozen, tak vypise posledni vypis o nakladani a prejde do stavu cesta
        if(pocetKosu == frontaPozadavku.peek().getPozadavek().getPozadavekKosu() || pocetKosu == maxPocetKosu){
            vypisNakladani();
            vykonavanaAkce = VelbloudAkce.CESTA;
        }

        else{

            //Kdyz velbloud neni plne nalozen, tak nalozi dalsi kos
            if((pocetKosu < frontaPozadavku.peek().getPozadavek().getPozadavekKosu() || pocetKosu < maxPocetKosu) && domovskySklad.getPocetKosu() > 0) {

                if (pocetKosu == 0) {

                    cestaPoCastech.addAll(frontaPozadavku.peek().getCestaCasti().getSeznamCest());

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
                casNaAkci = domovskySklad.getCasDalsiAkce();
            }
        }

    }

    /**
     * Kontroluje, zda velbloud může ujít následující úsek cesty, pokud ne tak nechá velblouda napít,
     * pokud ano, tak velblouda přemístí na další pozici. Pokud další pozice není cíl, tak vypíše průchod
     * a pokud další pozice je cíl, tak velbloud přejde do stavu vykládání.
     */
    private void obsluhaCesty(){

        //Kdyz nema energii na dalsi usek trasy, tak se napije
        if(energie < cestaPoCastech.get(aktualniUsek).getVzdalenost() ){
            vypisPiti(pozice);
            napiSe();
        }

        //Pokud muze, tak se premisti do dalsi zastavky
        else {

            //Kdyz pozice na kterou dosel neni zacatek ani konec pozadavku, tak vypise pruchodovou hlasku
            if(pozice != domovskySklad && pozice != frontaPozadavku.peek().getPozadavek().getOaza()){
                vypisPruchodu();
            }

            energie -= cestaPoCastech.get(aktualniUsek).getVzdalenost();
            pozice = cestaPoCastech.get(aktualniUsek).getKonec();
            casNaAkci += cestaPoCastech.get(aktualniUsek).getVzdalenost() / rychlost;

            aktualniUsek++;

            //Kdyz pri urazeni aktualniho useku bude v cili, tak prejde do stavu vykladani
            if(aktualniUsek == cestaPoCastech.size()){
                vykonavanaAkce = VelbloudAkce.VYKLADANI;

                aktualniUsek--;
            }
        }

    }

    private void obsluhaCestyZpet(){

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

                if(pozice != domovskySklad){
                    vypisPruchodu();
                }

                energie -= cestaPoCastech.get(aktualniUsek).getVzdalenost();
                pozice = cestaPoCastech.get(aktualniUsek).getZacatek();
                casNaAkci += cestaPoCastech.get(aktualniUsek).getVzdalenost() / rychlost;

                aktualniUsek--;
            }
        }

    }

    private void obsluhaVykladani(){

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

    }


    /**
     * Navýší počítadlo instancí
     */
    private void zvysID(){
        pocetVelbloudu++;
    }

    /**
     * Dá napít velbloudovi
     */
    private void napiSe(){
        setEnergie(maxVzdalenost);
        casNaAkci += dobaPiti;
    }

    /**
     * Vypíše do konzole stav nakládání
     */
    private void vypisNakladani(){
        int zaokrouhlenyCas = (int)Math.round(casNaAkci);
        int zaokrouhlenyOdchod = (int)Math.round(casOdchodu);
        System.out.println("Velbloud naklada \t Cas: " + zaokrouhlenyCas +", Velbloud: " + ID + ", Sklad: " + domovskySklad.getID() + ", Nalozeno kosu: " + pocetKosu +", Odchod v: " + zaokrouhlenyOdchod );

    }

    /**
     * Vypíše do konzole stav vykládání
     */
    private void vypisVykladani(){
        int zaokrouhlenyCas = (int)Math.round(casNaAkci);
        int zaokrouhlenyVykladani = (int)Math.round(konecVykladani);
        int zaokrouhlenaRezerva = (int)Math.round(frontaPozadavku.peek().getPozadavek().getDeadline() - casNaAkci);
        System.out.println("Velbloud vyklada \t Cas: " + zaokrouhlenyCas + ", Velbloud: "+ ID + ", Oaza: "+ pozice.getID() + ", Vylozeno kosu: "+ vylozenoKosu + ", Vylozeno v: " + zaokrouhlenyVykladani + ", Casova rezerva: " + zaokrouhlenaRezerva);
    }

    /**
     * Vypíše do konzole průbeh pití
     */
    private void vypisPiti(AMisto pozice){
        int zaokrouhlenyCas = (int)Math.round(casNaAkci);
        int zaokrouhlenyOdchod = (int)Math.round(casNaAkci + dobaPiti);
        if(pozice instanceof Oaza){
            System.out.println("Velbloud pije \t\t Cas: " + zaokrouhlenyCas + ", Velbloud: " + ID + ", Oaza: " + pozice.getID() + ", Ziznivy " + typ.getNazev() + ", Pokracovani mozne v: " + zaokrouhlenyOdchod);
        }
        else {
            System.out.println("Velbloud pije \t\t Cas: " + zaokrouhlenyCas + ", Velbloud: " + ID + ", Sklad: " + pozice.getID() + ", Ziznivy " + typ.getNazev() + ", Pokracovani mozne v: " + zaokrouhlenyOdchod);
        }
    }

    /**
     * Vypíše do konzole návrat velblouda
     */
    private void vypisNavratu(){
        int zaokrouhlenyCas = (int)Math.round(casNaAkci);
        System.out.println("Velbloud se vratil \t Cas: " + zaokrouhlenyCas + ", Velbloud: " + ID + ", Navrat do skladu: " + domovskySklad.getID());
    }

    /**
     * Vypíše do konzole, že velbloud prochází oázou
     */
    private void vypisPruchodu(){
        int zaokrouhlenyCas = (int)Math.round(casNaAkci);
        System.out.println("Kuk na velblouda \t Cas: " + zaokrouhlenyCas + ", Velbloud: " + ID + ", Oaza: " + pozice.getID() + ", Kuk na velblouda");
    }
}
