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
     * @param optimalizace jestli se má zapnout mód optimalizace
     * @return cesta po částech
     */
    public CestaCasti najdiNejkratsiCestuDijkstra(AMisto zacatek, AMisto konec, double omezeni, boolean optimalizace){
        CestaCasti nejkratsiCesta = new CestaCasti();
        AMisto konecTrasy = konec;
        AMisto predchudce;
        Map<AMisto, GrafVrchol> mapaPredchudcu;

        // Koukni se, jestli už jsi nevytvořil mapu předtím
        if(optimalizace) {
            mapaPredchudcu = seznamPredchozichMap.get(zacatek);
            if (mapaPredchudcu == null) {
                seznamPredchozichMap.put(zacatek, vytvorMapuPredchudcu(zacatek, omezeni));
                mapaPredchudcu = seznamPredchozichMap.get(zacatek);
            }
        } else {
            mapaPredchudcu = vytvorMapuPredchudcu(zacatek, omezeni);
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
     * Vrátí nejbližší sklad od oázy
     * @param oaza oáza
     * @param sklady všechny sklady v grafu
     * @param omezeni omezení minimální vzdáleností velblouda
     * @param optimalizace jestli se má zapnout mód optimalizace
     * @return sklad s nejkratší cestou
     */
    public AMisto najdiNejblizsiSklad(AMisto oaza, List<Sklad> sklady, double omezeni, boolean optimalizace){
        AMisto sklad = null;
        double cena;
        double nejkratsiCesta = Double.MAX_VALUE;
        LinkedList<GrafVrchol> sousedi = seznamSousednosti.get(oaza);

        // Pokud nemá sousedy
        if(sousedi == null){
            return null;
        }
        if(sousedi.isEmpty()){
            return null;
        }

        // TODO dost neefektivní
        // Pokud má právě jednoho souseda a tím je sklad
        if(sousedi.size() == 1 && sousedi.get(0).getVrchol().getClass().getSimpleName().equals("Sklad")){
            return seznamSousednosti.get(oaza).getFirst().getVrchol();
        }

        // Projdi všechny sklady a porovnej cesty
        for(AMisto s : sklady){
            cena = najdiNejkratsiCestuDijkstra(s, oaza, omezeni, optimalizace).getVzdalenost();
            if(cena < nejkratsiCesta){
                nejkratsiCesta = cena;
                sklad = s;
            }
        }
        return sklad;
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
