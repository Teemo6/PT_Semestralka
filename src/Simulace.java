import java.util.*;

/**
 * Instance třídy {@code Simulace} představuje jedináčka ve kterém běží celá simulace
 * @author Mikuláš Mach, Štěpán Faragula
 * @version 1.28 20-11-2022
 */
public class Simulace {
    /** Instance jedináčka Simulace */
    private static final Simulace INSTANCE = new Simulace();

    /** Model simulace */
    private static final VstupDat data = VstupDat.getInstance();
    private static final GrafMapa mapa = GrafMapa.getInstance();
    private static final VelbloudGenerator velGen = VelbloudGenerator.getInstance();

    /** Práce s časem */
    double simulacniCas;
    PriorityQueue<Pozadavek> frontaPozadavku;
    PriorityQueue<Sklad> frontaSkladu;
    PriorityQueue<VelbloudSimulace> frontaVelbloudu;

    /** Podmínky ukončení simulace */
    private final static List<Pozadavek> neobslouzenePozadavky = new ArrayList<>();
    private boolean simulaceBezi;
    private Pozadavek neobslouzenyPozadavek = null;

    /** Pokus o optimalizaci simulace */
    private boolean optimalizace;
    private final int OPTIMALIZACE_SKLADY = 100;

    /**
     * Vrátí jedináčka
     * @return instance jedináčka
     */
    public static Simulace getInstance(){
        return INSTANCE;
    }

