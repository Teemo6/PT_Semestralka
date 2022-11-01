import java.util.ArrayList;

public class VelbloudPozadavek {
    private Pozadavek pozadavek;
    private ArrayList<Cesta> cestaPoCastech;
    private int splnenoKosu;
    private final int pocetPotrebnychKosu;

    public VelbloudPozadavek(Pozadavek p, ArrayList<Cesta> c){
        pozadavek = p;
        cestaPoCastech = c;

        splnenoKosu = 0;
        pocetPotrebnychKosu = p.getPozadavekKosu();
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
