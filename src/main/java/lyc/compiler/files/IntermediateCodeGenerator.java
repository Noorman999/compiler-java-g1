package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import lyc.compiler.files.IntermediateCodeNodo;

public class IntermediateCodeGenerator implements FileGenerator {

    private static IntermediateCodeGenerator intermedia;
    HashMap<String, IntermediateCodeNodo> register;

    private Stack<String> pila;

    private IntermediateCodeGenerator() {
        this.register = new HashMap<String,IntermediateCodeNodo>();
        this.pila = new Stack<String>();
    }

    public static IntermediateCodeGenerator getInstance() {
        if(intermedia == null) {
            intermedia = new IntermediateCodeGenerator();
        }
        return intermedia;
    }
    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        fileWriter.write(this.intermedia.recorrer());
    }

    public void agregarHoja(String hojaNueva,String dato) {
        this.register.put(hojaNueva,new IntermediateCodeNodo(dato));
    }

    public void agregarNodo(String aux,String dato,String izquierda, String derecha) {
        IntermediateCodeNodo izq = register.get(izquierda);
        IntermediateCodeNodo der = register.get(derecha);
        this.register.put(aux,new IntermediateCodeNodo(dato, izq, der));
    }

    public void asignarPuntero(String izquierda,String derecha) {
        IntermediateCodeNodo der = this.register.get(derecha);
        this.register.put(izquierda,der);
    }

    public String recorrer(){

        IntermediateCodeNodo raiz = this.register.get(Puntero.p_program);

        return this.recorrerR(raiz);
    }

    private String recorrerR(IntermediateCodeNodo nodo) {

        if(nodo.left == null && nodo.right == null)
            return nodo.dato + "";

        String resParcial = "";

        resParcial += nodo.left != null ? recorrerR(nodo.left) : "";
        resParcial += nodo.dato + " ";
        return resParcial + (nodo.right != null ? recorrerR(nodo.right) : "");
    }

    public void apilar(String aux){
        this.pila.push(aux);
    }

    public String desapilar(){
        return this.pila.pop();
    }
}
