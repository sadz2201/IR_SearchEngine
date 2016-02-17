package ir.searchengine;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.index.IndexWriterConfig;

public class Preprocessing {

	class PairSize {
		public final Long dic_size;
		public final Long idx_size;

		public PairSize(Long t, Long u) {
			this.dic_size = t;
			this.idx_size = u;
		}
	}

	public PairSize get_sizes_and_delete() {
		final File folder = new File("index-directory");
		long dic_size = 0;
		long idx_size = 0;
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.getName().endsWith(".tim"))
				dic_size += fileEntry.length();
			if (fileEntry.getName().endsWith(".tip"))
				idx_size += fileEntry.length();

			fileEntry.delete();
		}
		return new PairSize(dic_size, idx_size);
	}

	public long time_for_indexing(JsonLoader jl, IndexWriterConfig config) {
		long startTime = System.nanoTime();
		Indexer indexer = new Indexer(jl, config);
		try {
			indexer.closeIndexWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endTime = System.nanoTime();
		return (endTime - startTime) / 1000000; // in milliseconds
	}

	public void evaluation() {
		Analyzer base_analyzer = new Analyzer() {
			public String toString() { return "Base_analyzer"; }

			@Override
			protected TokenStreamComponents createComponents(String fieldName) {
				Tokenizer source = new StandardTokenizer();
				return new TokenStreamComponents(source);
			}
		};

		Analyzer stem_analyzer = new Analyzer() {
			public String toString() { return "Stem_analyzer"; }

			@Override
			protected TokenStreamComponents createComponents(String fieldName) {
				Tokenizer source = new StandardTokenizer();
				TokenStream filter = new PorterStemFilter(source);
				return new TokenStreamComponents(source, filter);
			}
		};

		Analyzer lowercase_analyzer = new Analyzer() {
			public String toString() { return "LowerCase_analyzer"; }

			@Override
			protected TokenStreamComponents createComponents(String fieldName) {
				Tokenizer source = new StandardTokenizer();
				TokenStream filter = new LowerCaseFilter(source);
				return new TokenStreamComponents(source, filter);
			}
		};

		Analyzer stopword_analyzer = new Analyzer() {
			public String toString() { return "Stopword_analyzer"; }

			@Override
			protected TokenStreamComponents createComponents(String fieldName) {
				Tokenizer source = new StandardTokenizer();
				TokenStream filter = new StopFilter(source, StopAnalyzer.ENGLISH_STOP_WORDS_SET);
				return new TokenStreamComponents(source, filter);
			}
		};
		
		IndexWriterConfig[] list_of_config = {
				new IndexWriterConfig(base_analyzer), 
				new IndexWriterConfig(stem_analyzer), 
				new IndexWriterConfig(lowercase_analyzer), 
				new IndexWriterConfig(stopword_analyzer), 
				};

		String path = "/Users/Mar/Documents/workspace/yelp_dataset_challenge_academic_dataset";
		System.out.printf(" | %18s | %10s | %10s | %10s | \n", "Name",
				"Duration", "Dic_Size", "Idx_Size");
		System.out.printf(" + %18s + %10s + %10s + %10s + \n",
				"------------------", "----------", "----------", "----------");

		for (IndexWriterConfig config : list_of_config) {
			JsonLoader jl = new JsonLoader(path);
			long duration = time_for_indexing(jl, config);
			PairSize ps = get_sizes_and_delete();
			String name = config.getAnalyzer().toString();
			System.out.printf(" | %18s | %10.2f | %10s | %10d | \n", name,
					duration / 1000.0, ps.dic_size, ps.idx_size);

		}

	}

	public static void main(String[] args) {
		Preprocessing prep = new Preprocessing();
		prep.evaluation();
	}

}
