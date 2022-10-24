package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;

public class IntermediateCodeGenerator implements FileGenerator {

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        fileWriter.write("TODO");
    }

    Nodo crearNodo(String dato, Nodo left, Nodo right) {
        Nodo nodo= new Nodo();
        nodo.setDato(dato);
        nodo.setLeft(left);
        nodo.setRight(right);
        // deberia agregar algun llamado al generate para agregar el nodo nuevo al txt o donde sea.
        return nodo;
    }

    Nodo crearHoja(String dato) {
        Nodo nodo=new Nodo();
        nodo.setDato(dato);
        nodo.setLeft(null);
        nodo.setRight(null);
        // deberia agregar algun llamado al generate para agregar la hoja nueva al txt o donde sea.
        return nodo;
    }
}
