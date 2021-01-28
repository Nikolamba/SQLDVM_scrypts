import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class SQLDVMTestLookupValueFunction {
    public static void testLookupValue(File fileName) throws IOException {
        File fileForJDev = new File("C:\\Users\\nmeleshkin\\Desktop\\SQLDMVScrypt\\fileForJDev.txt");
        File fileWithVar = new File("C:\\Users\\nmeleshkin\\Desktop\\SQLDMVScrypt\\fileWithVar.txt");
        StringBuilder sb = new StringBuilder();
        StringBuffer varArr = new StringBuffer();
        Scanner scanner = new Scanner(fileName);
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String dictVar = line.substring(44).split("\"")[0].replace(".", "");

            varArr.append("<variable name=\"");
            varArr.append(dictVar);
            varArr.append("\" type=\"xsd:string\"/>" + "\n");

            sb.append("<copy ignoreMissingFromData=\"yes\" bpelx:insertMissingToData=\"yes\">" + "\n");
            sb.append("<from>" + line + "</from>" + "\n");
            sb.append("<to>$" + dictVar + "</to>" + "\n");
            sb.append("</copy>" + "\n");
        }
        scanner.close();

        FileWriter writer = new FileWriter(fileForJDev);
        writer.write(sb.toString());
        writer.close();

        FileWriter writer1 = new FileWriter(fileWithVar);
        writer1.write(varArr.toString());
        writer1.close();
    }

    public static void main(String[] args) throws IOException
    {
        File dir = new File("C:\\Users\\nmeleshkin\\Desktop\\SQLDMVScrypt\\test.txt");
        testLookupValue(dir);
        System.out.println("done");
    }
}