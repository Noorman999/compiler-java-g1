package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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
        generarCodigo();
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
        fileWriter.write("\n\n.CODE\nmov AX, @DATA ; inicializa el segmento de datos\nmov DS,AX\nmov es,ax;\n");
    }


    private void generarCodigo() {
        //TO DO
        IntermediateCodeGenerator gci =  IntermediateCodeGenerator.getInstance();
        gci.recorrer();
    }

    private void finishCode(FileWriter fileWriter) throws IOException {
        fileWriter.write("\nmov ax,4c00h ; Indica que debe finalizar la ejecucion\nInt 21h\n\nEnd");
    }

    public void generarAssembler() {
        System.out.println("Generando Assembler");
    }

}
