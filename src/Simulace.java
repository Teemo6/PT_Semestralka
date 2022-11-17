import java.util.*;

/**
 * Instance třídy {@code Simulace} představuje jedináčka ve kterém běží celá simulace
 * @author Mikuláš Mach, Štěpán Faragula
 * @version 1.23 15-11-2022
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
    boolean simulaceBezi;
    PriorityQueue<Pozadavek> frontaPozadavku;
    PriorityQueue<Sklad> frontaSkladu;
    PriorityQueue<VelbloudSimulace> frontaVelbloudu;

    /** Pokus o optimalizaci simulace */
    private boolean optimalizace;
    private final int OPTIMALIZACE_SKLADY = 100;
    private final int OPTIMALIZACE_MISTA = 500;

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

        // Má cenu optimalizovat
        optimalizace = data.getSklady().size() < OPTIMALIZACE_SKLADY && data.getMista().size() < OPTIMALIZACE_MISTA;

        // Simulacni cas
        simulacniCas = 0;
        simulaceBezi = true;

        // Casove fronty
        frontaPozadavku = new PriorityQueue<>(5, Comparator.comparingDouble(Pozadavek::getCasPrichodu));
        frontaSkladu = new PriorityQueue<>(5, Comparator.comparingDouble(Sklad::getCasDalsiAkce));
        frontaVelbloudu = new PriorityQueue<>(5, Comparator.comparingDouble(VelbloudSimulace::getCasNaAkci));

        frontaPozadavku.addAll(data.getPozadavky());
        frontaSkladu.addAll(data.getSklady());

        // Zpracovani fronty
        Pozadavek dalsiPozadavek;
        Sklad dalsiSklad;
        VelbloudSimulace dalsiVel;
        double casPozadavek, casSklad, casVel;

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

            // Ukonči simulaci pokud má požadavek po deadline
            Pozadavek neobslouzeny = neobslouzenyPozadavek();
            if(neobslouzeny != null){
                System.out.println("Cas: "+ (int)simulacniCas +", Oaza: "+ ((Oaza)neobslouzeny.getOaza()).getIDOaza() +", Vsichni vymreli, Harpagon zkrachoval, Konec simulace");
                break;
            }

            // Zkontroluj podmínky úspešné simulace
            if(vsechnyPozadavkyObslouzeny() && vsichniVelbloudiVolni()){
                break;
            }

            // Nastav simulační čas na další nejbližsí událost
            simulacniCas = Math.min(Math.min(casPozadavek, casSklad), casVel);
        }
        System.out.println();
        System.out.println("Pocet splnenych pozadavku: " + data.getPozadavky().size());
        System.out.println("Pocet vyuzitych velbloudu: " + frontaVelbloudu.size());
        System.out.println("SimCas: " + simulacniCas);
        System.out.println("\nRuntime: " + (System.currentTimeMillis() - casSpusteniSimulace) + " ms.");
    }

    /**
     * Vygeneruj vhodného velblouda na požadavek
     * @param cesta cesta po částech
     * @param domovskySklad nejbližší sklad vůči cílové oáze
     * @return velbloud vhodný pro vykonání požadavku
     */
    public VelbloudSimulace generujVhodnehoVelblouda(CestaCasti cesta, AMisto domovskySklad){
        Map<VelbloudTyp, Boolean> uzTenhleTypByl = new HashMap<>();
        double nejdelsiUsek = cesta.getNejdelsiUsek();
        VelbloudSimulace vel = null;

        for(VelbloudTyp v : data.getVelbloudi()){
            uzTenhleTypByl.put(v, false);
        }

        while(uzTenhleTypByl.containsValue(false)){
            VelbloudTyp dalsiTyp = velGen.dalsiVelbloudPodlePomeru();

            vel = velGen.generujVelblouda((Sklad)domovskySklad);
            frontaVelbloudu.add(vel);
            uzTenhleTypByl.put(dalsiTyp, true);

            if (dalsiTyp.getMinVzdalenost() > nejdelsiUsek) {
                vel.setCasNaAkci(simulacniCas);
                return vel;
            } else {
                vel.setCasNaAkci(Double.MAX_VALUE);
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
        for(VelbloudSimulace velSim : frontaVelbloudu){
            if (velSim.getVykonavanaAkce() != VelbloudAkce.VOLNY) {
                return false;
            }
        }
        return true;
    }

    //////////////////////
    //* Private metody *//
    //////////////////////

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
        AMisto nejblizsiSklad = mapa.najdiNejblizsiSklad(pozadavekOaza, data.getSklady(), velGen.getNejvetsiMinVzdalenost(), optimalizace);
        CestaCasti nejkratsiCesta = mapa.najdiNejkratsiCestuDijkstra(nejblizsiSklad, pozadavekOaza, velGen.getNejvetsiMinVzdalenost(), optimalizace);

        // Cesta je INF (neexistuje)
        if(nejkratsiCesta.getVzdalenost() == Double.MAX_VALUE){
            System.out.println("Pozadavek " + dalsiPozadavek.getID() + " nejde obslouzit, simulace pokracuje");
            System.out.println("Prichod pozadavku \t Cas: " + zaokrouhlenyCas + ", Pozadavek: " + dalsiPozadavek.getID() + ", Oaza: " + ((Oaza) dalsiPozadavek.getOaza()).getIDOaza() + ", Pocet kosu: " + dalsiPozadavek.getPozadavekKosu() + ", Deadline: " + zaokrouhlenaDeadline);
            return;
        }

        double nejdelsiUsekCesty = nejkratsiCesta.getNejdelsiUsek();

        // Zkus přiřadit požadavek existujícímu velbloudovi
        for (VelbloudSimulace v : frontaVelbloudu) {
            if(nejdelsiUsekCesty <= v.getMaxVzdalenost()) {
                double casNovehoPozadavku = v.jakDlouhoBudeTrvatCestaTam(nejkratsiCesta.getVzdalenost(), dalsiPozadavek.getPozadavekKosu());
                double casPozadavkuVelblouda = v.kdySeSplniFronta();

                if (dalsiPozadavek.getDeadline() - casNovehoPozadavku - simulacniCas - casPozadavkuVelblouda > 0) {
                    pozadavekPrirazen = true;
                    v.priradPozadavek(new VelbloudPozadavek(dalsiPozadavek, nejkratsiCesta), simulacniCas);
                    break;
                }
            }
        }

        // Vyber vhodný druh velblouda, vytvoř ho, přiřaď mu požadavek
        if (!pozadavekPrirazen) {
            VelbloudSimulace vel = generujVhodnehoVelblouda(nejkratsiCesta, nejblizsiSklad);
            double casNovehoPozadavku = vel.jakDlouhoBudeTrvatCestaTam(nejkratsiCesta.getVzdalenost(), dalsiPozadavek.getPozadavekKosu());

            if (dalsiPozadavek.getDeadline() - casNovehoPozadavku - simulacniCas > 0) {
                pozadavekPrirazen = true;
                vel.priradPozadavek(new VelbloudPozadavek(dalsiPozadavek, nejkratsiCesta), simulacniCas);
            }
        }

        // Požadavek nejde přiřadit, simulace bude pokračovat ale časem spadne
        if (!pozadavekPrirazen) {
            System.out.println("Pozadavek " + dalsiPozadavek.getID() + " nejde obslouzit, simulace pokracuje");
        }
        System.out.println("Prichod pozadavku \t Cas: " + zaokrouhlenyCas + ", Pozadavek: " + dalsiPozadavek.getID() + ", Oaza: " + ((Oaza) dalsiPozadavek.getOaza()).getIDOaza() + ", Pocet kosu: " + dalsiPozadavek.getPozadavekKosu() + ", Deadline: " + zaokrouhlenaDeadline);
    }
}
