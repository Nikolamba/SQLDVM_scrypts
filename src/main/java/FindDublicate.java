import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Скрипт проверяет, есть ли дубли справочников в указанной папке
 * Для проверки отбрасывается номер справочника.
 */
public class FindDublicate {
    public static void main(String[] args) {
        //Указываем путь до папки с sql скриптами
        File dir = new File("D:\\repo\\SQLDVM\\sqldvm\\EDM\\VTB24\\SQLDVM\\Common\\sqlScripts");
        Set<String> setDict = new HashSet<String>();
            for (File dictFile : dir.listFiles()) {
                if (!dictFile.isDirectory()) {
                    String dictName = dictFile.getName().substring(7);
                    if (!setDict.contains(dictName)) {
                        setDict.add(dictName);
                    } else {
                        System.out.println("Ошибка. Справочник " + dictFile + " задвоен");
                    }
                }
            }
    }
}
