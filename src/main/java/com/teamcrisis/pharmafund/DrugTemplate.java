package com.teamcrisis.pharmafund;

/**
 * Handles drug creation and update requests.
 */
public class DrugTemplate {
    String link;
    String amazonDrugId;
    String name;
    int count;

    public DrugTemplate() { }

    public String getAmazonDrugId() {
        return amazonDrugId;
    }

    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }
}
