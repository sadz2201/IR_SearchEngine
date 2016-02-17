package ir.searchengine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

/**
 * Implement a simple command-line user interface
 *
 */
public class Console {

	// default number of results returned from Searcher
	int num_of_result = 10;
	Searcher searcher;

	public void welcome() {
		System.out.print("WELCOME TO SEARCH-ENGINE PROGRAM\n" + "\n"
				+ "Type `:help' for getting help.\n");
	}

	String path = "DataSet"; // current working directory

	// useful commands and examples
	public void help() {
		System.out.print("We have two searching modes: \n"
				+ "   (1) free text search, \n"
				+ "       e.g: cafe or  \"hello world\" \n"
				+ "   (2) search with location information\n"
				+ "       syntax :spatial <lon> <lat> <radius> <text>\n"
				+ "       e.g    :spatial -115 36 5 cafe \n"
				+ "Type :help for getting this message.\n"
				+ "     :index to start indexing.\n" + "     :quit to exit.\n"
				+ "     :path <path> to change path to dataset.\n"
				+ "     :limit <n> to set number of results.\n"

		);
	}

	// do indexing
	public void index() {
		System.out.print("Indexing ... ");

		// loading json files into memory
		JsonLoader jl = new JsonLoader(path);

		// create an analyzer which removes default stop words
		EnglishAnalyzer analyzer = new EnglishAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);

		Indexer indexer = new Indexer(jl, config);
		try {
			indexer.closeIndexWriter();
			System.out.print(" [done]\n");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String abbreviate(String str, int offset, int maxWidth) {
		if (str == null) {
			return null;
		}
		str = str.replace('\n', ' ');

		if (maxWidth < 4) {
			throw new IllegalArgumentException(
					"Minimum abbreviation width is 4");
		}
		if (str.length() <= maxWidth) {
			return str;
		}
		if (offset > str.length()) {
			offset = str.length();
		}
		if ((str.length() - offset) < (maxWidth - 3)) {
			offset = str.length() - (maxWidth - 3);
		}
		if (offset <= 4) {
			return str.substring(0, maxWidth - 3) + "...";
		}
		if (maxWidth < 7) {
			throw new IllegalArgumentException(
					"Minimum abbreviation width with offset is 7");
		}
		if ((offset + (maxWidth - 3)) < str.length()) {
			return "..." + abbreviate(str.substring(offset), 0, maxWidth - 3);
		}
		return "..." + str.substring(str.length() - (maxWidth - 3));
	}

	public void print_result(TopDocs rl) {
		int count = 0;
		System.out
				.printf("  rank |   score    |   docID    |         review        \n");
		System.out
				.printf("-------+------------+------------+----------------------- \n");

		for (ScoreDoc doc : rl.scoreDocs) {
			count++;
			Document document;
			try {
				document = searcher.getDoc(doc.doc);
				System.out.printf("# %4d | %10.3f | %10d | %20s  \n", count,
						doc.score, doc.doc,
						abbreviate(document.get("text"), 0, 40));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void free_search(String query) {
		searcher = new Searcher(num_of_result);
		try {
			TopDocs rl = searcher.free_text_search(query);
			print_result(rl);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void spatial_search(String query) {
		String[] list_str = query.split(" ");
		searcher = new Searcher(num_of_result);
		try {
			String str = String.join(" ",
					Arrays.copyOfRange(list_str, 4, list_str.length));
			TopDocs rl = searcher.spatial_search(str,
					Double.parseDouble(list_str[1]),
					Double.parseDouble(list_str[2]),
					Double.parseDouble(list_str[3]));
			print_result(rl);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void repl() {

		welcome();

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.print("\n[SeaEng] >> ");
			try {
				String text = br.readLine().trim();
				if (text.startsWith(":quit")) {
					return;
				} else if (text.startsWith(":help")) {
					help();
				} else if (text.startsWith(":index")) {
					index();
				} else if (text.startsWith(":spatial")) {
					spatial_search(text);
				} else if (text.startsWith(":limit")) {
					num_of_result = Integer.parseInt(text.substring(7));
				} else if (text.startsWith(":path")) {
					path = text.substring(6);
				} else
					free_search(text);

			} catch (IOException e) {
				System.err.print(" err :-( \n");
			}
		}
	}

	public static void main(String[] args) {
		Console cs = new Console();

		cs.repl();
	}

}
