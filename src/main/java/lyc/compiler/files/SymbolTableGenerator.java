package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTableGenerator implements FileGenerator {

    private static SymbolTableGenerator symbolTable;
    private Map<String,SymbolTableToken> register;
    SymbolTableGenerator() {
        this.register = new HashMap<String,SymbolTableToken>();
    }
    public static SymbolTableGenerator getInstance() {
        if(symbolTable == null) {
            symbolTable = new SymbolTableGenerator();
        }
        return symbolTable;
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        String file = String.format("%-40s|%-10s|%-40s|%-10s\n","NOMBRE","TIPODATO","VALOR","LONGITUD");
        for (Map.Entry<String, SymbolTableToken> entry : this.register.entrySet()) {
            file += entry.getValue().toString() + "\n";
        }
        fileWriter.write(file);
    }

    public String getLength(String token) {
        return String.valueOf(token.length());
    }

    public void addTokenIdAssignment(String token) {
        if(!this.register.containsKey(token)) {
            this.register.put(token,new SymbolTableToken(token, "","",""));
        }
    }

    public void addTokenCteAssignment(String token, String dataType) {
        if(!this.register.containsKey(token)) {
            String newToken = (dataType == "String" ? token.replaceAll("\"", "") : token);
            this.register.put(token,new SymbolTableToken("_"+newToken,dataType,newToken,dataType == "String" ? getLength(newToken) : ""));
        }
    }

    static String dataTypeAux="";
    static String tokenAux="";
    public void addTokenInit(String token) {
        String dataType="";
        if(token=="String" || token=="Float" || token=="Int")
        {
            dataTypeAux=token;
        }
        else if(!this.register.containsKey(token)) {
            this.register.put(token,new SymbolTableToken(token, dataTypeAux,"",""));
        }

    }

    public ArrayList<SymbolTableToken> getTs() {
        ArrayList<SymbolTableToken> arr = new ArrayList<>();
        for ( SymbolTableToken token : this.register.values() ) {
            arr.add(token);
        }
        return arr;
    }
}
