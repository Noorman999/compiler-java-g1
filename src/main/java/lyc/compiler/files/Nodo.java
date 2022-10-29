package lyc.compiler.files;

public class Nodo {

        private String dato;
        private Nodo left;
        private Nodo right;

    public Nodo (String dato){
        this.dato = dato;
        left = null;
        right = null;
    }
    public Nodo (String dato, Nodo left, Nodo right){
        this.dato = dato;
        this.left = left;
        this.right = right;
    }
    public String getDato() {
        return dato;
    }

    public void setDato(String dato) {
        this.dato = dato;
    }

    public Nodo getLeft() {
        return left;
    }

    public void setLeft(Nodo left) {
        this.left = left;
    }

    public Nodo getRight() {
        return right;
    }

    public void setRight(Nodo right) {
        this.right = right;
    }
}
