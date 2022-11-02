import java.util.ArrayList;

public class VelbloudPozadavek {
    private Pozadavek pozadavek;
    private ArrayList<Cesta> cestaPoCastech;

    private double celkovaVzdalenostCesty;
    private int splnenoKosu;
    private final int pocetPotrebnychKosu;

    public VelbloudPozadavek(Pozadavek po, ArrayList<Cesta> ce){
        pozadavek = po;
        cestaPoCastech = ce;

        celkovaVzdalenostCesty = 0;
        for(Cesta c : ce){
            celkovaVzdalenostCesty += c.getVzdalenost();
        }
        splnenoKosu = 0;
        pocetPotrebnychKosu = po.getPozadavekKosu();
    }

    public boolean zkontrolujSplnenyPozadavek(){
        if(pozadavek.getPozadavekKosu() <= splnenoKosu){
            pozadavek.setJeSplnen(true);
            return true;
        }
        return false;
    }

    public Pozadavek getPozadavek() {
        return pozadavek;
    }

    public double getCelkovaVzdalenostCesty() {
        return celkovaVzdalenostCesty;
    }

    public void setPozadavek(Pozadavek pozadavek) {
        this.pozadavek = pozadavek;
    }

    public ArrayList<Cesta> getCestaPoCastech() {
        return cestaPoCastech;
    }

    public void setCestaPoCastech(ArrayList<Cesta> cestaPoCastech) {
        this.cestaPoCastech = cestaPoCastech;
    }

    public void doruceneKose(int pocetDorucenychKosu){
        splnenoKosu += pocetDorucenychKosu;
    }

    public int getPocetPotrebnychKosu(){return pocetPotrebnychKosu;}
}
