import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ReplaceNumberInDict {
    static void modifyFile(File filePath) throws IOException
    {
        String header = "INSERT INTO NF_DVM (DVM_NAME, ROW_NUMBER, COLUMN_NAME, COLUMN_VALUE) VALUES ('";
        File fileToBeModified = filePath;
        String oldContent = "";
        FileWriter writer = null;
        Pattern pattern = Pattern.compile("INSERT INTO NF_DVM \\(DVM_NAME, ROW_NUMBER, COLUMN_NAME, COLUMN_VALUE\\) VALUES \\('.+?',");
        Scanner scanner = new Scanner(fileToBeModified);
        String row_nuber_new = "";
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (line.contains(header) && !line.contains("SQLDVM_SEQ.nextval") && !line.contains("SQLDVM_SEQ.currval")) {
                String dict_name = line.split("'")[1];
               // System.out.println("dict_name: " + dict_name);
                String row_nuber = line.split("'")[3];
                //System.out.println("row_nuber: " + row_nuber);
                String tail = line.split("'", 5)[4];
                //System.out.println("tail: " + tail);

                String row = "";
                if (!row_nuber.equals(row_nuber_new)) {
                    row = "SQLDVM_SEQ.nextval";
                    row_nuber_new = row_nuber;
                } else row = "SQLDVM_SEQ.currval";
                String newLine = header + dict_name + "'," + row + tail;
                oldContent = oldContent + newLine + System.lineSeparator();
            }
            else {
                oldContent = oldContent + line + System.lineSeparator();
            }
            //System.out.println(oldContent);
        }
        scanner.close();
            //Rewriting the input text file with newContent
            writer = new FileWriter(fileToBeModified);
            writer.write(oldContent);
            writer.close();
    }

    public static void main(String[] args) throws IOException
    {
        File dir = new File("D:\\repo\\SQLDVM2\\sqldvm\\EDM\\VTB24\\SQLDVM\\Setting\\sqlScripts");
        for (File file : dir.listFiles()) {
            modifyFile(file);
        }
        System.out.println("done");
    }
}
