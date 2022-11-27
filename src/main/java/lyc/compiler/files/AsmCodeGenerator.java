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
        generarCodigoAssembler(fileWriter);
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

    public String recorrerArbol2(IntermediateCodeGenerator gci) {

        IntermediateCodeNodo raiz = gci.register.get(Puntero.p_program);
        return this.recorrer2(raiz);
    }
    /*
    public String recorrerArbol(IntermediateCodeGenerator gci) {

        IntermediateCodeNodo raiz = gci.register.get(Puntero.p_program);
        return this.recorrer(raiz);
    }

    private int flag = 0;
    private int flagWhile = 0;
    private int flagElse = 0;
    private int flagIf = 0;
  */
    private String operation = "";
    private int flagComp = 0;
    private int contWhile = 0;
    private int contIf = 0;
    private String pivotDo = "";

    private String recorrer2(IntermediateCodeNodo nodo) {
        //System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++"+nodo.dato);

        String assembler = "";
        if(nodo.left == null && nodo.right == null) {
            return escribirHoja(nodo.dato);
        }

        if(nodo.dato == "=") {
            assembler += nodoIgual2(nodo);
            return assembler;
        }
        if(nodo.dato == " if") {
            contIf++;
            assembler += nodoIf2(nodo);
            return assembler;

        }
        if(nodo.dato == " while") {
            contWhile++;
            assembler += nodoWhile2(nodo);
            return assembler;
        }

        if(nodo.dato == " DO") {
            assembler += nodoDo(nodo);
            return assembler;
        }



        assembler += nodo.left != null ? recorrer2(nodo.left) : "";
        assembler += traducirOperacion(nodo.dato);
        assembler += nodo.right != null ? recorrer2(nodo.right) : "";

        return assembler;
    }

    private String nodoDo(IntermediateCodeNodo nodo) {
        String resParcial = "";
        resParcial += recorrerDo(nodo.right);

        return resParcial;
    }

    private String recorrerDOWithDefault(IntermediateCodeNodo nodo) {
        String resParcial = "";

        return resParcial;
    }
    private String recorrerDOWithoutDefault(IntermediateCodeNodo nodo) {
        String resParcial = "";
        if(nodo.dato == " CASE") {
            resParcial += recorreSubArbolCond(nodo.left);
            resParcial += recorrer2(nodo.right);
        }
        resParcial += nodo.left != null ? recorrerDOWithoutDefault(nodo.left) : "";
        resParcial += nodo.right != null ? recorrerDOWithoutDefault(nodo.right) : "";

        return resParcial;
    }

    private String recorrerDo(IntermediateCodeNodo nodo) {
        String resParcial = "";
        System.out.println("////////////////////////////////////////////////////////"+resParcial);

        if(nodo.dato == " DEFAULT") {
            resParcial += recorrerDOWithDefault(nodo);
        } else {
            resParcial += recorrerDOWithoutDefault(nodo);
        }

        return resParcial;
    }



    private String escribirHojaIgual(String dato) {
        return "FLD "+dato+"\n";
    }

    private String escribirHojaIf(String dato) {
        return "FLD "+dato+"\n";
    }

    private String traducirOperacion2(String dato) {
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
            flagComp = 1;
            operation = "jna";
            return "FCOMP ";
        }
        if(dato == "<") {
            flagComp = 1;
            operation = "jae";
            return "FCOMP ";
        }
        if(dato == "==") {
            flagComp = 1;
            operation = "jne";
            return "FCOMP ";
        }
        return "";
    }


    private String nodoIgual2(IntermediateCodeNodo nodo) {
        String resParcial = "";
        resParcial += recorreSubArbolIgual(nodo.right);
        resParcial += "FSTP "+nodo.left.dato+"\n";
        return resParcial;
    }
    private String recorreSubArbolIgual(IntermediateCodeNodo nodo) {
        String resParcial = "";
        if(nodo.left == null && nodo.right == null) {
            return escribirHojaIgual(nodo.dato);
        }
        resParcial += nodo.left != null ? recorreSubArbolIgual(nodo.left) : "";
        resParcial += nodo.right != null ? recorreSubArbolIgual(nodo.right) : "";
        resParcial += traducirOperacion2(nodo.dato);
        return resParcial;

    }

    private String nodoWhile2(IntermediateCodeNodo nodo) {
        String resParcial = "";
        resParcial += "inicio_while"+contWhile+":\n";
        resParcial += recorreSubArbolCond(nodo.left);
        resParcial += "fin_while"+contWhile+":\n";
        resParcial += recorrer2(nodo.right);
        resParcial += "JMP inicio_while"+contWhile+":\n";
        resParcial += "fin_while"+contWhile+":\n";

        return resParcial;

    }
    private String nodoIf2(IntermediateCodeNodo nodo) {
        String resParcial = "";
        resParcial += recorreSubArbolCond(nodo.left);
        if(nodo.right.dato == " else") {
            resParcial += "saltaElse"+contIf+":\n";
            resParcial += recorreSubArbolConElse(nodo.right);
            resParcial += "fin_if"+contIf+":\n";
        } else {
            resParcial += "fin_if"+contIf+":\n";
            resParcial += recorrer2(nodo.right);
            resParcial += "fin_if"+contIf+":\n";
        }
        return resParcial;
    }

    private String recorreSubArbolConElse(IntermediateCodeNodo nodo) {
        String resParcial = "";
        resParcial += recorrer2(nodo.left);
        resParcial += "jmp fin_if"+contIf+":\n";
        resParcial += "saltaElse"+contIf+":\n";
        resParcial += recorrer2(nodo.right);
        return resParcial;
    }

    //private boolean recorreBuscaElse(IntermediateCodeNodo nodo) {
        //recorrer

    //}


    private String recorreSubArbolCond(IntermediateCodeNodo nodo) {
        //System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++"+nodo.dato);

        String resParcial = "";
        if(nodo.left == null && nodo.right == null) {
            if(flagComp == 1) {
                flagComp = 0;
                return nodo.dato+"\n";
            }
            return escribirHojaIf(nodo.dato);
        }
        resParcial += recorreSubArbolCond(nodo.left);
        resParcial += traducirOperacion2(nodo.dato);
        resParcial += recorreSubArbolCond(nodo.right);
        resParcial += "fstsw ax\nsahf\n"+operation+" ";
        return resParcial;
    }


    private String traducirOperacion(String dato) {
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
            return "FCOMP ";
        }
        if(dato == "<") {
            return "FCOMP ";
        }
        if(dato == ">=") {
            return "FCOMP ";
        }
        if(dato == "<=") {
            return "FCOMP ";
        }
        return "";
    }

    private String escribirHoja(String dato) {
        return "FLD "+dato+"\n";
    }

