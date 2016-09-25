package com.teamcrisis.pharmafund;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * Drug object
 */
public class Drug {
    private String drugTitle;
    private String amazonDrugId;
    private String amazonLink;
    private HashMap<String, Integer> amountOrdered;
    private double price;

    public Drug(Drug d, String leaderId) {
        this.drugTitle = d.getDrugTitle();
        this.amazonDrugId = d.getAmazonDrugId();
        this.amazonLink = d.getAmazonLink();
        this.price = d.getPrice();

        int orderCount = d.getAmountOrdered().get(leaderId);
        this.amountOrdered = new HashMap<>();
        this.amountOrdered.put(leaderId, orderCount);
    }

    public Drug(String leaderId, String amazonDrugId, int orderCount) throws UnsupportedEncodingException,
            NoSuchAlgorithmException, InvalidKeyException {
        this.amazonDrugId = amazonDrugId;

        this.price = PharmaService.getLowestPriceFromAmazon(amazonDrugId);
        this.drugTitle = PharmaService.getDrugTitleFromAmazon(amazonDrugId);

        this.amountOrdered = new HashMap<>();
        this.amountOrdered.put(leaderId, orderCount);
    }

    public int getLeaderCount(String leaderId) {
        return amountOrdered.get(leaderId);
    }

    public String getDrugTitle() {
        return drugTitle;
    }

    public String getAmazonDrugId() {
        return amazonDrugId;
    }

    public String getAmazonLink() {
        return amazonLink;
    }

    public HashMap<String, Integer> getAmountOrdered() {
        return amountOrdered;
    }

    public double getPrice() {
        return price;
    }

    public void increaseCount(String leaderId, int orderCount) {
        amountOrdered.put(leaderId, getLeaderCount(leaderId) + orderCount);
    }

    public void decreaseCount(String leaderId, int removeCount) {
        amountOrdered.put(leaderId, getLeaderCount(leaderId) - removeCount);
    }

    public void setCount(String leaderId, int orderCount) {
        amountOrdered.put(leaderId, orderCount);
    }

    public int getTotalCount() {
        int total = 0;
        for (int count : amountOrdered.values()) {
            total += count;
        }
        return total;
    }

    public void removeLeader(String leaderId) {
        amountOrdered.remove(leaderId);
    }

    public double getTotalPrice() {
        return getTotalCount() * price;
    }

    @Override
    public String toString() {
        return amountOrdered.toString() + "\n";
    }
}
