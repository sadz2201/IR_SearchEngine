package ir.searchengine;

public class SearchResult {
    public String business_id;
    public Double longitude;
    public Double latitude;

    @Override
    public String toString() {
        return String.format("%s: (%f, %f) ", business_id, longitude, latitude );
    }
}
