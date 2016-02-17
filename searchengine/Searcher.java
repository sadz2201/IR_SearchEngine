package ir.searchengine;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
// import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.apache.lucene.store.FSDirectory;

import com.spatial4j.core.context.SpatialContext;
import com.spatial4j.core.distance.DistanceUtils;
import com.spatial4j.core.shape.Circle;
import com.spatial4j.core.shape.Point;

public class Searcher {

	IndexSearcher indexSearcher;
	QueryParser queryParser;
	Query query;
	int num_of_result;

	public Searcher(int n) {
		num_of_result = n;
		try {
			indexSearcher = new IndexSearcher(DirectoryReader.open(FSDirectory
					.open(new File("index-directory").toPath())));

			queryParser = new QueryParser("text", new EnglishAnalyzer());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Document getDoc(int docid) throws IOException {
		return indexSearcher.doc(docid);
	}

	public TopDocs free_text_search(String querystr) throws IOException,
			ParseException {
		Query query = queryParser.parse(querystr);
		return indexSearcher.search(query, num_of_result);
	}

	public TopDocs spatial_search(String querystr, double lon, double lat, double radius) throws IOException,
			ParseException {
		Query query = queryParser.parse(querystr);
		SpatialContext ctx = SpatialContext.GEO;
		Point center = ctx.makePoint(lon, lat);
		Circle circle = ctx.makeCircle(center, radius * DistanceUtils.KM_TO_DEG); 
		SpatialArgs args = new SpatialArgs(SpatialOperation.IsWithin, circle);
		SpatialPrefixTree grid = new GeohashPrefixTree(ctx, 11);

		RecursivePrefixTreeStrategy strategy = new RecursivePrefixTreeStrategy(grid, "location");

		Filter spatialFilter = strategy.makeFilter(args);

		return indexSearcher.search(query, spatialFilter, num_of_result);
	}
}
