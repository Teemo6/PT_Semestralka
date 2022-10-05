public abstract class AMisto {
    protected IntVector2D pozice;

    public IntVector2D getPozice(){
        return this.pozice;
    }

    @Override
    public String toString() {
        return "AMisto{" +
                "pozice=" + pozice +
                '}';
    }
}
