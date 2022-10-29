package lyc.compiler.files;

public class IntermediateCodeNodo {

     String dato;
     IntermediateCodeNodo left;
     IntermediateCodeNodo right;

    public IntermediateCodeNodo(String dato){
        this.dato = dato;
        this.left = null;
        this.right = null;
    }
    public IntermediateCodeNodo(String dato, IntermediateCodeNodo left, IntermediateCodeNodo right){
        this.dato = dato;
        this.left = left;
        this.right = right;
    }
//    public String getDato() {
//        return dato;
//    }
//
//    public void setDato(String dato) {
//        this.dato = dato;
//    }
//
//    public IntermediateCodeNodo getLeft() {
//        return left;
//    }
//
//    public void setLeft(IntermediateCodeNodo left) {
//        this.left = left;
//    }
//
//    public IntermediateCodeNodo getRight() {
//        return right;
//    }
//
//    public void setRight(IntermediateCodeNodo right) {
//        this.right = right;
//    }
}
