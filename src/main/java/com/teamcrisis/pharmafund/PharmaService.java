package com.teamcrisis.pharmafund;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;


/**
 * Service model.
 */
public class PharmaService {

    private double totalFunds;
    private Catalog catalog;
    private LinkedList<Drug> shipments;
    private HashMap<String, CampLeader> leaderMap;
    private final Logger logger = LoggerFactory.getLogger(PharmaService.class);

    /**
     * Construct the model.
     */
    public PharmaService() {
        totalFunds = 0.0;
        catalog = new Catalog();
        shipments = new LinkedList<>();
        leaderMap = new HashMap<>();
    }

    public CampLeader createNewLeader(String body) {
        System.out.println(body);
        CampLeader leader = new Gson().fromJson(body, CampLeader.class);
        leaderMap.put(leader.getLeaderId(), leader);
        return leader;
    }

    public Drug addDrug(String leaderId, String body) throws PharmaServiceException,
            UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {

        checkLeader(leaderId);

        DrugTemplate drugTemplate = new Gson().fromJson(body, DrugTemplate.class);
        String amazonDrugLink = drugTemplate.getLink();
        String amazonDrugId = getDrugIdFromAmazon(amazonDrugLink);
        int orderCount = drugTemplate.getCount();

        if (amazonDrugId.equals("")) {
            String msg = "PharmaService.addDrug: Amazon item not found";
            String respCode = "404";
            logger.error(msg);
            throw new PharmaServiceException(respCode + " " + msg);
        }
        catalog.addDrug(leaderId, amazonDrugId, orderCount);
        return catalog.getDrug(amazonDrugId);
    }

    public Drug removeDrug(String leaderId, String body) throws PharmaServiceException,
            UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {

        checkLeader(leaderId);

        DrugTemplate drugTemplate = new Gson().fromJson(body, DrugTemplate.class);
        String amazonDrugLink = drugTemplate.getLink();
        String amazonDrugId = getDrugIdFromAmazon(amazonDrugLink);
        int removeCount = drugTemplate.getCount();

        Drug d = catalog.getDrug(amazonDrugId);
        if (d == null) {
            String msg = "PharmaService.checkDrugInSet: Drug not in list";
            String respCode = "404";
            logger.error(msg);
            throw new PharmaServiceException(respCode + " " + msg);
        }

        int drugCountInSet = d.getLeaderCount(leaderId);
        if (removeCount > drugCountInSet) {
            String msg = "PharmaService.checkDrugInSet: Drug count in set is " + Integer.toString(drugCountInSet) + ".";
            String respCode = "404";
            logger.error(msg);
            throw new PharmaServiceException(respCode + " " + msg);
        }

        return catalog.removeDrug(leaderId, amazonDrugId, removeCount);
    }

    public Catalog getAllOrders(String leaderId) throws PharmaServiceException {
        checkLeader(leaderId);

        return catalog;
    }

    public ArrayList<Drug> getLeaderOrders(String leaderId) throws PharmaServiceException {
        checkLeader(leaderId);

        return catalog.getLeaderOrders(leaderId);
    }

    public LinkedList<Drug> getUpcomingShipments(String leaderId) throws PharmaServiceException {
        checkLeader(leaderId);

        return shipments;
    }

    public HashMap<String, Double> useAvailableFunds() {
        // Get a list of drugs to be purchased
        LinkedList<Drug> drugsPurchased = catalog.purchaseDrugs(totalFunds);

        // Reduce the price of purchased drugs from total funds
        for (Drug d : drugsPurchased) {
            totalFunds -= d.getTotalPrice();
        }
        shipments.addAll(drugsPurchased);
        HashMap<String, Double> funds = new HashMap<>();
        funds.put("totalFunds", totalFunds);
        return funds;
    }

    public void donateToFunds(String body) {
        Donation donation = new Gson().fromJson(body, Donation.class);
        totalFunds += donation.getAmount();
    }

    // public void displayShipmentsOnMap() { }

    //-----------------------------------------------------------------------------//
    // Helper Classes and Methods
    //-----------------------------------------------------------------------------//

    private boolean checkLeader(String leaderId) throws PharmaServiceException {
        if (!leaderMap.containsKey(leaderId)) {
            String msg = "PharmaService.checkDrugInSet: Drug not in list";
            String respCode = "404";
            logger.error(msg);
            throw new PharmaServiceException(respCode + " " + msg);
        }
        return true;
    }

    /**
     * Thrown when a request from database is invalid
     */
    public static class PharmaServiceException extends Exception {
        public PharmaServiceException(String message) {
            super(message);
        }
    }

    /**
     * Given the name, checks amazon if the drug exists
     * Uses amazon API
     * @param link Drug link
     * @return Amazon id of the drug if exists, otherwise empty string
     */
    public static String getDrugIdFromAmazon(String link) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        return AmazonLinkConverter.getAmazonData(link).get(0);
    }

    public static double getLowestPriceFromAmazon(String amazonDrugId) throws UnsupportedEncodingException,
            NoSuchAlgorithmException, InvalidKeyException {
        return AmazonLinkConverter.getPriceFromId(amazonDrugId);
    }

    public static String getDrugTitleFromAmazon(String amazonDrugId) throws UnsupportedEncodingException,
            NoSuchAlgorithmException, InvalidKeyException {
        return AmazonLinkConverter.getTitleFromId(amazonDrugId);
    }

}