    /**
     * Spustí simulaci, obsahuje hlavní smyčku s časem
     * @param vstupniSoubor Vstupní data
     */
    public void spustSimulaci(String vstupniSoubor){
        // Inicializace simulace
        long casSpusteniSimulace = System.currentTimeMillis();

        data.vytvorObjekty(vstupniSoubor);
        System.out.println("\nData nactena: " + (System.currentTimeMillis() - casSpusteniSimulace) + " ms.");

        mapa.vytvorGraf(data.getCesty());
        System.out.println("\nMapa vytvorena: " + (System.currentTimeMillis() - casSpusteniSimulace) + " ms.");

        velGen.vytvorGenerator(data.getVelbloudi());
        System.out.println("\nGenerator pripraven: " + (System.currentTimeMillis() - casSpusteniSimulace) + " ms.\n");

        // Má cenu optimalizovat, je málo skladů?
        optimalizace = data.getSklady().size() < OPTIMALIZACE_SKLADY && data.getMista().size() - OPTIMALIZACE_SKLADY > 0;

        // Simulacni cas
        simulacniCas = 0;
        simulaceBezi = true;

        // Casove fronty
        frontaPozadavku = new PriorityQueue<>(5, Comparator.comparingDouble(Pozadavek::getCasPrichodu).thenComparing(Pozadavek::getDeadline));
        frontaSkladu = new PriorityQueue<>(5, Comparator.comparingDouble(Sklad::getCasDalsiAkce));
        frontaVelbloudu = new PriorityQueue<>(5, Comparator.comparingDouble(VelbloudSimulace::getCasNaAkci));

        frontaPozadavku.addAll(data.getPozadavky());
        frontaSkladu.addAll(data.getSklady());

        // Zpracovani fronty
        Pozadavek dalsiPozadavek;
        Sklad dalsiSklad;
        VelbloudSimulace dalsiVel;
        double casPozadavek, casSklad, casVel;

        // Podminka ukonceni
        neobslouzenePozadavky.addAll(data.getPozadavky());

        while(simulaceBezi){
            dalsiPozadavek = frontaPozadavku.peek();
            dalsiSklad = frontaSkladu.peek();
            dalsiVel = frontaVelbloudu.peek();

            casPozadavek = casSklad = casVel = Double.MAX_VALUE;

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
                continue;
            }
            if(casSklad <= simulacniCas){
                Sklad docasnySklad = frontaSkladu.poll();
                assert docasnySklad != null;

                docasnySklad.doplnSklad();
                frontaSkladu.add(docasnySklad);
                continue;
            }
            if(casVel <= simulacniCas){
                VelbloudSimulace docasnyVelbloud = frontaVelbloudu.poll();
                assert docasnyVelbloud != null;

                docasnyVelbloud.vykonejDalsiAkci();
                frontaVelbloudu.add(docasnyVelbloud);
                continue;
            }

            // Zkontroluj podminky ukonceni simulace
            zkontrolujKonecSimulace();
            if(!simulaceBezi){
                break;
            }

            // Nastav simulační čas na další nejbližsí událost
            simulacniCas = Math.min(Math.min(casPozadavek, casSklad), casVel);
        }
        int pocetPozadavku = data.getPozadavky().size() - neobslouzenePozadavky.size();
        System.out.println();
        System.out.println("Pocet splnenych pozadavku: " + pocetPozadavku);
        System.out.println("Pocet generovanych velbloudu: " + frontaVelbloudu.size());
        System.out.println("SimCas: " + simulacniCas);
        System.out.println("\nRuntime: " + (System.currentTimeMillis() - casSpusteniSimulace) + " ms.");
    }

    /**
     * Označí požadavek ze svého seznamu jako splněný
     * @param p jaký je spněný požadavek
     */
    public static void oznacSplnenyPozadavek(Pozadavek p){
        neobslouzenePozadavky.remove(p);
    }

    //////////////////////
    //* Private metody *//
    //////////////////////

    /**
     * Ukončí simulaci pokud dojde deadline nebo jsou požadavky obslouženy
     */
    private void zkontrolujKonecSimulace(){
        // Zkontroluj podmínky úspešné simulace
        if(vsechnyPozadavkyObslouzeny() && vsichniVelbloudiVolni()){
            simulaceBezi = false;
        }

        // Zkontroluj podmínky neúspešné simulace
        if(neobslouzenyPozadavek != null){
            System.out.println("Cas: "+ (int)simulacniCas +", Oaza: "+ ((Oaza)neobslouzenyPozadavek.getOaza()).getIDOaza() +", Vsichni vymreli, Harpagon zkrachoval, Konec simulace");
            simulaceBezi = false;
        }
    }

    /**
     * Zkontroluj jestli už jsou všechny požadavky (i budoucí) jsou splněny
     * @return true pokud jsou splněny všechny požadavky
     */
    private boolean vsechnyPozadavkyObslouzeny(){
        if(neobslouzenePozadavky.size() > 0){
            if(neobslouzenePozadavky.get(0).getDeadline() < simulacniCas){
                neobslouzenyPozadavek = neobslouzenePozadavky.get(0);
            }
            return false;
        }
        return true;
    }

    /**
     * Zkontroluj jestli jsou všichni velbloudi ve stavu VOLNY
     * @return true pokud jsou všichni velbloudi volní
     */
    private boolean vsichniVelbloudiVolni(){
        for(VelbloudSimulace velSim : frontaVelbloudu){
            if (velSim.getVykonavanaAkce() != VelbloudAkce.VOLNY) {
                return false;
            }
        }
        return true;
    }

    /**
     * Přiřadí požadavek vhodnému velbloudovi
     * Pokud neexistuje vhodný velbloud, vytvoří ho v nejbližším skladu oázy
     */
    private void priradPozadavekVelbloudovi() {
        Pozadavek dalsiPozadavek = frontaPozadavku.poll();
        assert dalsiPozadavek != null;
        boolean pozadavekPrirazen = false;
        int zaokrouhlenyCas = (int)Math.round(simulacniCas);
        int zaokrouhlenaDeadline = (int)Math.round(dalsiPozadavek.getDeadline());

        // Vyhledej cestu do oázy
        AMisto pozadavekOaza = dalsiPozadavek.getOaza();
        AMisto nejblizsiSklad = mapa.najdiNejblizsiSklad(pozadavekOaza, data.getSklady(), velGen.nejvetsiPrumernaVzdalenost(), optimalizace);
        CestaCasti nejkratsiCesta = mapa.najdiNejkratsiCestuDijkstra(nejblizsiSklad, pozadavekOaza, velGen.nejvetsiPrumernaVzdalenost(), optimalizace);

        // Cesta je INF (neexistuje)
        if(nejkratsiCesta.getVzdalenost() == Double.MAX_VALUE){
            System.out.println("Pozadavek " + dalsiPozadavek.getID() + " nejde obslouzit, simulace pokracuje");
            System.out.println("Prichod pozadavku \t Cas: " + zaokrouhlenyCas + ", Pozadavek: " + dalsiPozadavek.getID() + ", Oaza: " + ((Oaza) dalsiPozadavek.getOaza()).getIDOaza() + ", Pocet kosu: " + dalsiPozadavek.getPozadavekKosu() + ", Deadline: " + zaokrouhlenaDeadline);
            return;
        }

        double nejdelsiUsekCesty = nejkratsiCesta.getNejdelsiUsek();

        // Zkus přiřadit požadavek existujícímu velbloudovi
        for (VelbloudSimulace vel : frontaVelbloudu) {
            if(nejdelsiUsekCesty <= vel.getMaxVzdalenost()) {
                pozadavekPrirazen = zkusPriraditPozadavekVelbloudovi(vel, dalsiPozadavek, nejkratsiCesta);
                if(pozadavekPrirazen){
                    break;
                }
            }
        }

        // Vyber vhodný druh velblouda, vytvoř ho, přiřaď mu požadavek
        if (!pozadavekPrirazen) {
            pozadavekPrirazen = zkusPriraditPozadavekVelbloudovi(generujVhodnehoVelblouda(nejkratsiCesta, nejblizsiSklad), dalsiPozadavek, nejkratsiCesta);
        }

        // Požadavek nejde přiřadit, simulace bude pokračovat ale časem spadne
        if (!pozadavekPrirazen) {
            System.out.println("Pozadavek " + dalsiPozadavek.getID() + " nejde obslouzit, simulace pokracuje");
        }
        System.out.println("Prichod pozadavku \t Cas: " + zaokrouhlenyCas + ", Pozadavek: " + dalsiPozadavek.getID() + ", Oaza: " + ((Oaza) dalsiPozadavek.getOaza()).getIDOaza() + ", Pocet kosu: " + dalsiPozadavek.getPozadavekKosu() + ", Deadline: " + zaokrouhlenaDeadline);
    }

    /**
     * Počítání jestli velbloud stihne splnit požadavek
     * @param vel velbloud vybraný pro požadavek
     * @param dalsiPozadavek požadavek
     * @param nejkratsiCesta cesta
     * @return true pokud velbloud stihne obsloužit požadavek
     */
    private boolean zkusPriraditPozadavekVelbloudovi(VelbloudSimulace vel, Pozadavek dalsiPozadavek, CestaCasti nejkratsiCesta){
        double casNovehoPozadavku = vel.kdySplniPozadavek(nejkratsiCesta, dalsiPozadavek.getPozadavekKosu());
        double casFrontyVelblouda = vel.kdySeSplniFronta();

        if (dalsiPozadavek.getDeadline() - simulacniCas - casNovehoPozadavku - casFrontyVelblouda > 0) {
            vel.priradPozadavek(new VelbloudPozadavek(dalsiPozadavek, nejkratsiCesta), simulacniCas);
            return true;
        }
        return false;
    }

    /**
     * Vygeneruj vhodného velblouda na požadavek
     * @param cesta cesta po částech
     * @param domovskySklad nejbližší sklad vůči cílové oáze
     * @return velbloud vhodný pro vykonání požadavku
     */
    private VelbloudSimulace generujVhodnehoVelblouda(CestaCasti cesta, AMisto domovskySklad){
        Map<VelbloudTyp, Boolean> uzTenhleTypByl = new HashMap<>();
        double nejdelsiUsek = cesta.getNejdelsiUsek();
        VelbloudSimulace vel = null;
        VelbloudTyp vhodnyTyp = null;

        for(VelbloudTyp v : data.getVelbloudi()){
            uzTenhleTypByl.put(v, false);
        }

        while(uzTenhleTypByl.containsValue(false)){
            VelbloudTyp dalsiTyp = velGen.dalsiVelbloudPodlePomeru();

            vel = velGen.generujVelblouda((Sklad)domovskySklad);

            if (dalsiTyp.getVetsiPrumerVzdalenost() >= nejdelsiUsek) {
                vhodnyTyp = dalsiTyp;
                if(vel.getMaxVzdalenost() >= nejdelsiUsek){
                    vel.setCasNaAkci(simulacniCas);
                    frontaVelbloudu.add(vel);
                    return vel;
                }
            } else {
                vel.setCasNaAkci(Double.MAX_VALUE);
                frontaVelbloudu.add(vel);
            }

            // Typ je vhodny, jenom se vygeneroval spatny velbloud
            if(vel.getTyp() == vhodnyTyp){
                continue;
            }
            uzTenhleTypByl.put(dalsiTyp, true);
        }
        return vel;
    }
}
