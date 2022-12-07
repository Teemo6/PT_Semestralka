import java.util.*;

/**
 * Instance třídy {@code GrafMapa} představuje neorientovaný matematický graf
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.27 20-11-2022
 */
public class GrafMapa {
    private Map<AMisto, LinkedList<GrafVrchol>> seznamSousednosti;
    private Map<AMisto, Map<AMisto, GrafVrchol>> seznamPredchozichMap;

    /** Instance jedináčka Simulace */
    private static final GrafMapa INSTANCE = new GrafMapa();

    /**
     * Vrátí jedináčka
     * @return instance jedináčka
     */
    public static GrafMapa getInstance(){
        return INSTANCE;
    }

    /**
     * Vytvoří graf jako spojový seznam
     * @param cesty seznam cest které tvoří graf
     */
    public void vytvorGraf(List<Cesta> cesty){
        seznamSousednosti = new HashMap<>();
        seznamPredchozichMap = new HashMap<>();

        for(Cesta cesta : cesty){
            pridejVrchol(cesta);
        }
    }

    /**
     * Vrátí nejkratší cestu mezi dvěmi body
     * @param zacatek počáteční bod
     * @param konec koncový bod
     * @param omezeni omezení minimální vzdáleností velblouda
     * @return cesta po částech
     */
    public CestaCasti najdiNejkratsiCestuDijkstra(AMisto zacatek, AMisto konec, double omezeni){
        CestaCasti nejkratsiCesta = new CestaCasti();
        AMisto konecTrasy = konec;
        AMisto predchudce;
        Map<AMisto, GrafVrchol> mapaPredchudcu;

        // Koukni se, jestli už jsi nevytvořil mapu předtím

            mapaPredchudcu = seznamPredchozichMap.get(zacatek);
            if (mapaPredchudcu == null) {
                seznamPredchozichMap.put(zacatek, vytvorMapuPredchudcu(zacatek, omezeni));
                mapaPredchudcu = seznamPredchozichMap.get(zacatek);
            }

        // Cesta neexistuje
        if(mapaPredchudcu.isEmpty()){
            return CestaCasti.nekonecnaCesta();
        }

        // Cesta existuje
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
     * Vrátí dostačující (ne nejlepší!) cestu mezi dvěmi vrcholy
     * @param vychoziBod výchozí bod
     * @param konecnyBod koncový bod
     * @param omezeni omezení vzdálenosti
     * @return dostačující cesta
     */
    public CestaCasti najdiCestuDoKoncovehoBodu(AMisto vychoziBod, AMisto konecnyBod, double omezeni){
        CestaCasti cestaSklad = new CestaCasti();
        Map<AMisto, GrafVrchol> mapaPredchudcu = new HashMap<>();
        GrafVrchol vychoziVrchol = new GrafVrchol(vychoziBod, 0.0);

        // Naplň mapu předchůdců hodnotou INF
        for(Map.Entry<AMisto, LinkedList<GrafVrchol>> entry : seznamSousednosti.entrySet()){
            mapaPredchudcu.put(entry.getKey(), new GrafVrchol(null, Double.MAX_VALUE));
        }

        // Zpracovani fronty Dijkstry
        PriorityQueue<GrafVrchol> fronta = new PriorityQueue<>(Comparator.comparingDouble(GrafVrchol::getVzdalenost));
        mapaPredchudcu.put(vychoziBod, vychoziVrchol);
        fronta.add(new GrafVrchol(vychoziBod, 0));

        while (fronta.size() > 0) {
            AMisto predchudce = fronta.poll().getVrchol();
            // Ukonči procházení Dijkstry, narazil jsi na koncový bod a má dostačující vzdálenost
            if(predchudce == konecnyBod){
                cestaSklad = backtracking(vychoziBod, konecnyBod, mapaPredchudcu);
                break;
            }

            // Vychozi misto nema zadne sousedy
            if(seznamSousednosti.get(predchudce) == null){
                return CestaCasti.nekonecnaCesta();
            }

            // Projdi vsechny sousedy
            for (GrafVrchol sousedniVrchol : seznamSousednosti.get(predchudce)) {
                AMisto soused = sousedniVrchol.getVrchol();
                double vzdalenostSouseda = mapaPredchudcu.get(predchudce).getVzdalenost() + sousedniVrchol.getVzdalenost();

                if(sousedniVrchol.getVzdalenost() > omezeni && soused == konecnyBod){
                    return CestaCasti.nekonecnaCesta();
                }

                // Pokud je cesta kratsi, zapis souseda jako predchudce
                if (mapaPredchudcu.get(soused).getVzdalenost() > vzdalenostSouseda) {
                    mapaPredchudcu.put(soused, new GrafVrchol(predchudce, vzdalenostSouseda));
                    fronta.add(new GrafVrchol(soused, mapaPredchudcu.get(soused).getVzdalenost()));
                }
            }
        }
        return cestaSklad;
    }

    //////////////////////
    //* Private metody *//
    //////////////////////

    /**
     * Vytvoří mapu vzdáleností z počátku, Dijkstrův algoritmus
     * @param vychoziBod začínající bod algoritmu
     * @return mapa předchůdců
     */
    private Map<AMisto, GrafVrchol> vytvorMapuPredchudcu(AMisto vychoziBod, double omezeni){
        Map<AMisto, GrafVrchol> mapaPredchudcu = new HashMap<>();
        GrafVrchol vychoziVrchol = new GrafVrchol(vychoziBod, 0.0);

        for(Map.Entry<AMisto, LinkedList<GrafVrchol>> entry : seznamSousednosti.entrySet()){
            mapaPredchudcu.put(entry.getKey(), new GrafVrchol(null, Double.MAX_VALUE));
        }

        // Zpracovani fronty Dijkstry
        PriorityQueue<GrafVrchol> fronta = new PriorityQueue<>(Comparator.comparingDouble(GrafVrchol::getVzdalenost));
        mapaPredchudcu.put(vychoziBod, vychoziVrchol);
        fronta.add(new GrafVrchol(vychoziBod, 0));

        while (fronta.size() > 0) {
            AMisto predchudce = fronta.poll().getVrchol();

            // Vychozi misto nema zadne sousedy
            if(seznamSousednosti.get(predchudce) == null){
                return new HashMap<>();
            }

            // Projdi vsechny sousedy
            for (GrafVrchol sousedniVrchol : seznamSousednosti.get(predchudce)) {
                AMisto soused = sousedniVrchol.getVrchol();
                double vzdalenostSouseda = mapaPredchudcu.get(predchudce).getVzdalenost() + sousedniVrchol.getVzdalenost();

                if(sousedniVrchol.getVzdalenost() > omezeni){
                    continue;
                }

                // Pokud je cesta kratsi, zapis souseda jako predchudce
                if (mapaPredchudcu.get(soused).getVzdalenost() > vzdalenostSouseda) {
                    mapaPredchudcu.put(soused, new GrafVrchol(predchudce, vzdalenostSouseda));
                    fronta.add(new GrafVrchol(soused, mapaPredchudcu.get(soused).getVzdalenost()));
                }
            }
        }
        return mapaPredchudcu;
    }

    /**
     * Vrátí cestu ve správném směru mezi dvěmi body
     * @param zacatek výchozí bod
     * @param konec koncový bod
     * @param mapaPredchudcu mapa kudy se dostat z konce do začátku
     * @return cesta od začátku do konce
     */
    private CestaCasti backtracking(AMisto zacatek, AMisto konec, Map<AMisto, GrafVrchol> mapaPredchudcu){
        CestaCasti cestaVeSpravnymSmeru = new CestaCasti();
        AMisto konecClanku = konec;
        AMisto zacatekClanku = konec;

        while(true) {
            if(zacatekClanku == null || zacatekClanku == zacatek){
                break;
            }
            zacatekClanku = mapaPredchudcu.get(konecClanku).getVrchol();
            cestaVeSpravnymSmeru.pridejCestuNaZacatek(new Cesta(zacatekClanku, konecClanku));
            konecClanku = zacatekClanku;
        }
        cestaVeSpravnymSmeru.uzavriCestu();
        return cestaVeSpravnymSmeru;
    }

    /**
     * Přidá vrchol do grafu
     * @param cesta hrana grafu
     */
    private void pridejVrchol(Cesta cesta){
        AMisto mistoA = cesta.getZacatek();
        AMisto mistoB = cesta.getKonec();
        double vzdalenost = cesta.getVzdalenost();

        seznamSousednosti.putIfAbsent(mistoA, new LinkedList<>());
        seznamSousednosti.putIfAbsent(mistoB, new LinkedList<>());

        seznamSousednosti.get(mistoA).add(new GrafVrchol(mistoB, vzdalenost));
        seznamSousednosti.get(mistoB).add(new GrafVrchol(mistoA, vzdalenost));
    }
}
