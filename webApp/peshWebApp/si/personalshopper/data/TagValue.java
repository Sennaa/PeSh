/*
 * Decompiled with CFR 0_118.
 */
package si.personalshopper.data;

public class TagValue {
    private String tag;
    private double rating;

    public TagValue(String tag, double rating) {
        this.tag = tag;
        this.rating = rating;
    }

    public String getTag() {
        return this.tag;
    }

    public double getRating() {
        return this.rating;
    }

    public boolean equals(Object obj) {
        TagValue tagValue = (TagValue)obj;
        if (this.getTag().equals(tagValue.getTag()) && this.getRating() == tagValue.getRating()) {
            return true;
        }
        return false;
    }
}

