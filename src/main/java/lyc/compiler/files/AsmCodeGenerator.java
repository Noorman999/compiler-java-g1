package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class AsmCodeGenerator implements FileGenerator {

    //PARA QUE NO FALLE main/compiler
    private static AsmCodeGenerator asm;

    AsmCodeGenerator() {
    }

    //PARA QUE NO FALLE main/compiler
    public static AsmCodeGenerator getInstance() {
        if(asm == null) {
            asm = new AsmCodeGenerator();
        }
        return asm;
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        setHeaders(fileWriter);
        setData(fileWriter);
        setCode(fileWriter);
        generarCodigo(fileWriter);
        finishCode(fileWriter);
    }
    private void setHeaders(FileWriter fileWriter) throws IOException {
        fileWriter.write(".MODEL LARGE ;Modelo de Memoria\n.386 ;Tipo de Procesador\n.STACK 200h ;Bytes en el Stack\n");
    }
    private void setData(FileWriter fileWriter) throws IOException {
        fileWriter.write("\n.DATA\n");
        SymbolTableGenerator ts =  SymbolTableGenerator.getInstance();
        ArrayList<SymbolTableToken> variables = ts.getTs();
        variables.forEach((var) -> {
            try {
                String value = "?";
                String dataType = "";
                if(var.getDataType() == "Int") {
                    dataType = "; cte entera";
                    value = var.getValue() == "" ? "?" : var.getValue();
                } else if (var.getDataType() == "Float") {
                    dataType = "; cte real";
                    value = var.getValue() == "" ? "?" : var.getValue();
                    value = value.startsWith(".") ? "0"+value : value;
                } else {
                    dataType = "; cte variable";
                }
                fileWriter.write(String.format("%-40s%-10s%-10s%-10s\n",var.getName(),"dd",value,dataType));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    private void setCode(FileWriter fileWriter) throws IOException {
        fileWriter.write("\n\n.CODE\nmov AX, @DATA ; inicializa el segmento de datos\nmov DS,AX\nmov es,ax;\n\n");
    }

    public String recorrer(IntermediateCodeGenerator gci) {

        IntermediateCodeNodo raiz = gci.register.get(Puntero.p_program);
        return this.recorrer2(raiz);
    }
    private int flag = 0;
    private int flagWhile = 0;
    private String recorrer2(IntermediateCodeNodo nodo) {
        String resParcial = "";
        if(nodo.left == null && nodo.right == null) {
            return cargar(nodo.dato);
        }
        if(nodo.dato == "=") {
            resParcial += nodoIgual(nodo);
        } else {
            if(nodo.dato == " while") {
                resParcial += "inicio_while:\n";
                flagWhile = 1;
            }
            resParcial += nodo.left != null ? recorrer2(nodo.left) : "";
            resParcial += traducir(nodo.dato);
            resParcial += nodo.right != null ? recorrer2(nodo.right) : "";
        }

      //  if(flagWhile == 1) {
       //     flagWhile = 0;
       //     resParcial += "jmp inicio_while:\nfin_while:\n";
        //}
        //(nodo.dato == "if") {
        //    resParcial += nodoIf(nodo);
        //}


        return resParcial;
    }

    private String nodoIf(IntermediateCodeNodo nodo) {
        String resParcial = "";
        if(nodo.dato == "if") {
            resParcial += recorrer2(nodo.left);
        }
        return resParcial;
    }

    private String nodoIgual(IntermediateCodeNodo nodo) {
        String resParcial = "";
        if(nodo.dato == "=") {
            resParcial += recorrer2(nodo.right);
            flag = 1;
            resParcial += recorrer2(nodo.left);
        } else {
            resParcial += recorrer2(nodo.left);
            resParcial += recorrer2(nodo.right);
            resParcial += traducir(nodo.dato);
        }
        return resParcial;
    }

    private String cargar(String dato) {
        //logica para subir CTE o ID al coprocesador
        if (flag == 1) {
            flag = 0;
            return "FSTP "+dato +"\n";
        }
        if(flag == 2 && flagWhile == 0) {
            flag = 0;
            return dato+"\n"+"fstsw ax\nsahf\njna else_part\ntrue_part:\n";
        }
        if(flag == 3 && flagWhile == 0) {
            flag = 0;
            return dato+"\n"+"fstsw ax\nsahf\njae else_part\ntrue_part:\n";
        }
        if(flag == 4 && flagWhile == 0) {
            flag = 0;
            return dato+"\n"+"fstsw ax\nsahf\njb else_part\ntrue_part:\n";
        }
        if(flag == 5 && flagWhile == 0) {
            flag = 0;
            return dato+"\n"+"fstsw ax\nsahf\nja else_part\ntrue_part:\n";
        }
        if(flag == 2 && flagWhile == 1) {
            flag = 0;
            return dato+"\n"+"fstsw ax\nsahf\njna fin_while:\n";
        }
        if(flag == 3 && flagWhile == 1) {
            flag = 0;
            return dato+"\n"+"fstsw ax\nsahf\njae fin_while:\n";
        }
        if(flag == 4 && flagWhile == 1) {
            flag = 0;
            return dato+"\n"+"fstsw ax\nsahf\njb fin_while:\n";
        }
        if(flag == 5 && flagWhile == 1) {
            flag = 0;
            return dato+"\n"+"fstsw ax\nsahf\nja fin_while:\n";
        }
        return "FLD "+ dato +"\n";
    }

    private String traducir(String dato) {
        if(dato == "+") {
            return "FADD\n";
        }
        if(dato == "-") {
            return "FSUB\n";
        }
        if(dato == "*") {
            return "FMUL\n";
        }
        if(dato == "/") {
            return "FDIV\n";
        }
        if(dato == ">") {
            flag = 2;
            return "FCOMP ";
        }
        if(dato == "<") {
            flag = 3;
            return "FCOMP ";
        }
        if(dato == ">=") {
            flag = 4;
            return "FCOMP ";
        }
        if(dato == "<=") {
            flag = 5;
            return "FCOMP ";
        }
        if(dato == " else") {
            return "else_part:\n";
        }
        //if(dato == " while") {
        //    flagWhile = 1;
        //}
        return "";
    }

    private void generarCodigo(FileWriter fileWriter) throws IOException {
        //TO DO
        IntermediateCodeGenerator gci =  IntermediateCodeGenerator.getInstance();
        fileWriter.write(recorrer(gci));

    }

    private void finishCode(FileWriter fileWriter) throws IOException {
        fileWriter.write("\n\nmov ax,4c00h ; Indica que debe finalizar la ejecucion\nInt 21h\n\nEnd");
    }

    public void generarAssembler() {
        System.out.println("Generando Assembler");
    }

}
