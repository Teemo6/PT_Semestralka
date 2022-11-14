import java.util.*;

/**
 * Instance třídy {@code Simulace} představuje jedináčka ve kterém běží celá simulace
 * @author Mikuláš Mach, Štěpán Faragula
 * @version 1.22 14-11-2022
 */
public class Simulace {
    /** Instance jedináčka Simulace */
    private static final Simulace INSTANCE = new Simulace();

    /** Datový model */
    private VstupDat data;

    /** Matice se kterými pracuje simulace */
    MaticeDouble distancniMatice;
    MaticeInteger maticePredchudcu;

    /** Reprezentace grafu */
    GrafMapa mapa;

    /** Práce s časem */
    double simulacniCas = 0;
    boolean simulaceBezi = true;
    PriorityQueue<Pozadavek> casovaFrontaPozadavku;
    PriorityQueue<Sklad> casovaFrontaSkladu;
    PriorityQueue<VelbloudSimulace> casovaFrontaVelbloudu;

    /**
     * Vrátí jedináčka
     * @return instance jedináčka
     */
    public static Simulace getInstance(){
        return INSTANCE;
    }

    /**
     * Spustí simulaci, obsahuje hlavní smyčku s časem
     * @param data Vstupní data
     */
    public void spustSimulaci(VstupDat data){
        this.data = data;

        vytvorPotrebneMatice();

        mapa = vytvorMapu();
        vytvorMapuPredchudcu(data.getMista().get(1));

        casovaFrontaPozadavku = new PriorityQueue<>(5, Comparator.comparingDouble(Pozadavek::getCasPrichodu));
        casovaFrontaSkladu = new PriorityQueue<>(5, Comparator.comparingDouble(Sklad::getCasDalsiAkce));
        casovaFrontaVelbloudu = new PriorityQueue<>(5, Comparator.comparingDouble(VelbloudSimulace::getCasNaAkci));

        casovaFrontaPozadavku.addAll(data.getPozadavky());
        casovaFrontaSkladu.addAll(data.getSklady());

        Pozadavek dalsiPozadavek;
        Sklad dalsiSklad;
        VelbloudSimulace dalsiVel;
        double casPozadavek, casSklad, casVel;

        while(simulaceBezi){
            dalsiPozadavek = casovaFrontaPozadavku.peek();
            dalsiSklad = casovaFrontaSkladu.peek();
            dalsiVel = casovaFrontaVelbloudu.peek();

            casPozadavek = Double.MAX_VALUE;
            casSklad = Double.MAX_VALUE;
            casVel = Double.MAX_VALUE;

            // Zkontroluj jestli existuje další akce
            if(dalsiPozadavek != null){
                casPozadavek = dalsiPozadavek.getCasPrichodu();
            }
            if(dalsiSklad != null){
                casSklad = dalsiSklad.getCasDalsiAkce();
            }
            if(dalsiVel != null){
                casVel = dalsiVel.getCasNaAkci();
            }

            // Zkontroluj jestli už je možné provést akci
            if(casPozadavek <= simulacniCas){
                priradPozadavekVelbloudovi();
                casovaFrontaPozadavku.removeIf(f->false);
                continue;
            }
            if(casSklad <= simulacniCas){
                naplnSklady();
                casovaFrontaSkladu.removeIf(f->false);
                continue;
            }
            if(casVel <= simulacniCas){
                VelbloudSimulace docasny = casovaFrontaVelbloudu.poll();
                assert docasny != null;
                docasny.vykonejDalsiAkci();
                casovaFrontaVelbloudu.add(docasny);
                continue;
            }

            // Zkontroluj jestli nemá jeden požadavek po deadline
            Pozadavek neobslouzeny = neobslouzenyPozadavek();
            if(neobslouzeny != null){
                ukonciSimulaci(neobslouzeny.getOaza());
            }

            // Nastav simulační čas na další nejbližsí událost
            simulacniCas = Math.min(Math.min(casPozadavek, casSklad), casVel);

            // Zkontroluj podmínky úspešné simulace
            if(vsechnyPozadavkyObslouzeny() && vsichniVelbloudiVolni()){
                simulaceBezi = false;
            }
        }
        System.out.println();
        System.out.println("Pocet splnenych pozadavku: " + data.getPozadavky().size());
        System.out.println("Pocet vyuzitych velbloudu: " + casovaFrontaVelbloudu.size());
    }