/*
    private String recorrer(IntermediateCodeNodo nodo) {
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
            if(nodo.dato == " if") {
                flagIf = 1;
            }
            if(nodo.dato == " else") {
                flagElse = 1;
            }
            resParcial += nodo.left != null ? recorrer(nodo.left) : "";
            resParcial += traducir(nodo.dato);
            resParcial += nodo.right != null ? recorrer(nodo.right) : "";

            if(flagIf == 1) {
                flagIf = 0;
                resParcial += "fin_if:\n";
            }

        }

        return resParcial;
    }

    private String nodoIgual(IntermediateCodeNodo nodo) {
        String resParcial = "";
        if(nodo.dato == "=") {
            resParcial += recorrer(nodo.right);
            flag = 1;
            resParcial += recorrer(nodo.left);
        }
        return resParcial;
    }
    private String nodoIf(IntermediateCodeNodo nodo) {
        String resParcial = "";
        if(nodo.dato == "if") {
            resParcial += recorrer(nodo.left);
        }
        return resParcial;
    }

    private String nodoWhile(IntermediateCodeNodo nodo) {
        return "";
    }

    private String cargar(String dato) {
        //logica para subir CTE o ID al coprocesador
        if (flag == 1 && flagWhile == 0) {
            flag = 0;
            return "FSTP "+dato +"\n";
        }
        if(flag == 2 && flagWhile == 0) {
            flag = 0;
            return dato+"\n"+"fstsw ax\nsahf\njna fin_if\ntrue_part:\n";
        }
        if(flag == 3 && flagWhile == 0) {
            flag = 0;
            return dato+"\n"+"fstsw ax\nsahf\njae fin_if\ntrue_part:\n";
        }
        if(flag == 4 && flagWhile == 0) {
            flag = 0;
            return dato+"\n"+"fstsw ax\nsahf\njb fin_if\ntrue_part:\n";
        }
        if(flag == 5 && flagWhile == 0) {
            flag = 0;
            return dato+"\n"+"fstsw ax\nsahf\nja fin_if\ntrue_part:\n";
        }
        if(flag == 2 && flagElse == 1) {
            flagElse = 0;
            flag = 0;
            return dato+"\n"+"fstsw ax\nsahf\njna else_part\ntrue_part:\n";
        }
        if(flag == 3 && flagElse == 1) {
            flagElse = 0;
            flag = 0;
            return dato+"\n"+"fstsw ax\nsahf\njae else_part\ntrue_part:\n";
        }
        if(flag == 4 && flagElse == 1) {
            flagElse = 0;
            flag = 0;
            return dato+"\n"+"fstsw ax\nsahf\njb else_part\ntrue_part:\n";
        }
        if(flag == 5 && flagElse == 1) {
            flagElse = 0;
            flag = 0;
            return dato+"\n"+"fstsw ax\nsahf\nja else_part\ntrue_part:\n";
        }
        if (flag == 1 && flagWhile == 1) {
            flagWhile = 0;
            flag = 0;
            return "FSTP "+dato +"\njmp inicio_while:\nfin_while:\n";
        }
        if(flag == 2 && flagWhile == 1) {
            flagWhile = 0;
            flag = 0;
            return dato+"\n"+"fstsw ax\nsahf\njna fin_while:\n";
        }
        if(flag == 3 && flagWhile == 1) {
            flagWhile = 0;
            flag = 0;
            return dato+"\n"+"fstsw ax\nsahf\njae fin_while:\n";
        }
        if(flag == 4 && flagWhile == 1) {
            flagWhile = 0;
            flag = 0;
            return dato+"\n"+"fstsw ax\nsahf\njb fin_while:\n";
        }
        if(flag == 5 && flagWhile == 1) {
            flagWhile = 0;
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
            flagElse = 1;
            return "else_part:\n";
        }
        if(dato == " while") {
            flagWhile = 1;
        }
        if(dato == " _") {
            return "\n";
        }
        return "";
    }
*/
    private void generarCodigoAssembler(FileWriter fileWriter) throws IOException {
        //TO DO
        IntermediateCodeGenerator gci =  IntermediateCodeGenerator.getInstance();
        fileWriter.write(recorrerArbol2(gci));
    }

    private void finishCode(FileWriter fileWriter) throws IOException {
        fileWriter.write("\n\nmov ax,4c00h ; Indica que debe finalizar la ejecucion\nInt 21h\n\nEnd");
    }

    public void generarAssembler() {
        System.out.println("Generando Assembler");
    }

}
