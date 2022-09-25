package lyc.compiler.files;


public class SymbolTableToken {

    private String name;
    private String dataType;
    private String value;
    private String length;

    public SymbolTableToken(){
        this.name = "";
        this.dataType = "";
        this.value = "";
        this.length = "";
    }

    public SymbolTableToken(String name, String dataType, String value, String length){
        this.name = name;
        this.dataType = dataType;
        this.value = value;
        this.length = length;
    }

    public String toString() {
        return String.format("%-40s|%-10s|%-40s|%-10s",this.name, this.dataType, this.value, this.length);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }
}
