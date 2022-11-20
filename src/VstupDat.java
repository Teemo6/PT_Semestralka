import java.util.*;
import java.io.*;

/**
 * Instance třídy {@code VstupDat} představuje jedináčka, který umí přečíst vstupní soubor a vybrat potřebná data
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.28 20-11-2022
 */
public class VstupDat{
    /** Instance jedináčka VstupDat */
    private static final VstupDat INSTANCE = new VstupDat();

    /** Znaky komentáře v parseru */
    private final int LEVA_ZAVORA_PRVNI = 55357;
    private final int LEVA_ZAVORA_DRUHY = 56362;
    private final int PRAVA_ZAVORA_PRVNI = 55356;
    private final int PRAVA_ZAVORA_DRUHY = 57308;

    /** Vytvořené objekty */
    private List<Sklad> sklady;
    private List<Oaza> oazy;
    private List<Cesta> cesty;
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
        cesty = new ArrayList<>();
        velbloudi = new ArrayList<>();
        pozadavky = new ArrayList<>();
        mista = new HashMap<>();
        index = 0;

        try {
            vytvorSklady(validniData);
            vytvorOazy(validniData);
            vytvorCesty(validniData);
            vytvorVelbloudy(validniData);
            vytvorPozadavky(validniData);
        } catch (Exception e){
            System.out.println("\nSoubor nema pozadovanou strukturu.");
            System.exit(1);
        }
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
    public List<Cesta> getCesty() {
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

    //////////////////////
    //*     Parser     *//
    //////////////////////

    /**
     * Vybere ze souboru všechna data, která se nachází mimo komentáře (data jsou oddělena jakýmkoliv bílým znakem)
     * data uloží jako dynamické pole Stringů
     * pracujeme s komentářema kde se každý skládá ze 2 znaků
     * @param vstupniSoubor soubor ke čtení
     */
     private List<String> vyberValidniData(String vstupniSoubor){
         // Čtený soubor
         BufferedReader br = null;

         // Vyparsovaná data
         List<String> validniData = new ArrayList<>();

         try {
             br = new BufferedReader(new FileReader(vstupniSoubor));
             validniData = vyparsujData(br);
         } catch (IOException e) {
             System.out.println("\nNepovedlo se najit soubor.");
             System.exit(1);
         } finally {
             if(br != null){
                 try{
                    br.close();
                 } catch (IOException e){
                     System.out.println("\nNepovedlo se precist soubor.");
                     System.exit(1);
                 }
             }
         }
         return validniData;
     }

    /**
     * Z předaného BufferedReaderu vyparsuje data a vrátí jejich seznam bez zakomentovaných částí
     * @param br buffered reader
     * @return seznam dat mimo komentáře
     * @throws IOException hodí výjimku pokud se nepodaří přečíst soubor
     */
     private List<String> vyparsujData(BufferedReader br) throws IOException{
         // Čtený znak
         int readChar;
         int pocetVnoreni = 0;
         int zmenaVnoreni;
         boolean bylPrvniZnakKomentare = false;
         boolean bylMinuleKonecKomentare = false;

         // Vytvořená slova
         StringBuilder builder = new StringBuilder();
         ArrayList<String> validniData = new ArrayList<>();

         while ((readChar = br.read()) != -1) {
             // První část znaku komentáře
             if(prvniZnakKomentare(readChar)){
                 bylPrvniZnakKomentare = true;
                 continue;
             }

             // Druhá část znaku komentáře
             if(bylPrvniZnakKomentare){
                 zmenaVnoreni = zmenaVnoreni(readChar);
                 pocetVnoreni += zmenaVnoreni;

                 if(zmenaVnoreni == -1){
                     bylMinuleKonecKomentare = true;
                 }
                 bylPrvniZnakKomentare = false;
                 continue;
             }

             // Vyber data mimo komentáře
             if(pocetVnoreni == 0){
                 String znak = Character.toString(readChar);

                 // Další znak je whitespace, zapiš uložené slovo
                 if(znak.matches("\\s+")){
                     builder = noveSlovo(builder, validniData);
                     bylMinuleKonecKomentare = false;
                     continue;
                 }

                 // Konec komentáře byl těsně nalepený na data
                 if(bylMinuleKonecKomentare){
                     builder = noveSlovo(builder, validniData);
                 }

                 // Přidej znak do slova
                 builder.append(znak);
                 bylMinuleKonecKomentare = false;
             }
         }
         // Zapiš poslední slovo, jestli nějaké je
         noveSlovo(builder, validniData);
         return validniData;
     }

    /**
     * Vrátí jestli se znak rovná hodnotě prvního znaku komentáře levé nebo pravé závory
     * @param readChar znak na porovnání
     * @return true pokud je shoda znaku
     */
     private boolean prvniZnakKomentare(int readChar){
        return readChar == LEVA_ZAVORA_PRVNI || readChar == PRAVA_ZAVORA_PRVNI;
     }

    /**
     * Vrátí jestli se má změnit vnoření v hierarchii komentářů
     * @param readChar znak na porovnání
     * @return 1 pokud se má ponořit hlouběji
     *        -1 pokud se má vynořit
     *         0 pokud nemá změnit vnoření
     */
    private int zmenaVnoreni(int readChar){
        if(readChar == LEVA_ZAVORA_DRUHY){
            return 1;
        }
        if(readChar == PRAVA_ZAVORA_DRUHY){
            return -1;
        }
        return 0;
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

    ///////////////////////
    //* Tvoreni objektu *//
    ///////////////////////

    /**
     * Vybere z validních dat sklady a načte je
     * @param validniData vyparsovaná data ze vstupního souboru
     */
     private void vytvorSklady(List<String> validniData) throws RuntimeException{
         int posledniIndexSkladu = Integer.parseInt(validniData.get(index)) * 5;  // pocet skladu je nasoben poctem parametru pro vytvoreni objektu
         index++;

         for(posledniIndexSkladu = index + posledniIndexSkladu ; index < posledniIndexSkladu ; index += 5) {

             double x = Double.parseDouble(validniData.get(index));
             double y = Double.parseDouble(validniData.get(index + 1));
             int pocetKosu = Integer.parseInt(validniData.get(index + 2));
             double dobaDoplneniTs = Double.parseDouble(validniData.get(index + 3));
             double dobaDoplneniTn = Double.parseDouble(validniData.get(index + 4));

             if(dobaDoplneniTn < 0 || dobaDoplneniTs < 0){
                 throw new RuntimeException("Cas nemuze byt zaporny");
             }

             Sklad sklad = new Sklad(new DoubleVector2D(x, y), pocetKosu, dobaDoplneniTs, dobaDoplneniTn);
             sklady.add(sklad);
             mista.put(sklad.getID(), sklad);
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
             mista.put(oaza.getID(), oaza);
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

             Cesta cesta = new Cesta(mista.get(zacatekCesty), mista.get(konecCesty));
             cesty.add(cesta);
         }
     }

    /**
     * Vybere z validních dat velbloudy a načte je
     * @param validniData vyparsovaná data ze vstupního souboru
     */
     private void vytvorVelbloudy(List<String> validniData) throws RuntimeException{
         // Kontrola pomeru
         double sumaPomeru = 0;
         double epsilon = 0.00000000001;

         int posledniIndexVelblouda = Integer.parseInt(validniData.get(index)) * 8;  // pocet velbloudu je nasoben poctem parametru pro vytvoreni objektu
         index++;

         for (posledniIndexVelblouda = index + posledniIndexVelblouda ; index < posledniIndexVelblouda ; index += 8){

             String nazev = validniData.get(index);
             double minRychlost = Double.parseDouble(validniData.get(index + 1));
             double maxRychlost = Double.parseDouble(validniData.get(index + 2));
             double minVzdalenost = Double.parseDouble(validniData.get(index + 3));
             double maxVzdalenost = Double.parseDouble(validniData.get(index + 4));
             double dobaPiti = Math.abs(Double.parseDouble(validniData.get(index + 5)));
             int maxZatizeni = Math.abs(Integer.parseInt(validniData.get(index + 6)));
             double procentualniPomerDruhu = Double.parseDouble(validniData.get(index + 7));

             // Spatne uvedena rychlost/vzdalenost
             if(minRychlost > maxRychlost || minVzdalenost > maxVzdalenost){
                 throw new RuntimeException("Minimum nemuze byt vetsi nez maximum");
             }
             sumaPomeru += procentualniPomerDruhu;

             VelbloudTyp velbloudTyp = new VelbloudTyp(nazev, minRychlost, maxRychlost, minVzdalenost, maxVzdalenost, dobaPiti, maxZatizeni, procentualniPomerDruhu);
             velbloudi.add(velbloudTyp);
         }

         // Soucet vsech typu != 1
         if(Math.abs(sumaPomeru - 1) > epsilon){
            throw new RuntimeException("Soucet typu velblouda neni 1");
         }
     }

    /**
     * Vybere z validních dat požadavky a načte je
     * @param validniData vyparsovaná data ze vstupního souboru
     */
     private void vytvorPozadavky(List<String> validniData) throws RuntimeException{
         int posledniIndexPozadavku = Integer.parseInt(validniData.get(index)) * 4;   // pocet pozadavku je nasoben poctem parametru pro vytvoreni objektu
         index++;

         for (posledniIndexPozadavku = index + posledniIndexPozadavku ; index < posledniIndexPozadavku ; index += 4){

             double casPozadavku = Double.parseDouble(validniData.get(index));
             int indexOazy = Integer.parseInt(validniData.get(index + 1));
             int pozadavekKosu = Integer.parseInt(validniData.get(index + 2));
             double casDoruceni = Double.parseDouble(validniData.get(index + 3));

             // Cas nemuze byt zaporny
             if(casPozadavku < 0 || pozadavekKosu < 0 || casDoruceni < 0){
                 throw new RuntimeException("Cas nemuze byt zaporny");
             }

             AMisto oaza = getOazy().get(indexOazy - 1);
             Pozadavek pozadavek = new Pozadavek(casPozadavku, oaza, pozadavekKosu, casDoruceni);
             pozadavky.add(pozadavek);
         }

         // Nevalidni soubor
         if(validniData.size() > index + 3){
             throw new RuntimeException("Spatny pocet pozadavku");
         }
     }
}