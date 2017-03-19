/*
 * Decompiled with CFR 0_118.
 */
package si.personalshopper.data;

public class Tag {
    private String tag;
    private double[] similarities;

    public Tag(String tag, double[] similarities) {
        this.similarities = similarities;
        this.tag = tag;
    }

    public String getTag() {
        return this.tag;
    }

    public double[] getSimilarities() {
        return this.similarities;
    }

    public String getSimilaritiesString() {
        String similaritiesString = "";
        for (double similarity : this.similarities) {
            similaritiesString = similaritiesString + similarity + "; ";
        }
        return similaritiesString;
    }
}

