import java.util.*;
import java.io.*;

/**
 * Instance třídy {@code VstupDat} představuje jedináčka, který umí přečíst vstupní soubor a vybrat potřebná data
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.13 30-10-2022
 */
public class VstupDat{
    /** Instance jedináčka VstupDat */
    private static final VstupDat INSTANCE = new VstupDat();

    /** Vytvořené objekty */
    private List<Sklad> sklady;
    private List<Oaza> oazy;
    private HashSet<Cesta> cesty;
    private List<VelbloudTyp> velbloudi;
    private List<Pozadavek> pozadavky;
    private HashMap<Integer, AMisto> mista;

    /**
     * Vrátí jedináčka
     * @return instance jedináčka
     */
    public static VstupDat getInstance(){
        return INSTANCE;
    }

    /**
     * Vybere ze souboru všechna data, která se nachází mimo komentáře (data jsou oddělena jakýmkoliv bílým znakem)
     * data uloží jako pole Stringů
     * @param vstupniSoubor soubor ke čtení
     */
     private ArrayList<String> vyberValidniData(String vstupniSoubor){
         // Levá a pravá závora znaku komentáře ve vstupním souboru
         int LEVA_ZAVORA_PRVNI = 55357;
         int LEVA_ZAVORA_DRUHY = 56362;
         int PRAVA_ZAVORA_PRVNI = 55356;
         int PRAVA_ZAVORA_DRUHY = 57308;

         ArrayList<String> validniData = new ArrayList<>();
         StringBuilder builder = new StringBuilder();

         int pocetVnoreni = 0;
         boolean bylMinuleKomentar = false;
         boolean bylMinuleKonecKomentare = false;

         int readChar;

         try {
             FileReader fr = new FileReader(vstupniSoubor);
             BufferedReader br = new BufferedReader(fr);

             while ((readChar = br.read()) != -1) {
                 // Leva zavora komentare
                 if(readChar == LEVA_ZAVORA_PRVNI) {
                     bylMinuleKomentar = true;
                     continue;
                 }
                 if(readChar == LEVA_ZAVORA_DRUHY && bylMinuleKomentar) {
                     bylMinuleKomentar = false;
                     pocetVnoreni += 1;
                     continue;
                 }

                 // Prava zavora komentare
                 if(readChar == PRAVA_ZAVORA_PRVNI) {
                     bylMinuleKomentar = true;
                     continue;
                 }
                 if(readChar == PRAVA_ZAVORA_DRUHY && bylMinuleKomentar) {
                     bylMinuleKomentar = false;
                     bylMinuleKonecKomentare = true;
                     pocetVnoreni -= 1;
                     continue;
                 }

                 // Data mimo komentare
                 if(pocetVnoreni == 0){
                     String znak = Character.toString(readChar);
                     if(znak.matches("\\s+")){
                         if(builder.length() != 0) {
                             validniData.add(builder.toString());
                             builder = new StringBuilder();
                         }
                         continue;
                     }
                     if(bylMinuleKonecKomentare && builder.length() != 0){
                         validniData.add(builder.toString());
                         builder = new StringBuilder();
                     }
                     builder.append(znak);
                 }
                 bylMinuleKonecKomentare = false;
             }

             // Zapis posledni slovo
             if(builder.length() != 0) {
                 validniData.add(builder.toString());
             }
             br.close();
         } catch (IOException e) {
             e.printStackTrace();
         }
         return validniData;
     }

