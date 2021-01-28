import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Скрипт для удаления дубликатов записей в скриптах справочников.
 * Путь до папки со справочниками указывается в переменной sourceDir
 */
public class DeleteDublicatesIntoSqlDvmScrypts {
    public void deleteDublicates(File targetFile) throws IOException {
        List<String> arraySourceFile = new MergeSqlDvmScryptsWithConflicts().makeArrayDict(targetFile);
        System.out.println("Формирования массива данных для справочника " + targetFile.getName() + " завершено. Количество записей - " + arraySourceFile.size());
        arraySourceFile = arraySourceFile.stream().distinct().collect(Collectors.toList());
        System.out.println("Удаление дубликатов в массиве для справочника " + targetFile.getName() + " завершено. Количество записей - " + arraySourceFile.size());
        createScryptFromArray(arraySourceFile, targetFile);
        System.out.println("Запись в файл для справочника " + targetFile.getName() + " завершена");
    }

    public void createScryptFromArray(List<String> arraySourceFile, File targetFile) throws IOException {
        String result = "";
        Scanner scanner = new Scanner(targetFile);
        List<String> temp = Arrays.asList(arraySourceFile.remove(0).split(";"));
        List<String> tempMod = new ArrayList<>(temp);
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains("SQLDVM_SEQ.nextval") || line.contains("SQLDVM_SEQ.currval")) {
                if (!tempMod.isEmpty()) result += tempMod.remove(0) + ";" + System.lineSeparator();
                else {
                    if (arraySourceFile.isEmpty()) continue;
                    else {
                        temp = Arrays.asList(arraySourceFile.remove(0).split(";"));
                        tempMod.addAll(temp);
                        result += tempMod.remove(0) + ";" + System.lineSeparator();
                    }

                }
            } else result += line + System.lineSeparator();
        }
        scanner.close();
        //Rewriting the input text file with newContent
        FileWriter writer = new FileWriter(targetFile);
        writer.write(result);
        writer.close();
    }

    public static void main(String[] args) throws IOException {
        File sourceDir = new File("D:\\repo\\SQLDVM\\Scrypts");
        for (File targetFile : sourceDir.listFiles()) {
            if (!targetFile.isDirectory()) {
                new DeleteDublicatesIntoSqlDvmScrypts().deleteDublicates(targetFile);
            }
        }
    }
}
