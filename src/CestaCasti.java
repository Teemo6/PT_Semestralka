import java.util.ArrayList;
import java.util.List;

/**
 * Instance třídy {@code CestaCasti} představuje dlouhou cestu rozdělenou na několik částí {@code Cesta}
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.24 17-11-2022
 */
public class CestaCasti {
    List<Cesta> seznamCest;
    AMisto zacatek;
    AMisto konec;
    double vzdalenost;
    double nejdelsiUsek;
    boolean uzavrena;

    /**
     * Otevřený konstruktor, můžou se přidávat cesty, poté je potřeba uzavřít
     */
    public CestaCasti(){
        seznamCest = new ArrayList<>();
        zacatek = null;
        konec = null;
        vzdalenost = Double.MAX_VALUE;
        nejdelsiUsek = 0;
        uzavrena = false;
    }

    /**
     * Uzavřený konstruktor, ze seznamu cest udělá objekt
     * @param cesty seznam cest, očekává seřazený seznam (1 -> 2, 2 -> 3, 3 -> 4...)
     */
    public CestaCasti(List<Cesta> cesty){
        this();

        for(Cesta c : cesty){
            seznamCest.add(c);
            vzdalenost += c.getVzdalenost();
            if(c.getVzdalenost() > nejdelsiUsek){
                nejdelsiUsek = c.getVzdalenost();
            }
        }
        uzavrena = true;
        zacatek = seznamCest.get(0).getZacatek();
        konec = seznamCest.get(seznamCest.size() - 1).getKonec();
    }

    /**
     * Vrátí cestu se vzdáleností INF
     * @return cesta se vzdáleností INF
     */
    public static CestaCasti nekonecnaCesta(){
        CestaCasti c = new CestaCasti();
        c.uzavriCestu();
        return c;
    }

    /**
     * Přidá cestu do seznamu
     * Pokud je CestaCasti uzavřená, neprovede nic
     * @param c cesta ke přidání
     */
    public void pridejCestu(Cesta c){
        if(uzavrena){
            return;
        }
        seznamCest.add(c);
        if(vzdalenost == Double.MAX_VALUE){
            vzdalenost = 0;
        }
        vzdalenost += c.getVzdalenost();
        if(c.getVzdalenost() > nejdelsiUsek){
            nejdelsiUsek = c.getVzdalenost();
        }
    }

    /**
     * Přidá cestu do seznamu
     * Pokud je CestaCasti uzavřená, neprovede nic
     * @param c cesta ke přidání
     */
    public void pridejCestuNaZacatek(Cesta c){
        if(uzavrena){
            return;
        }
        seznamCest.add(0, c);
        if(vzdalenost == Double.MAX_VALUE){
            vzdalenost = 0;
        }
        vzdalenost += c.getVzdalenost();
        if(c.getVzdalenost() > nejdelsiUsek){
            nejdelsiUsek = c.getVzdalenost();
        }
    }

    /**
     * Uzavře cestu, poté se nemůže upravovat
     * Nastaví začátek a konec
     */
    public void uzavriCestu(){
        if(uzavrena){
            return;
        }
        uzavrena = true;

        if(seznamCest.isEmpty()){
            return;
        }
        zacatek = seznamCest.get(0).getZacatek();
        konec = seznamCest.get(seznamCest.size() - 1).getKonec();
    }

    /**
     * Vytvoří novou cestu po částech v obráceném směru
     * Pokud je CestaCasti uzavřená, neprovede nic
     * @return cesta po částech pozpátku
     */
    public CestaCasti prohodSmer(){
        if(!uzavrena){
            return null;
        }
        List<Cesta> cestaZpet = new ArrayList<>();

        for(int i = seznamCest.size() - 1; i >= 0; i--){
            cestaZpet.add(seznamCest.get(i).prohodSmer());
        }
        return new CestaCasti(cestaZpet);
    }

    /**
     * Vrátí začáteční bod
     * @return začáteční bod
     */
    public AMisto getZacatek() {
        return zacatek;
    }

    /**
     * Vrátí konečný bod cesty
     * @return konečný bod
     */
    public AMisto getKonec() {
        return konec;
    }

    /**
     * Vrátí celkovou vzdálenost cesty
     * @return celková vzdálenost cesty
     */
    public double getVzdalenost() {
        return vzdalenost;
    }

    /**
     * Vrátí nejdelší úsek cesty
     * @return nejdelší úsek ze všech cest
     */
    public double getNejdelsiUsek() {
        return nejdelsiUsek;
    }

    /**
     * Vrátí seznam cest ze kterých se skládá CestaCasti
     * @return seznam cest
     */
    public List<Cesta> getSeznamCest(){
        if(uzavrena){
            return seznamCest;
        }
        System.out.println("Nejdrive je potreba uzavrit cestu, vracim prazdny seznam");
        return new ArrayList<>();
    }

    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();

        for(Cesta c : seznamCest){
            str.append(c).append(", ");
        }

        return str.toString();
    }
}
