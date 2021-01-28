import com.sun.javaws.IconUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Скрипт для сравнения скриптов справочников в разных ветках.
 * Сравнивает справочники из директорий targetDir и sourceDir.
 * Добавляет значения в справочники из директории targetDir, если эти значения есть в справочнике из sourceDir, но нет в targetDir
 */
public class MergeSqlDvmScryptsWithConflicts {
    String header = "INSERT INTO NF_DVM (DVM_NAME, ROW_NUMBER, COLUMN_NAME, COLUMN_VALUE) VALUES ('";

    public void resolveConflict(File targetFile, File sourceFile) throws IOException {
        List<String> arrayTargetFile = makeArrayDict(targetFile);
        List<String> arraySourceFile = makeArrayDict(sourceFile);
        arraySourceFile = arraySourceFile.stream().distinct().collect(Collectors.toList());
        List<String> addingValue = new ArrayList<>();
        arraySourceFile.stream().filter(value -> !arrayTargetFile.contains(value)).forEach(value -> {
            //System.out.println("Add Value: " + value);
            addingValue.add(value);
        });
        addValueToTargetFile(addingValue, targetFile);
    }

    public void addValueToTargetFile(List<String> values, File targetFile) throws IOException {
        String result = "";
        int countStringInTargetFile = getCountString(targetFile);
        Scanner scanner = new Scanner(targetFile);
        int stringCounter = 0;
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            result += line + System.lineSeparator();
            if (line.contains("SQLDVM_SEQ.nextval") || line.contains("SQLDVM_SEQ.currval")) stringCounter++;
            if (stringCounter == countStringInTargetFile) {
                countStringInTargetFile = 0;
                //result += line + System.lineSeparator();
                if (!values.isEmpty()) {
                    result += "--Adding value" + System.lineSeparator();
                    System.out.println(targetFile.getName());
                }
                for (String value : values) {
                    for (String str : value.split(";")) {
                        result += str + ";" + System.lineSeparator();
                    }
                }
            }
        }
        scanner.close();
        //Rewriting the input text file with newContent
        FileWriter writer = new FileWriter(targetFile);
        writer.write(result);
        writer.close();
    }

    public int getCountString(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        int result = 0;
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains("SQLDVM_SEQ.nextval") || line.contains("SQLDVM_SEQ.currval")) result++;
        }
        scanner.close();
        return result;
    }

    public List<String> makeArrayDict(File file) throws IOException {
        ReplaceNumberInDict.modifyFile(file);
        int countRowInTargetFile = getCountDictRow(file);
        List<String> arrayDictInTargetFile = new ArrayList<>();
        Scanner scanner = new Scanner(file);
        int countRowTemp = 0;
        String dictValue = "";
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains(header)) {
                if (countRowTemp == countRowInTargetFile) {
                    countRowTemp = 1;
                    arrayDictInTargetFile.add(dictValue);
                    if (line.contains("'NULL'")) line = line.replace("'NULL'", "NULL");
                    dictValue = line.trim();
                }
                else {
                    if (line.contains("'NULL'")) line = line.replace("'NULL'", "NULL");
                    dictValue += line.trim();
                    countRowTemp++;
                }
            }
        }
        arrayDictInTargetFile.add(dictValue);
        scanner.close();
        return arrayDictInTargetFile;
    }

    public int getCountDictRow(File file) throws FileNotFoundException {
        int result = 0;
        Scanner scanner = new Scanner(file);
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains(header) && line.contains("SQLDVM_SEQ.nextval")) {
                if (result != 0) return result;
                else result = 1;
            }
            if (line.contains(header) && line.contains("SQLDVM_SEQ.currval")) result++;
        }
        scanner.close();
        return result;
    }

    public static void main(String[] args) throws IOException {
        File targetDir = new File("D:\\repo\\SQLDVM\\Scrypts");
        File sourceDir = new File("D:\\repo\\SQLDVM2\\sqldvm\\EDM\\VTB24\\SQLDVM\\Common\\sqlScripts");
        for (File targetFile : targetDir.listFiles()) {
            if (!targetFile.isDirectory()) {
                File sourceFile = Arrays.stream(sourceDir.listFiles()).filter(file1 -> file1.getName().equals(targetFile.getName())).findFirst().orElse(null);
                new MergeSqlDvmScryptsWithConflicts().resolveConflict(targetFile, sourceFile);
            }
        }
    }
}