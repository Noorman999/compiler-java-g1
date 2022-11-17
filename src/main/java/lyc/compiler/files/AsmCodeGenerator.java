package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;

public class AsmCodeGenerator implements FileGenerator {

    //PARA QUE NO FALLE main/compiler
    private static AsmCodeGenerator asm;

    //PARA QUE NO FALLE main/compiler
    public static AsmCodeGenerator getInstance() {
        return asm;
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        fileWriter.write("TODO");
    }
}
