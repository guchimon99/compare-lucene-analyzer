package tutorial;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class TermExportor {

    private Directory directory;

    public TermExportor(String indexDirectoryPath) throws IOException {
        this.directory = FSDirectory.open(Paths.get(indexDirectoryPath));
    }

    public List<String> getAllTerms() throws IOException {
        List<String> termsList = new ArrayList<>();

        try (IndexReader reader = DirectoryReader.open(directory)) {
            for (LeafReaderContext leafReaderContext : reader.leaves()) {
                Terms terms = leafReaderContext.reader().terms("contents");
                if (terms != null) {
                    TermsEnum termsEnum = terms.iterator();
                    BytesRef term;
                    while ((term = termsEnum.next()) != null) {
                        termsList.add(term.utf8ToString());
                    }
                }
            }
        }

        return termsList;
    }

    public void export(String outputPath) throws IOException {
        List<String> terms = getAllTerms();
        Path file = Paths.get(outputPath);
        Files.write(file, terms, StandardCharsets.UTF_8);
    }
}