    /**
     * Vytvoří distanční matici a matici předchůdců
     * Využití Floyd-Warshall algoritmu
     */
    public void vytvorPotrebneMatice(){
        distancniMatice = new MaticeDouble(data.getMista().size());
        distancniMatice.vyplnNekonecnem();

        for (Cesta cesta : data.getCesty()) {
            distancniMatice.setCisloSymetricky(cesta.getZacatek().getID() - 1, cesta.getKonec().getID() - 1, cesta.getVzdalenost());
        }
        int velikostMatice = distancniMatice.getVelikost();

        maticePredchudcu = new MaticeInteger(velikostMatice);
        maticePredchudcu.vyplnNekonecnem();

        // Matice předchůdců
        for (int i = 0; i < velikostMatice; i++) {
            for (int j = 0; j < velikostMatice; j++) {
                if (distancniMatice.getCislo(i, j) != Double.MAX_VALUE){
                    maticePredchudcu.setCislo(i, j, i + 1);
                }
            }
        }

        // Distancni matice
        for(int k = 0; k < velikostMatice; k++){
            for(int i = 0; i < velikostMatice; i++){
                for(int j = 0; j < velikostMatice; j++){
                    double hodnotaCesty = distancniMatice.getCislo(i, k) + distancniMatice.getCislo(k, j);
                    if((distancniMatice.getCislo(i, j) > hodnotaCesty)){
                        distancniMatice.setCislo(i, j, hodnotaCesty);
                        maticePredchudcu.setCislo(i, j, k + 1);
                    }
                }
            }
        }
        maticePredchudcu.vyplnNulyNaDiagonalu();
    }

    /**
     * Vrátí sezman všech mezicest z bodu A do bodu B
     * @param zacatek začínající bod grafu
     * @param konec konečný bod grafu
     * @return seznam mezicest A, B
     */
    public List<Cesta> najdiNejkratsiCestu(AMisto zacatek, AMisto konec){
        ArrayList<Cesta> nejkratsiCesta = new ArrayList<>();
        AMisto zacatekTrasy = zacatek;

        int IDmisto;
        AMisto predchudce;

        while(true) {
            IDmisto = maticePredchudcu.getCislo(konec.getID() - 1, zacatekTrasy.getID() - 1);
            if(IDmisto == Integer.MAX_VALUE){
                break;
            }
            predchudce = data.getMista().get(IDmisto);
            nejkratsiCesta.add(new Cesta(zacatekTrasy, predchudce));
            if (predchudce == konec) {
                break;
            }
            zacatekTrasy = predchudce;
        }
        return nejkratsiCesta;
    }

    /**
     * Vrátí sezman všech mezicest z bodu A do bodu B
     * @param oaza začínající bod grafu
     * @return seznam mezicest A, B
     */
    public AMisto najdiNejblizsiSklad(AMisto oaza){
        AMisto sklad = null;
        double cena;
        double nejkratsiCesta = Double.MAX_VALUE;

        for(AMisto s : data.getSklady()){
            cena = distancniMatice.getCislo(s.getID() - 1, oaza.getID() - 1);
            if(cena < nejkratsiCesta){
                nejkratsiCesta = cena;
                sklad = s;
            }
        }
        return sklad;
    }

    /**
     * Naplní sklady
     */
    public void naplnSklady(){
        data.getSklady().forEach(Sklad::doplnSklad);
    }

    /**
     * Vygeneruj vhodného velblouda na požadavek
     * TODO: Zatím vybírá "hloupě" podle poměru, vyšší poměr má vyšší prioritu
     * @param celkovaVzdalenost vzdálenost požadavku
     * @param nejblizsiSklad nejbližší sklad vůči cílové oáze
     * @return velbloud vhodný pro vykonání požadavku
     */
    public VelbloudSimulace generujVelblouda(double celkovaVzdalenost, AMisto nejblizsiSklad){
        VelbloudSimulace vel = null;
        for (VelbloudTyp typ : data.getVelbloudi()) {
            if (typ.getMinVzdalenost() > celkovaVzdalenost) {
                vel = typ.generujVelblouda((Sklad)nejblizsiSklad);
                vel.setCasNaAkci(simulacniCas);
                casovaFrontaVelbloudu.add(vel);
            }
        }
        return vel;
    }

