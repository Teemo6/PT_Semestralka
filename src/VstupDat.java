import java.util.*;
import java.io.*;

/**
 * Instance třídy {@code VstupDat} představuje jedináčka, který umí přečíst vstupní soubor a vybrat potřebná data
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.20 06-11-2022
 */
public class VstupDat{
    /** Instance jedináčka VstupDat */
    private static final VstupDat INSTANCE = new VstupDat();

    /** Vytvořené objekty */
    private List<Sklad> sklady;
    private List<Oaza> oazy;
    private Set<CestaSymetricka> cesty;
    private List<VelbloudTyp> velbloudi;
    private List<Pozadavek> pozadavky;
    private Map<Integer, AMisto> mista;

    /** Index průchodu validních dat */
    private int index;

    /**
     * Vrátí jedináčka
     * @return instance jedináčka
     */
    public static VstupDat getInstance(){
        return INSTANCE;
    }

    /**
     * Z validních dat načte parametry pro strukturu programu
     * @param vstupniSoubor soubor ke čtení, předává se privátní metodě vyberValidniData()
     */
    public void vytvorObjekty(String vstupniSoubor){
        List<String> validniData = vyberValidniData(vstupniSoubor);

        sklady = new ArrayList<>();
        oazy = new ArrayList<>();
        cesty = new HashSet<>();
        velbloudi = new ArrayList<>();
        pozadavky = new ArrayList<>();
        mista = new HashMap<>();
        index = 0;

        vytvorSklady(validniData);
        vytvorOazy(validniData);

        // Napln mapu mista
        sklady.forEach(s -> mista.put(s.getID(), s));
        oazy.forEach(o -> mista.put(o.getID(), o));

        vytvorCesty(validniData);
        vytvorVelbloudy(validniData);
        vytvorPozadavky(validniData);

        // TODO Sežazení sestupně podle poměru pro budoucí generování, hloupé
        velbloudi.sort(Comparator.comparingDouble(VelbloudTyp::getPomer).reversed());
    }

    /**
     * Vrátí vytvořené sklady
     * @return sklady
     */
    public List<Sklad> getSklady() {
        return sklady;
    }

    /**
     * Vrátí vytvořené oázy
     * @return oázy
     */
    public List<Oaza> getOazy() {
        return oazy;
    }

    /**
     * Vrátí vytvořené cesty
     * @return cesty
     */
    public Set<CestaSymetricka> getCesty() {
        return cesty;
    }

    /**
     * Vrátí vytvořené velbloudy (typ)
     * @return velbloudi (typ)
     */
    public List<VelbloudTyp> getVelbloudi() {
        return velbloudi;
    }

    /**
     * Vrátí vytvořené požadavky
     * @return požadavky
     */
    public List<Pozadavek> getPozadavky() {
        return pozadavky;
    }

    /**
     * Vrátí vytvořená místa (sklady a oázy dohromady)
     * @return místa (sklady a oázy dohromady)
     */
    public Map<Integer, AMisto> getMista() {
        return mista;
    }

    //////////////////////
    //* Private metody *//
    //////////////////////

    /**
     * Vybere ze souboru všechna data, která se nachází mimo komentáře (data jsou oddělena jakýmkoliv bílým znakem)
     * data uloží jako dynamické pole Stringů
     * pracujeme s komentářema kde se každý skládá ze 2 znaků
     * @param vstupniSoubor soubor ke čtení
     */
     private List<String> vyberValidniData(String vstupniSoubor){
         // Levá a pravá závora znaku komentáře ve vstupním souboru
         final int LEVA_ZAVORA_PRVNI = 55357;
         final int LEVA_ZAVORA_DRUHY = 56362;
         final int PRAVA_ZAVORA_PRVNI = 55356;
         final int PRAVA_ZAVORA_DRUHY = 57308;

         // Čtený znak
         int readChar;
         int pocetVnoreni = 0;
         boolean bylPrvniZnakKomentare = false;
         boolean bylMinuleKonecKomentare = false;

         // Vytvořená slova
         StringBuilder builder = new StringBuilder();
         ArrayList<String> validniData = new ArrayList<>();

         try {
             FileReader fr = new FileReader(vstupniSoubor);
             BufferedReader br = new BufferedReader(fr);

             while ((readChar = br.read()) != -1) {
                 // Prvni cast znaku komentare
                 if(readChar == LEVA_ZAVORA_PRVNI || readChar == PRAVA_ZAVORA_PRVNI) {
                     bylPrvniZnakKomentare = true;
                     continue;
                 }

                 // Druha cast znaku komentare
                 if(bylPrvniZnakKomentare){
                     if(readChar == LEVA_ZAVORA_DRUHY){
                         pocetVnoreni += 1;
                     }
                     if(readChar == PRAVA_ZAVORA_DRUHY){
                         pocetVnoreni -= 1;
                         bylMinuleKonecKomentare = true;
                     }
                     bylPrvniZnakKomentare = false;
                     continue;
                 }

                 // Vyber data mimo komentare
                 if(pocetVnoreni == 0){
                     String znak = Character.toString(readChar);

                     // Dalsi znak je whitespace, zapis slovo
                     if(znak.matches("\\s+")){
                         builder = noveSlovo(builder, validniData);
                         bylMinuleKonecKomentare = false;
                         continue;
                     }

                     // Konec komentare byl tesne nalepeny na data
                     if(bylMinuleKonecKomentare){
                         builder = noveSlovo(builder, validniData);
                     }

                     // Pridej znak do slova
                     builder.append(znak);
                     bylMinuleKonecKomentare = false;
                 }
             }
             // Zapis posledni slovo jestli nejake je
             noveSlovo(builder, validniData);

             // Zavri soubor
             br.close();
             fr.close();
         } catch (IOException e) {
             System.out.println("\nNepovedlo se najit soubor.");
             System.exit(1);
         }
         return validniData;
     }

