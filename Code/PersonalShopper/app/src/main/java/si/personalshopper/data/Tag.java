package si.personalshopper.data;

/**
 * Created by Senna on 15-5-2016.
 */
public class Tag {

    private String tag;
    private double[] similarities;

    // Call when the tag is already added to SimilarityTags
    public Tag(String tag, double[] similarities) {
        this.similarities = similarities;
        this.tag = tag;
    }

    // Make new tag and add to SimilarityTags
    public Tag(String tag, double[] similarities, SimilarityTags currentSimilarities) {
        this.similarities = similarities;
        this.tag = tag;
        currentSimilarities.updateSimilaritiesWithTag(this);
    }

    public String getTag() { return tag; }

    public double[] getSimilarities() { return similarities; }

    public String getSimilaritiesString() {
        String similaritiesString = "";
        for (double similarity: similarities) {
            similaritiesString += (similarity + "; ");
        }
        return similaritiesString;
    }

}