    /**
     * Najdi a vyber neobsloužený požadavek
     * @return neobsloužený požadavek
     */
    public Pozadavek neobslouzenyPozadavek(){
        for(Pozadavek p : data.getPozadavky()){
            if(!p.jeSplnen() && p.getDeadline() < simulacniCas) {
                return p;
            }
        }
        return null;
    }

    /**
     * Zkontroluj jestli už jsou všechny požadavky (i budoucí) jsou splněny
     * @return true pokud jsou splněny všechny požadavky
     */
    public boolean vsechnyPozadavkyObslouzeny(){
        for(Pozadavek p : data.getPozadavky()){
            if(!p.jeSplnen()){
                return false;
            }
        }
        return true;
    }

    /**
     * Zkontroluj jestli jsou všichni velbloudi ve stavu VOLNY
     * @return true pokud jsou všichni velbloudi volní
     */
    public boolean vsichniVelbloudiVolni(){
        for(VelbloudSimulace velSim : casovaFrontaVelbloudu){
            if (velSim.getVykonavanaAkce() != VelbloudAkce.VOLNY) {
                return false;
            }
        }
        return true;
    }

    /**
     * Ukonči simulaci neúspěchem
     * @param oaza oáza která zkrachovala Harpagona
     */
    public void ukonciSimulaci(AMisto oaza){
        System.out.println("Cas: "+ (int)simulacniCas +", Oaza: "+ ((Oaza)oaza).getIDOaza() +", Vsichni vymreli, Harpagon zkrachoval, Konec simulace");
        System.exit(1);
    }

    //////////////////////
    //* Private metody *//
    //////////////////////

    /**
     * Vytvoří mapu vzdáleností z počátku, Dijkstrův algoritmus
     * @param vychoziBod začínající bod algoritmu
     * @return mapa předchůdců
     */
    public Map<AMisto, GrafVrchol> vytvorMapuPredchudcu(AMisto vychoziBod){
        Map<AMisto, GrafVrchol> mapaPredchudcu = new HashMap<>();
        GrafVrchol vychoziVrchol = new GrafVrchol(vychoziBod, 0.0);

        // Nastav predchudce na null, vzdalenost na inf
        data.getMista().forEach((k, v) -> mapaPredchudcu.put(v, new GrafVrchol(null, Double.MAX_VALUE)));

        // Zpracovani fronty Dijkstry
        PriorityQueue<GrafVrchol> fronta = new PriorityQueue<>(Comparator.comparingDouble(GrafVrchol::getVzdalenost));
        mapaPredchudcu.put(vychoziBod, vychoziVrchol);
        fronta.add(new GrafVrchol(vychoziBod, 0));

        while (fronta.size() > 0) {
            AMisto predchudce = fronta.poll().getVrchol();

            // Vychozi misto nema zadne sousedy
            if(mapa.seznamSousednosti.get(predchudce) == null){
                break;
            }

            // Projdi vsechny sousedy
            for (GrafVrchol sousedniVrchol : mapa.seznamSousednosti.get(predchudce)) {
                AMisto soused = sousedniVrchol.getVrchol();
                double vzdalenostSouseda = mapaPredchudcu.get(predchudce).getVzdalenost() + sousedniVrchol.getVzdalenost();

                // Pokud je cesta kratsi, zapis souseda jako predchudce
                if (mapaPredchudcu.get(soused).getVzdalenost() > vzdalenostSouseda) {
                    mapaPredchudcu.put(soused, new GrafVrchol(predchudce, vzdalenostSouseda));
                    fronta.add(new GrafVrchol(soused, mapaPredchudcu.get(soused).getVzdalenost()));
                }
            }
        }
        Map<AMisto, CestaCasti> mapaCest = new HashMap<>();
        mapaPredchudcu.forEach((k, v) -> mapaCest.put(k, najdiNejkratsiCestuDijkstra(mapaPredchudcu, vychoziBod, k)));

        System.out.println("Vzdalenosti:");
        mapaPredchudcu.forEach((k, v) -> System.out.println(k.getID() + " -> " + v.getVzdalenost()));
        System.out.println();
        System.out.println("Predchudci:");
        mapaPredchudcu.forEach((k, v) -> System.out.println(k.getID() + ": " + v.getVrchol()));
        System.out.println();
        System.out.println("Cesty cely:");
        mapaCest.forEach((k, v) -> System.out.println(k.getID() + ": " + v));
        System.out.println();

        return null;
    }