    /**
     * Zkontroluje jestli je možné začít psát nové slovo bez ztráty obsahu StringBuilder
     * @param sb builder který chceme připravit pro nové slovo
     * @param data kam se má uložit obsah builderu
     * @return prázdný StringBuilder
     */
     private StringBuilder noveSlovo(StringBuilder sb, List<String> data){
        if(!sb.isEmpty()){
            data.add(sb.toString());
            return new StringBuilder();
        }
        return sb;
     }

    /**
     * Vybere z validních dat sklady a načte je
     * @param validniData vyparsovaná data ze vstupního souboru
     */
     private void vytvorSklady(List<String> validniData){
         int posledniIndexSkladu = Integer.parseInt(validniData.get(index)) * 5;  // pocet skladu je nasoben poctem parametru pro vytvoreni objektu
         index++;

         for(posledniIndexSkladu = index + posledniIndexSkladu ; index < posledniIndexSkladu ; index += 5) {

             double x = Double.parseDouble(validniData.get(index));
             double y = Double.parseDouble(validniData.get(index + 1));
             int pocetKosu = Integer.parseInt(validniData.get(index + 2));
             double dobaDoplneniTs = Double.parseDouble(validniData.get(index + 3));
             double dobaDoplneniTn = Double.parseDouble(validniData.get(index + 4));

             Sklad sklad = new Sklad(new DoubleVector2D(x, y), pocetKosu, dobaDoplneniTs, dobaDoplneniTn);
             sklady.add(sklad);
         }
     }

    /**
     * Vybere z validních dat oázy a načte je
     * @param validniData vyparsovaná data ze vstupního souboru
     */
     private void vytvorOazy(List<String> validniData){
         int posledniIndexOazy = Integer.parseInt(validniData.get(index)) * 2;    // pocet oaz je nasoben poctem parametru pro vytvoreni objektu
         index++;

         for (posledniIndexOazy = index + posledniIndexOazy ; index < posledniIndexOazy ; index += 2){

             double x = Double.parseDouble(validniData.get(index));
             double y = Double.parseDouble(validniData.get(index + 1));

             Oaza oaza = new Oaza(new DoubleVector2D(x,y));
             oazy.add(oaza);
         }
     }

    /**
     * Vybere z validních dat cesty a načte je
     * @param validniData vyparsovaná data ze vstupního souboru
     */
     private void vytvorCesty(List<String> validniData){
         int posledniIndexCesty = Integer.parseInt(validniData.get(index)) * 2;   // pocet cest je nasoben poctem parametru pro vytvoreni objektu
         index++;

         for (posledniIndexCesty = index + posledniIndexCesty ; index < posledniIndexCesty ; index += 2){

             int zacatekCesty = Integer.parseInt(validniData.get(index));
             int konecCesty = Integer.parseInt(validniData.get(index + 1));

             CestaSymetricka cesta = new CestaSymetricka(mista.get(zacatekCesty), mista.get(konecCesty));
             cesty.add(cesta);
         }
     }

    /**
     * Vybere z validních dat velbloudy a načte je
     * @param validniData vyparsovaná data ze vstupního souboru
     */
     private void vytvorVelbloudy(List<String> validniData){
         int posledniIndexVelblouda = Integer.parseInt(validniData.get(index)) * 8;  // pocet velbloudu je nasoben poctem parametru pro vytvoreni objektu
         index++;

         for (posledniIndexVelblouda = index + posledniIndexVelblouda ; index < posledniIndexVelblouda ; index += 8){

             String nazev = validniData.get(index);
             double minRychlost = Double.parseDouble(validniData.get(index + 1));
             double maxRychlost = Double.parseDouble(validniData.get(index + 2));
             double minVzdalenost = Double.parseDouble(validniData.get(index + 3));
             double maxVzdalenost = Double.parseDouble(validniData.get(index + 4));
             double dobaPiti = Double.parseDouble(validniData.get(index + 5));
             int maxZatizeni = Integer.parseInt(validniData.get(index + 6));
             double procentualniPomerDruhu = Double.parseDouble(validniData.get(index + 7));

             VelbloudTyp velbloudTyp = new VelbloudTyp(nazev, minRychlost, maxRychlost, minVzdalenost, maxVzdalenost, dobaPiti, maxZatizeni, procentualniPomerDruhu);
             velbloudi.add(velbloudTyp);
         }
     }

    /**
     * Vybere z validních dat požadavky a načte je
     * @param validniData vyparsovaná data ze vstupního souboru
     */
     private void vytvorPozadavky(List<String> validniData){
         int posledniIndexPozadavku = Integer.parseInt(validniData.get(index)) * 4;   // pocet pozadavku je nasoben poctem parametru pro vytvoreni objektu
         index++;

         for (posledniIndexPozadavku = index + posledniIndexPozadavku ; index < posledniIndexPozadavku ; index += 4){

             double casPozadavku = Double.parseDouble(validniData.get(index));
             int indexOazy = Integer.parseInt(validniData.get(index + 1));
             int pozadavekKosu = Integer.parseInt(validniData.get(index + 2));
             double casDoruceni = Double.parseDouble(validniData.get(index + 3));

             AMisto oaza = getOazy().get(indexOazy - 1);

             Pozadavek pozadavek = new Pozadavek(casPozadavku, oaza, pozadavekKosu, casDoruceni);
             pozadavky.add(pozadavek);
         }
     }
}