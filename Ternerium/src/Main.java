import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class Main {
    static HashMap<String,Command> commands;
    static{
         commands = new HashMap<>();
         commands.put("class", (input1, input2) -> "class " + input1 + "{ " + toJava(input2) + "}");
         commands.put("set", (input1, input2) -> input1 + " = " + toJava(input2) + ";");
         commands.put("void", (input1, input2) -> "void " + input1 + "(){" + toJava(input2) + "}");
         commands.put("returnType", (input1, input2) -> toJava(input2).replaceFirst("void",input1));
         commands.put("modify", (input1, input2) -> input1 + " " + toJava(input2));
         commands.put("if", (input1, input2) -> "if(" + toJava(input1) + "){" + toJava(input2) + "}");
         commands.put("run", (input1, input2) -> input1+"("+toJava(input2)+")");
         commands.put("while", (input1, input2) -> "while(" + toJava(input1) + "){" + toJava(input2) + "}");
         commands.put("break",((input1, input2) -> "break;"));
        commands.put("continue",((input1, input2) -> "continue;"));
        commands.put("return",((input1, input2) -> "return " + toJava(input1) + ";"));
        commands.put("parameters", new Command() {
            @Override
            public String toJavaCode(String input1, String input2) throws Exception {
                return toJava(input2).replaceFirst("\\(","("+toString(input1).substring(1));
            }
            public String toString(String input){
                if (!input.contains("?")){
                    return "";
                }
                input = input.substring(1,input.length()-1);
                String num1 = input.substring(0,input.indexOf("?"));
                String num2 = input.substring(input.indexOf("?")+1,input.indexOf(":"));
                String num3 = input.substring(input.indexOf(":")+1);
                return ","+num1 + " " + num2 + toString(num3);
            }
        });
        commands.put("new", (input1, input2) -> "new " + input1 + "(" + toJava(input2) + ")");
    }
    public static void main(String[] args) throws Exception {
        File output = new File("./output.txt");
        output.delete();
        output.createNewFile();
        String input = Files.readString(Path.of("./input.txt"));
        FileWriter fw = new FileWriter(output);
        fw.write(toJava(input));
        fw.flush();
        fw.close();
    }
    public static String toJava(String input) throws Exception{
        if (!input.contains("?")){
            return input;
        }
        if (input.startsWith("(")){
            int paranthesesLeft = 1;
            int index=-1;
            for (int i = 1; i <= input.length(); i++) {
                if (i==input.length()){
                    throw new Exception("Missing a ).");
                }
                if (input.charAt(i)=='('){
                    paranthesesLeft++;
                }
                if (input.charAt(i)==')') {
                    paranthesesLeft--;
                    if (paranthesesLeft == 0) {
                        index = i;
                        break;
                    }
                }
            }
            return toJava(input.substring(1,index))+toJava(input.substring(index+1));
        }
        else{
            String begin = input.substring(0,input.indexOf("?"));
            int colonsLeft = 1;
            int index=-1;
            for (int i = input.indexOf("?")+1; i <= input.length(); i++) {
                if (i==input.length()){
                    throw new Exception("Missing a :.");
                }
                if (input.charAt(i)=='?'){
                    colonsLeft++;
                }
                if (input.charAt(i)==':') {
                    colonsLeft--;
                    if (colonsLeft == 0) {
                        index = i;
                        break;
                    }
                }
            }
            String param1 = input.substring(input.indexOf("?")+1,index);
            String param2 = input.substring(index+1);
            return commands.getOrDefault(begin, (input1, input2) -> begin + " " + input1 + (input2.equals("none") ? ";" : (" = " + input2 + ";"))).toJavaCode(param1,param2);
        }
    }
}