    /**
     * Vrátí nejkratší cestu mezi dvěmi body
     * @param zacatek počáteční bod
     * @param konec koncový bod
     * @return cesta po částech
     */
    private CestaCasti najdiNejkratsiCestuDijkstra(Map<AMisto, GrafVrchol> mapaPredchudcu, AMisto zacatek, AMisto konec){
        CestaCasti nejkratsiCesta = new CestaCasti();
        AMisto konecTrasy = konec;
        AMisto predchudce;

        while(true) {
            predchudce = mapaPredchudcu.get(konecTrasy).getVrchol();
            if(predchudce == null){
                break;
            }
            nejkratsiCesta.pridejCestuNaZacatek(new Cesta(predchudce, konecTrasy));
            if (predchudce == zacatek) {
                break;
            }
            konecTrasy = predchudce;
        }
        nejkratsiCesta.uzavriCestu();

        return nejkratsiCesta;
    }

    /**
     * Vytvoří graf jako spojový seznam
     * @return mapa grafu
     */
    private GrafMapa vytvorMapu(){
        GrafMapa novaMapa = new GrafMapa();

        for(CestaSymetricka cesta : data.getCesty()){
            novaMapa.pridejVrchol(cesta);
        }

        return novaMapa;
    }

    /**
     * Přiřadí požadavek vhodnému velbloudovi
     * Pokud neexistuje vhodný velbloud, vytvoří ho v nejbližším skladu oázy
     */
    private void priradPozadavekVelbloudovi() {
        Pozadavek dalsiPozadavek = casovaFrontaPozadavku.poll();
        assert dalsiPozadavek != null;
        boolean pozadavekPrirazen = false;

        // Vyhledej cestu do oázy
        AMisto pozadavekOaza = dalsiPozadavek.getOaza();
        AMisto nejblizsiSklad = najdiNejblizsiSklad(pozadavekOaza);
        List<Cesta> cestaCasti = najdiNejkratsiCestu(nejblizsiSklad, pozadavekOaza);

        // Vypočítej celkovou vzdálenost
        double celkovaVzdalenost = 0;
        for (Cesta c : cestaCasti) {
            celkovaVzdalenost += c.getVzdalenost();
        }

        // Zkus přiřadit požadavek existujícímu velbloudovi
        for (VelbloudSimulace v : casovaFrontaVelbloudu) {
            double casNovehoPozadavku = v.jakDlouhoBudeTrvatCestaTam(celkovaVzdalenost, dalsiPozadavek.getPozadavekKosu());
            double casPozadavkuVelblouda = v.kdySeSplniFronta();

            if (dalsiPozadavek.getDeadline() - casNovehoPozadavku - simulacniCas - casPozadavkuVelblouda > 0) {
                pozadavekPrirazen = true;
                v.priradPozadavek(new VelbloudPozadavek(dalsiPozadavek, cestaCasti), simulacniCas);
                break;
            }
        }

        // Vyber nejlepší druh velblouda na cestu, vytvoř ho, přiřaď mu požadavek
        if (!pozadavekPrirazen) {
            VelbloudSimulace vel = generujVelblouda(celkovaVzdalenost, nejblizsiSklad);
            if(vel != null){
                pozadavekPrirazen = true;
                vel.priradPozadavek(new VelbloudPozadavek(dalsiPozadavek, cestaCasti), simulacniCas);
            }
        }

        // Požadavek nejde přiřadit, simulace bude pokračovat ale časem spadne
        if (!pozadavekPrirazen) {
            System.out.println("Požadavek nepůjde obsloužit, pokračujem v simulaci");
        }

        int zaokrouhlenyCas = (int)Math.round(simulacniCas);
        int zaokrouhlenaDeadline = (int)Math.round(dalsiPozadavek.getDeadline());
        System.out.println("Prichod pozadavku \t Cas: " + zaokrouhlenyCas + ", Pozadavek: " + dalsiPozadavek.getID() + ", Oaza: " + ((Oaza) dalsiPozadavek.getOaza()).getIDOaza() + ", Pocet kosu: " + dalsiPozadavek.getPozadavekKosu() + ", Deadline: " + zaokrouhlenaDeadline);
    }
}
