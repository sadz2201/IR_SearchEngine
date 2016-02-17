package ir.searchengine;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.spatial4j.core.context.SpatialContext;
import com.spatial4j.core.shape.Point;

public class Indexer {

	private IndexWriter indexWriter;

	public void closeIndexWriter() throws IOException {
		if (indexWriter != null) {
			indexWriter.close();
		}
	}

	public Indexer(JsonLoader jl, IndexWriterConfig config) {
		this(jl, config, -1);
	}

	public Indexer(JsonLoader jl, IndexWriterConfig config, int n) {

		SpatialContext ctx = SpatialContext.GEO;
		SpatialPrefixTree grid = new GeohashPrefixTree(ctx, 11);

		RecursivePrefixTreeStrategy strategy = new RecursivePrefixTreeStrategy(
				grid, "location");

		try {
			Directory indexDir = FSDirectory.open(new File("index-directory")
					.toPath());
			indexWriter = new IndexWriter(indexDir, config);
			Review review;
			Map<String, Business> map = jl.get_id_to_business_map();
			
			int count = 0;
			while ((review = jl.nextReview()) != null
					&& (++count <= n || n == -1)) {
				Document document = new Document();
				
				document.add(new TextField("text", review.text, Store.YES));

				// adding spatial information
				Business busi = map.get(review.business_id);
				
				Point pt = ctx.makePoint(busi.longitude, busi.latitude);
				for (IndexableField f : strategy.createIndexableFields(pt)) {
					document.add(f);
				}


				indexWriter.addDocument(document);

			}
			
			indexWriter.commit();
			// System.out.printf("done!\n");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
