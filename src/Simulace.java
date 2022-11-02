import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * Instance třídy {@code Simulace} představuje jedináčka ve kterém běží celá simulace
 * @author Mikuláš Mach, Štěpán Faragula
 * @version 1.19 02-11-2022
 */
public class Simulace {
    /** Instance jedináčka Simulace */
    private static final Simulace INSTANCE = new Simulace();
    private final Random random = new Random();

    /** Datový model */
    private VstupDat data;

    /** Matice se kterými pracuje simulace */
    IMaticeSymetricka distancniMatice;
    MaticeInteger maticePredchudcu;

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

        casovaFrontaPozadavku = new PriorityQueue<>();
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

            // Zkontroluj jestli existuje další akce
            if(dalsiPozadavek != null){
                casPozadavek = dalsiPozadavek.getCasPrichodu();
            } else {
                casPozadavek = Double.MAX_VALUE;
            }
            if(dalsiSklad != null){
                casSklad = dalsiSklad.getCasDalsiAkce();
            } else {
                casSklad = Double.MAX_VALUE;
            }
            if(dalsiVel != null){
                casVel = dalsiVel.getCasNaAkci();
            } else {
                casVel = Double.MAX_VALUE;
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
                if (docasny != null) {
                    docasny.vykonejDalsiAkci();
                }
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
        distancniMatice = new MaticeCtvercova(data.getMista().size());
        distancniMatice.vyplnNekonecnem();

        for (Cesta cesta : data.getCesty()) {
            distancniMatice.setCisloSymetricky(cesta.getMistoA().getID() - 1, cesta.getMistoB().getID() - 1, cesta.getVzdalenost());
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
    public ArrayList<Cesta> najdiNejkratsiCestu(AMisto zacatek, AMisto konec){
        ArrayList<Cesta> nejkratsiCesta = new ArrayList<>();

        int IDmisto;
        AMisto predcudce;

        while(true) {
            IDmisto = maticePredchudcu.getCislo(konec.getID() - 1, zacatek.getID() - 1);
            if(IDmisto == Integer.MAX_VALUE){
                break;
            }
            predcudce = data.getMista().get(IDmisto);
            nejkratsiCesta.add(new Cesta(zacatek, predcudce));
            if (predcudce == konec) {
                break;
            }
            zacatek = predcudce;
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
     * Vytvoří konkrétního velblouda
     * @param typ typ velblouda
     * @return entita
     */
    public VelbloudSimulace generujVelblouda(VelbloudTyp typ, Sklad pozice){
        double rychlost = random.nextDouble() * (typ.getMaxRychlost() - typ.getMinRychlost()) + typ.getMinRychlost();

        double stredniHodnota = (typ.getMinVzdalenost() + typ.getMaxVzdalenost()) / 2;          // podle zadání
        double smerodatnaOdchylka = (typ.getMaxVzdalenost() - typ.getMinVzdalenost()) / 4;      // podle zadání
        double vzdalenost = random.nextGaussian() * smerodatnaOdchylka + stredniHodnota;

        return new VelbloudSimulace(pozice, rychlost, vzdalenost, typ);
    }

    /**
     * Přiřadí požadavek vhodnému velbloudovi
     * Pokud neexistuje vhodný velbloud, vytvoří ho v nejbližším skladu oázy
     */
    public void priradPozadavekVelbloudovi() {
        Pozadavek dalsiPozadavek = casovaFrontaPozadavku.poll();
        boolean pozadavekPrirazen = false;

        if (dalsiPozadavek != null) {
            // Vyhledej cestu do oázy
            AMisto pozadavekOaza = dalsiPozadavek.getOaza();
            AMisto nejblizsiSklad = najdiNejblizsiSklad(pozadavekOaza);
            ArrayList<Cesta> cestaCasti = najdiNejkratsiCestu(nejblizsiSklad, pozadavekOaza);

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
        for (VelbloudTyp v : data.getVelbloudi()) {
            if (v.getMinVzdalenost() > celkovaVzdalenost) {
                vel = generujVelblouda(v, (Sklad) nejblizsiSklad);
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
            if(!p.jeSplnen()) {
                if (p.getDeadline() < simulacniCas) {
                    return p;
                }
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
}
