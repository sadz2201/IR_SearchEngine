package ir.searchengine;

/**
 * Capture objects from Review json file
 */
public class Review {
	public String review_id;
    public String text;
    public String business_id;

    @Override
    public String toString() {
        return String.format("(%s) %s : %s [...]", review_id, business_id, text.substring(0,70));
    }

}
