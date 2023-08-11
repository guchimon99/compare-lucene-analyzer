package tutorial;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;

public class App {

    String fileDir = "./data/file/";
    String indexDir = "./data/index/";
    String termDir = "./data/term/";
    String searchResultDir = "./data/searchResult/";
    String searchWordsFile = "./data/search/words.txt";

    public static void main(String[] args) {
        try {
            App app = new App();
            app.createIndex();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void createIndex() throws IOException, Exception  {
        Analyzer kuromojiAnalyzer = new JapaneseAnalyzer();
        String kuromojiIndexDir = indexDir + "/kuromoji/";
        Indexer kuromojiIndexer = new Indexer(kuromojiIndexDir, kuromojiAnalyzer);
        kuromojiIndexer.createIndex(fileDir);
        kuromojiIndexer.close();

        Analyzer cjkAnalyzer = new CJKAnalyzer();
        String cjkIndexDir = indexDir + "/cjk/";
        Indexer cjkIndexer = new Indexer(cjkIndexDir, cjkAnalyzer);
        cjkIndexer.createIndex(fileDir);
        cjkIndexer.close();

        String kuromojiTermFile = termDir + "/kuromoji.txt";
        TermExportor kuromojiTermExportor = new TermExportor(kuromojiIndexDir);
        kuromojiTermExportor.export(kuromojiTermFile);

        String cjkTermFile = termDir + "/cjk.txt";
        TermExportor cjkTermExportor = new TermExportor(cjkIndexDir);
        cjkTermExportor.export(cjkTermFile);

        List<String> searchWords = Files.readAllLines(Paths.get(searchWordsFile));

        SearchResultExportor kuromojiSearchResultExportor = new SearchResultExportor(kuromojiIndexDir, kuromojiAnalyzer);
        kuromojiSearchResultExportor.export(searchWords, searchResultDir + "kuromoji");

        SearchResultExportor cjkSearchResultExportor = new SearchResultExportor(cjkIndexDir, cjkAnalyzer);
        cjkSearchResultExportor.export(searchWords, searchResultDir + "cjk");
    }
}
