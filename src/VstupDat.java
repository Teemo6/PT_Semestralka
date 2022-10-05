import java.util.*;
import java.io.*;

/**
 * Instance třídy {@code VstupDat} představuje jedináčka, který umí přečíst vstupní soubor a vybrat potřebná data
 * @author Štěpán Faragula, Mikuláš Mach 05-10-2022
 * @version 1.02
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
        validniData.forEach(System.out::println);

        sklady = new ArrayList<>();
        oazy = new ArrayList<>();
        cesty = new ArrayList<>();
        velbloudi = new ArrayList<>();
        pozadavky = new ArrayList<>();
     }
}