    /**
     * Z validních dat načte parametry pro strukturu programu
     * @param vstupniSoubor soubor ke čtení, předává se privátní metodě vyberValidniData()
     */
    public void vytvorObjekty(String vstupniSoubor){
        ArrayList<String> validniData = vyberValidniData(vstupniSoubor);

        sklady = new ArrayList<>();
        oazy = new ArrayList<>();
        cesty = new HashSet<>();
        velbloudi = new ArrayList<>();
        pozadavky = new ArrayList<>();
        mista = new HashMap<>();

        int posledniIndexSkladu;
        int posledniIndexOazy;
        int posledniIndexCesty;
        int posledniIndexVelblouda;
        int posledniIndexPozadavku;

        int index = 0;

        /*  ----------------------  */
        /*  Vytvoreni vsech skladu  */
        /*  ----------------------  */

        posledniIndexSkladu = Integer.parseInt(validniData.get(index)) * 5;  // pocest skladu je nasoben poctem parametru pro vytvoreni objektu
        index++;

        for(posledniIndexSkladu = index + posledniIndexSkladu ; index < posledniIndexSkladu ; index += 5) {

            double x = Double.parseDouble(validniData.get(index));
            double y = Double.parseDouble(validniData.get(index + 1));
            int pocetKosu = Integer.parseInt(validniData.get(index + 2));
            int dobaDoplneniTs = Integer.parseInt(validniData.get(index + 3));
            int dobaDoplneniTn = Integer.parseInt(validniData.get(index + 4));

            Sklad sklad = new Sklad(new DoubleVector2D(x, y), pocetKosu, dobaDoplneniTs, dobaDoplneniTn);
            sklady.add(sklad);
        }

        /*  -------------------  */
        /*  Vytvoreni vsech oaz  */
        /*  -------------------- */

        posledniIndexOazy = Integer.parseInt(validniData.get(index)) * 2;    // pocest oaz je nasoben poctem parametru pro vytvoreni objektu
        index++;

        for (posledniIndexOazy = index + posledniIndexOazy ; index < posledniIndexOazy ; index += 2){

            double x = Double.parseDouble(validniData.get(index));
            double y = Double.parseDouble(validniData.get(index + 1));

            Oaza oaza = new Oaza(new DoubleVector2D(x,y));
            oazy.add(oaza);
        }

        // Napln HashMap mista
        sklady.forEach(s -> mista.put(s.getID(), s));
        oazy.forEach(o -> mista.put(o.getID(), o));

        /*  --------------------  */
        /*  Vytvoreni vsech cest  */
        /*  --------------------  */

        posledniIndexCesty = Integer.parseInt(validniData.get(index)) * 2;   // pocest cest je nasoben poctem parametru pro vytvoreni objektu
        index++;

        for (posledniIndexCesty = index + posledniIndexCesty ; index < posledniIndexCesty ; index += 2){

            int zacatekCesty = Integer.parseInt(validniData.get(index));
            int konecCesty = Integer.parseInt(validniData.get(index + 1));

            Cesta cesta = new Cesta(mista.get(zacatekCesty), mista.get(konecCesty));
            cesty.add(cesta);
        }

        /*  -------------------------  */
        /*  Vytvoreni vsech velbloudu  */
        /*  -------------------------  */

        posledniIndexVelblouda = Integer.parseInt(validniData.get(index)) * 8;  // pocest velbloudu je nasoben poctem parametru pro vytvoreni objektu
        index++;

        for (posledniIndexVelblouda = index + posledniIndexVelblouda ; index < posledniIndexVelblouda ; index += 8){

            String nazev = validniData.get(index);
            double minRychlost = Double.parseDouble(validniData.get(index + 1));
            double maxRychlost = Double.parseDouble(validniData.get(index + 2));
            double minVzdalenost = Double.parseDouble(validniData.get(index + 3));
            double maxVzdalenost = Double.parseDouble(validniData.get(index + 4));
            int dobaPiti = Integer.parseInt(validniData.get(index + 5));
            int maxZatizeni = Integer.parseInt(validniData.get(index + 6));
            double procentualniPomerDruhu = Double.parseDouble(validniData.get(index + 7));

            VelbloudTyp velbloudTyp = new VelbloudTyp(nazev, minRychlost, maxRychlost, minVzdalenost, maxVzdalenost, dobaPiti, maxZatizeni, procentualniPomerDruhu);
            velbloudi.add(velbloudTyp);
        }

        /*  -------------------------  */
        /*  Vytvoreni vsech pozadavku  */
        /*  -------------------------  */

        posledniIndexPozadavku = Integer.parseInt(validniData.get(index)) * 4;   // pocest pozadavku je nasoben poctem parametru pro vytvoreni objektu
        index++;

        for (posledniIndexPozadavku = index + posledniIndexPozadavku ; index < posledniIndexPozadavku ; index += 4){

            double casPozadavku = Double.parseDouble(validniData.get(index));
            int indexOazy = Integer.parseInt(validniData.get(index + 1));
            int pozadavekKosu = Integer.parseInt(validniData.get(index + 2));
            int casDoruceni = Integer.parseInt(validniData.get(index + 3));

            Pozadavek pozadavek = new Pozadavek(casPozadavku, indexOazy, pozadavekKosu, casDoruceni);
            pozadavky.add(pozadavek);
        }
/*
        sklady.forEach(System.out::println);
        System.out.println();
        oazy.forEach(System.out::println);
        System.out.println();
        cesty.forEach(System.out::println);
        System.out.println();
        velbloudi.forEach(System.out::println);
        System.out.println();
        pozadavky.forEach(System.out::println);
        System.out.println();

        for (Integer ID : mista.keySet()) {
            String key = ID.toString();
            String value = mista.get(ID).toString();
            System.out.println(key + " " + value);
        }
*/

     }

    public List<Sklad> getSklady() {
        return sklady;
    }

    public List<Oaza> getOazy() {
        return oazy;
    }

    public HashSet<Cesta> getCesty() {
        return cesty;
    }

    public List<VelbloudTyp> getVelbloudi() {
        return velbloudi;
    }

    public List<Pozadavek> getPozadavky() {
        return pozadavky;
    }

    public Map<Integer, AMisto> getMista() {
        return mista;
    }
}