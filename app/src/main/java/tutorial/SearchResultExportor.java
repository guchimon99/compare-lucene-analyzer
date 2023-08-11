package tutorial;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SearchResultExportor {

    private Directory directory;
    private Analyzer analyzer;

    public SearchResultExportor(String indexDirectoryPath, Analyzer analyzer) throws IOException {
        this.directory = FSDirectory.open(Paths.get(indexDirectoryPath));
        this.analyzer = analyzer;
    }

    public List<String> search(String queryString) throws Exception {
        List<String> results = new ArrayList<>();

        try (IndexReader reader = DirectoryReader.open(directory)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser parser = new QueryParser("contents", analyzer);
            Query query = parser.parse(queryString);

            TopDocs topDocs = searcher.search(query, 10);

            QueryScorer scorer = new QueryScorer(query);
            Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
            SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter();
            Highlighter highlighter = new Highlighter(simpleHTMLFormatter, scorer);
            highlighter.setTextFragmenter(fragmenter);

            Integer maxNumFragments = 2;

            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                String filepath = doc.get("filepath");
                String contents = doc.get("contents");
                TokenStream tokenStream = analyzer.tokenStream("contents", new StringReader(contents));

                String[] fragments = highlighter.getBestFragments(tokenStream, contents, maxNumFragments);

                String highlightedText = String
                    .join("", fragments)
                    .replace("\n", " ")
                    .replace("\r", "");

                results.add(String.format("Query: %s, File: %s\nContent: %s\n", queryString, filepath, highlightedText));
            }
        }

        return results;
    }

    public void export(List<String> queryStrings, String outputDirPath) throws Exception {
        clearSearchResultFiles(outputDirPath);
        for (String queryString : queryStrings) {
            exportSearchResults(queryString, outputDirPath + "/" + queryString + ".txt");
        }
    }

    public void exportSearchResults(String queryString, String outputPath) throws Exception {
        List<String> searchResults = search(queryString);
        Path file = Paths.get(outputPath);
        Files.write(file, searchResults, StandardCharsets.UTF_8);
    }

    private void clearSearchResultFiles(String outputDir) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(outputDir), "*.txt")) {
            for (Path entry : stream) {
                Files.delete(entry);
            }
        }
    }
}
