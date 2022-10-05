import java.util.*;
import java.io.*;

/**
 * Instance třídy {@code VstupDat} představuje jedináčka, který umí přečíst vstupní soubor a vybrat potřebná data
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.04 05-10-2022
 */
public class VstupDat{
    /** Instance jedináčka VstupDat */
    private static final VstupDat INSTANCE = new VstupDat();

    /** Levá a pravá závora komentáře */
    private final int LEVA_ZAVORA_PRVNI = 55357;
    private final int LEVA_ZAVORA_DRUHY = 56362;
    private final int PRAVA_ZAVORA_PRVNI = 55356;
    private final int PRAVA_ZAVORA_DRUHY = 57308;

    /** Načtená data */
    private List<String> validniData;

    /** Vytvořené objekty */
    private List<Sklad> sklady;
    private List<Oaza> oazy;
    private List<Cesta> cesty;
    private List<Velbloud> velbloudi;
    private List<Pozadavek> pozadavky;
    private List<AMisto> misto;

    /**
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
     private void vyberValidniData(String vstupniSoubor){
         validniData = new ArrayList<>();
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
             if(builder.length() != 0) {
                 validniData.add(builder.toString());
             }
             br.close();
         } catch (IOException e) {
             e.printStackTrace();
         }
     }

    public void vytvorObjekty(String vstupniSoubor){
        vyberValidniData(vstupniSoubor);
        //validniData.forEach(System.out::println);
        sklady = new ArrayList<>();
        oazy = new ArrayList<>();
        cesty = new ArrayList<>();
        velbloudi = new ArrayList<>();
        pozadavky = new ArrayList<>();
        misto = new ArrayList<>();

        int posledniIndexSkladu;
        int posledniIndexOazy;
        int posledniIndexCesty;
        int posledniIndexVeldblouda;
        int posledniIndexPozadavku;

        int iterator = 0;

        /*  ----------------------  */
        /*  Vytvoreni vsech skladu  */
        /*  ----------------------  */

        posledniIndexSkladu = Integer.parseInt(validniData.get(iterator)) * 5;  // pocest skladu je nasoben poctem parametru pro vytvoreni objektu
        iterator++;

        for(posledniIndexSkladu = iterator + posledniIndexSkladu ; iterator < posledniIndexSkladu;) {

            int x = Integer.parseInt(validniData.get(iterator));
            iterator++;
            int y = Integer.parseInt(validniData.get(iterator));
            iterator++;
            int pocetKosu = Integer.parseInt(validniData.get(iterator));
            iterator++;
            int dobaDoplneniTs = Integer.parseInt(validniData.get(iterator));
            iterator++;
            int dobaDoplneniTn = Integer.parseInt(validniData.get(iterator));
            iterator++;

            Sklad sklad = new Sklad(new IntVector2D(x, y), pocetKosu, dobaDoplneniTs, dobaDoplneniTn);
            sklady.add(sklad);
        }

        /*  -------------------  */
        /*  Vytvoreni vsech oaz  */
        /*  -------------------- */

        posledniIndexOazy = Integer.parseInt(validniData.get(iterator)) * 2;    // pocest oaz je nasoben poctem parametru pro vytvoreni objektu
        iterator++;


        for (posledniIndexOazy = iterator + posledniIndexOazy ; iterator < posledniIndexOazy;){
            int x = Integer.parseInt(validniData.get(iterator));
            iterator++;
            int y = Integer.parseInt(validniData.get(iterator));
            iterator++;

            Oaza oaza = new Oaza(new IntVector2D(x,y));
            oazy.add(oaza);

        }

        misto.addAll(sklady);
        misto.addAll(oazy);

        /*  --------------------  */
        /*  Vytvoreni vsech cest  */
        /*  --------------------  */

        posledniIndexCesty = Integer.parseInt(validniData.get(iterator)) * 2;   // pocest cest je nasoben poctem parametru pro vytvoreni objektu
        iterator++;

        for (posledniIndexCesty = iterator + posledniIndexCesty ; iterator < posledniIndexCesty;){
            int zacatekCesty = Integer.parseInt(validniData.get(iterator)) -1;  //-1 protoze sklady a oazy jsou cislovany od 1 a ne od 0
            iterator++;
            int konecCesty = Integer.parseInt(validniData.get(iterator)) -1;
            iterator++;

            Cesta cesta = new Cesta(misto.get(zacatekCesty), misto.get(konecCesty));
            cesty.add(cesta);

        }

        /*  -------------------------  */
        /*  Vytvoreni vsech velbloudu  */
        /*  -------------------------  */

        posledniIndexVeldblouda = Integer.parseInt(validniData.get(iterator)) * 8;  // pocest velbloudu je nasoben poctem parametru pro vytvoreni objektu
        iterator++;

        for (posledniIndexVeldblouda = iterator + posledniIndexVeldblouda ; iterator < posledniIndexVeldblouda;){
            String nazev = validniData.get(iterator);
            iterator++;
            int minRychlost = Integer.parseInt(validniData.get(iterator));
            iterator++;
            int maxRychlost = Integer.parseInt(validniData.get(iterator));
            iterator++;
            int minVzdalenost = Integer.parseInt(validniData.get(iterator));
            iterator++;
            int maxVzdalenost = Integer.parseInt(validniData.get(iterator));
            iterator++;
            int dobaPiti = Integer.parseInt(validniData.get(iterator));
            iterator++;
            int maxZatizeni = Integer.parseInt(validniData.get(iterator));
            iterator++;
            double procentualniPomerDruhu = Double.parseDouble(validniData.get(iterator));
            iterator++;

            Velbloud velbloud = new Velbloud(nazev, minRychlost, maxRychlost, minVzdalenost, maxVzdalenost, dobaPiti, maxZatizeni, procentualniPomerDruhu);
            velbloudi.add(velbloud);

        }

        /*  -------------------------  */
        /*  Vytvoreni vsech pozadavku  */
        /*  -------------------------  */

        posledniIndexPozadavku = Integer.parseInt(validniData.get(iterator)) * 4;   // pocest pozadavku je nasoben poctem parametru pro vytvoreni objektu
        iterator++;

        for (posledniIndexPozadavku = iterator + posledniIndexPozadavku ; iterator < posledniIndexPozadavku;){
            int casPozadavku = Integer.parseInt(validniData.get(iterator));
            iterator++;
            int indexOazy = Integer.parseInt(validniData.get(iterator));
            iterator++;
            int pozadavekKosu = Integer.parseInt(validniData.get(iterator));
            iterator++;
            int casDoruceni = Integer.parseInt(validniData.get(iterator));
            iterator++;

            Pozadavek pozadavek = new Pozadavek(casPozadavku, indexOazy, pozadavekKosu, casDoruceni);
            pozadavky.add(pozadavek);

        }
    }
}