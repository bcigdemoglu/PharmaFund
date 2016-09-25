package com.teamcrisis.pharmafund;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Database of all drugs
 */
public class Catalog {
    HashMap<String, Drug> dMap;

    public Catalog() {
        dMap = new HashMap<String, Drug>();
    }

    public void addDrug(String leaderId, String amazonDrugId, int orderCount) throws UnsupportedEncodingException,
            NoSuchAlgorithmException, InvalidKeyException {
        System.out.println(leaderId + " " + amazonDrugId + " " + orderCount);
        Drug d = dMap.get(amazonDrugId);
        System.out.println(dMap.toString());
        if (d != null) {
            System.out.println(d.toString());
            // Update count if drug exists
            d.increaseCount(leaderId, orderCount);
            System.out.println(d.toString());

        } else {
            // Create drug if does not exist
            d = new Drug(leaderId, amazonDrugId, orderCount);
            dMap.put(amazonDrugId, d);
        }
    }

    public Drug getDrug(String amazonDrugId) {
        return dMap.get(amazonDrugId);
    }

    public Drug removeDrug(String leaderId, String amazonDrugId, int removeCount) {
        Drug d = dMap.get(amazonDrugId);
        d.decreaseCount(leaderId, removeCount);
        if (d.getTotalCount() == 0) {
            dMap.remove(amazonDrugId);
        } else if (d.getLeaderCount(leaderId) == 0) {
            d.removeLeader(leaderId);
        }
        return d;
    }

    public ArrayList<Drug> getLeaderOrders(String leaderId) {
        ArrayList<Drug> orders = new ArrayList<>();
        for (Drug d : dMap.values()) {
            if (d.getLeaderCount(leaderId) > 0) {
                Drug leaderDrug = new Drug(d, leaderId);
                orders.add(leaderDrug);
            }
        }
        return orders;
    }

    public LinkedList<Drug> purchaseDrugs(double totalFunds) {
        LinkedList<Drug> buyList = new LinkedList<>();
        while (totalFunds > 0.0) {
            Drug drugToBeBought = getHighestDemandedDrug();
            if (drugToBeBought == null) {
                break;
            }
            int totalCount = drugToBeBought.getTotalCount();
            double totalPrice = drugToBeBought.getTotalPrice();

            if (totalFunds > totalPrice) {
                dMap.remove(drugToBeBought.getAmazonDrugId());
                buyList.add(drugToBeBought);
                totalFunds -= totalPrice;
            } else {
                int buyableCount = (int) ((double) totalFunds / drugToBeBought.getPrice());
                int purchaseCoefficient = buyableCount / totalCount;
                // Go through each camp
                for (Map.Entry<String, Integer> entry : drugToBeBought.getAmountOrdered().entrySet())
                {
                    int newCount = purchaseCoefficient * entry.getValue();
                    if (newCount == 0) { continue; }

                    String leaderId = entry.getKey();
                    // Just get leader
                    Drug drugOrder = new Drug(drugToBeBought, leaderId);
                    // Set order
                    drugOrder.setCount(leaderId, newCount);
                    // Add it to buy list
                    buyList.add(drugOrder);
                    // Decrease purchased amount
                    drugToBeBought.decreaseCount(leaderId, newCount);
                    // Reduce amount used from funds
                    totalFunds -= newCount * drugToBeBought.getPrice();
                }
                break;
            }
        }
        return buyList;
    }

    private Drug getHighestDemandedDrug() {
        int maxDemand = 0;
        Drug maxDemandedDrug = null;
        for (Drug d : dMap.values()) {
            if (d.getTotalCount() > maxDemand) {
                maxDemand = d.getTotalCount();
                maxDemandedDrug = d;
            }
        }
        return maxDemandedDrug;
    }
}
