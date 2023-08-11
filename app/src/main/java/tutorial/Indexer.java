package tutorial;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {
    private IndexWriter writer;

    private Directory directory;

    public Indexer(String indexDirectoryPath, Analyzer analyzer) throws IOException {
        this.directory = FSDirectory.open(Paths.get(indexDirectoryPath));

        clearIndexDirectory(indexDirectoryPath);

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(directory, config);
    }

    private void deletePath(Path path) {
      try {
          Files.delete(path);
      } catch (IOException e) {
          System.err.println("Failed to delete " + path);
      }
    }

    private void deleteDirectoryRecursively(Path path) {
        try (Stream<Path> stream = Files.walk(path)) {
            stream
                .sorted(java.util.Comparator.reverseOrder())
                .forEach(p -> deletePath(p));
        } catch (IOException e) {
            System.err.println("Failed to read directory: " + path);
        }
    }

    private void clearIndexDirectory (String indexDirectoryPath) {
        Path path = Paths.get(indexDirectoryPath);
        deleteDirectoryRecursively(path);
    }

    private Document getDocument (File file) throws IOException {
        Document document = new Document();
        String contents = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        TextField contentsField = new TextField("contents", contents, TextField.Store.YES);
        TextField fileNameField = new TextField("filename", file.getName(), TextField.Store.YES);
        TextField filePathField = new TextField("filepath", file.getPath(), TextField.Store.YES);
        document.add(contentsField);
        document.add(fileNameField);
        document.add(filePathField);
        return document;
    }

    public void close() throws CorruptIndexException, IOException {
        writer.close();
    }

    private void indexFile(File file) throws IOException {
      Document document = getDocument(file);
      writer.addDocument(document);
    }

    public int createIndex(String dataDirPath) throws IOException {
      File[] files = new File(dataDirPath).listFiles();
      for (File file : files) {
          if (!file.isDirectory() && file.exists() && file.canRead())  {
              indexFile(file);
          }
      }
      return writer.numRamDocs();
    }
}
