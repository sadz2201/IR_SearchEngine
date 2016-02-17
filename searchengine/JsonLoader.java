package ir.searchengine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.google.gson.Gson;

public class JsonLoader {

	private HashMap<String, Business> id_to_business_map;
	private BufferedReader reviewReader;
	private int file_size;

	public int get_size_in_bytes() {
		return file_size;
	}
	
	public Review nextReview() {
		try {
			String line = reviewReader.readLine();
			if (line == null)
				return null;
			return new Gson().fromJson(line, Review.class);
		} catch (Exception e) {
		}

		return null;
	}

	public HashMap<String, Business> get_id_to_business_map() {
		return id_to_business_map;
	}

	public JsonLoader(String filePath) {
		String line;
		Gson gson = new Gson();
		id_to_business_map = new HashMap<String,Business>();
		// System.out.print("Loading data files ...");

		try {
			BufferedReader businessReader = new BufferedReader(new FileReader(
					filePath + "/yelp_academic_dataset_business.json"));

			FileReader fr = new FileReader(filePath+ "/yelp_academic_dataset_review.json");
			reviewReader = new BufferedReader(fr);

			file_size = (int) new File(filePath+ "/yelp_academic_dataset_review.json").length();
			
			while ((line = businessReader.readLine()) != null) {
				Business obj = gson.fromJson(line, Business.class);
				id_to_business_map.put(obj.business_id, obj);
			}

			// System.out.println("   [done] ");
			businessReader.close();

		} catch (FileNotFoundException e) {
			System.err.println("  File missing!!!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
