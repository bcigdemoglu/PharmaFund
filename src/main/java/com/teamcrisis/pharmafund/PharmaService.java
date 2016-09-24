package com.teamcrisis.pharmafund;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.awt.image.ImageWatched;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;


/**
 * Service.
 */
public class PharmaService {

    double totalFunds;
    Catalog catalog;
    LinkedList<Drug> shipments;
    private final Logger logger = LoggerFactory.getLogger(PharmaService.class);

    /**
     * Construct the model by creating a map of gameids and boards.
     */
    public PharmaService() {
        double totalFunds = 0.0;
        Catalog catalog = new Catalog();
    }

    public CampLeader createNewLeader(String body) {
        CampLeader leader = new Gson().fromJson(body, CampLeader.class);
        // String leaderId = UUID.randomUUID().toString();
    }

    public void addDrug(String leaderId, String name, int count) throws PharmaServiceException {
        String amazonDrugId = getDrugIdFromAmazon("name");

        if (amazonDrugId.equals("")) {
            String msg = "PharmaService.addDrug: Amazon item not found";
            String respCode = "404";
            logger.error(msg);
            throw new PharmaServiceException(respCode + " " + msg);
        }

        catalog.addToSet(leaderId, amazonDrugId, count);
    }

    public void removeDrug(String leaderId, String body) throws PharmaServiceException {
        DrugTemplate drugTemplate = new Gson().fromJson(body, DrugTemplate.class);
        String amazonDrugId = drugTemplate.getAmazonDrugId();
        int removeCount = drugTemplate.getCount();

        Drug d = catalog.getDrugFromSet(leaderId, new Drug(amazonDrugId));
        if (d == null) {
            String msg = "PharmaService.checkDrugInSet: Drug not in list";
            String respCode = "404";
            logger.error(msg);
            throw new PharmaServiceException(respCode + " " + msg);
        }

        int drugCountInSet = d.getLeaderOrder(leaderId);
        if (removeCount > drugCountInSet) {
            String msg = "PharmaService.checkDrugInSet: Drug count in set is " + Integer.toString(drugCountInSet) + ".";
            String respCode = "404";
            logger.error(msg);
            throw new PharmaServiceException(respCode + " " + msg);
        }
        catalog.removeFromSet(leaderId, amazonDrugId, removeCount);
    }

    /**
     * Given the name, checks amazon if the drug exists
     * Uses amazon API
     * @param name Drug
     * @return Amazon id of the drug if exists, otherwise empty string
     */
    public String getDrugIdFromAmazon(String name) {

    }

    public Catalog getAllOrders() {
        return catalog;
    }

    public Catalog getLeaderOrders(String leaderId) {
        return catalog.getLeaderOrders();
    }

    public LinkedList<Drug> getUpcomingShipments() {
        return shipments;
    }

    public void useAvailableFunds() {
        // Get a list of drugs to be purchased
        LinkedList<Drug> drugsPurchased = catalog.purchaseDrugs(totalFunds);

        // Reduce the price of purchased drugs from total funds
        for (Drug d : drugsPurchased) {
            totalFunds -= d.getTotalPrice();
        }
        shipments.addAll(drugsPurchased);
    }

    public int donateToFunds(double donation) {
        totalFunds += donation;
    }

    // public void displayShipmentsOnMap() { }

    //-----------------------------------------------------------------------------//
    // Helper Classes and Methods
    //-----------------------------------------------------------------------------//

    /**
     * Thrown when a request from database is invalid
     */
    public static class PharmaServiceException extends Exception {
        public PharmaServiceException(String message) {
            super(message);
        }
    }

}
