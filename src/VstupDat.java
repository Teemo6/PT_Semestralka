import java.nio.file.*;
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
    private final String LEVA_ZAVORA = "\uD83D\uDC2A";
    private final String PRAVA_ZAVORA = "\uD83C\uDFDC";

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
    private void vyberValidniData(String vstupniSoubor) {
        validniData = new ArrayList<>();
        int pocetVnoreni = 0;

        try {
            Path filePath = Paths.get(vstupniSoubor);
            Scanner scanner = new Scanner(filePath);

            while (scanner.hasNext()) {
                String nextString = scanner.next();

                if(nextString.contains(LEVA_ZAVORA) || nextString.contains(PRAVA_ZAVORA)){
                    // Kontrola leve zavory bez mezery
                    // TODO
                    if(pocetVnoreni == 0 && nextString.contains(LEVA_ZAVORA)){
                        String[] splitString = nextString.split(LEVA_ZAVORA);
                        if(splitString.length > 1 && !splitString[0].equals(PRAVA_ZAVORA) && !splitString[0].equals("")){
                            validniData.add(nextString.split(LEVA_ZAVORA)[0]);
                        }
                    }

                    // Aktualizace vnoreni
                    int pocetLevychZavor = nextString.length() - nextString.replaceAll(LEVA_ZAVORA,"").length();
                    int pocetPravychZavor = nextString.length() - nextString.replaceAll(PRAVA_ZAVORA,"").length();
                    pocetVnoreni += (pocetLevychZavor - pocetPravychZavor);

                    // Kontrola prave zavory bez mezery
                    // TODO
                    if(pocetVnoreni == 0 && nextString.contains(PRAVA_ZAVORA)){
                        String[] splitString = nextString.split(PRAVA_ZAVORA);
                        if(splitString.length > 1){
                            validniData.add(nextString.split(PRAVA_ZAVORA)[nextString.split(PRAVA_ZAVORA).length - 1]);
                        }
                    }
                    continue;
                }

                if(pocetVnoreni == 0){
                    validniData.add(nextString);
                }
            }
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