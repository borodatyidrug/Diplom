import java.io.File;
import java.io.IOException;
import java.util.*;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import java.util.Map.Entry;

public class BooleanSearchEngine implements SearchEngine {
    
    protected Map<String, List<PageEntry>> index;
    
    public BooleanSearchEngine(File pdfsDir) throws IOException {
        
        index = new HashMap<>();
        for (File file : pdfsDir.listFiles()) {
            var doc = new PdfDocument(new PdfReader(file));
            for (int i = 1; i <= doc.getNumberOfPages(); i++) {
                var text = PdfTextExtractor.getTextFromPage(doc.getPage(i));
                var words = text.split("\\P{IsAlphabetic}+");
                // Набор слов с их количествами для текущей страницы
                Map<String, Integer> freqs = new HashMap<>();
                for (String word : words) {
                    if(!word.isBlank()) {
                        freqs.put(word.toLowerCase(), freqs.getOrDefault(word, 0) + 1);
                    }
                }
                // Получаем множество вхождений из предыдущей мапы для итерирования по этому множеству
                Set<Entry<String, Integer>> currPageWordSet = freqs.entrySet();
                for (Entry<String, Integer> freqsEntry : currPageWordSet) {
                    if (index.containsKey(freqsEntry.getKey())) {
                        // Если такое слово нашлось в мапе, то, значит, оно отображается на уже существующий список, который
                        // получаем из индекса и добавляем в него новый элемент с соотв. значениями слова, номера страницы,
                        // количества на странице
                        index.get(freqsEntry.getKey()).add(new PageEntry(file.getName(), i, freqsEntry.getValue()));
                    } else {
                        // В противном случае создаем новый список, добавляем в него первый элемент
                        List<PageEntry> currEntryList = new ArrayList<>();
                        currEntryList.add(new PageEntry(file.getName(), i, freqsEntry.getValue()));
                        // И добавляем в индекс
                        index.put(freqsEntry.getKey(), currEntryList);
                    }
                }
            }
        }
        // Сортируем списки в индексе по каждому ключу
        Set<String> keySet = index.keySet();
        for (String e : keySet) {
            Collections.sort(index.get(e), Comparator.reverseOrder());
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        if (index.containsKey(word)) {
            return index.get(word);
        } else {
            // Если слово - не найдено, то вернем список с одним елементом с параметрами по-умолчанию
            List<PageEntry> defaultList = new ArrayList<>();
            defaultList.add(new PageEntry("Несуществующий документ.pdf", 0, 0));
            return defaultList;
        }
    }
}
