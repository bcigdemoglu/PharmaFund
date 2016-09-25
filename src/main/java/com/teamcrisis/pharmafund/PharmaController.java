package com.teamcrisis.pharmafund;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import static spark.Spark.*;

/**
 * Controller.
 */
class PharmaController {

    private static final String API_CONTEXT = "/pharma/api";

    private final PharmaService pharmaService;

    private final Logger logger = LoggerFactory.getLogger(PharmaController.class);

    public PharmaController(PharmaService pharmaService) {
        this.pharmaService = pharmaService;
        setupEndpoints();
    }

    private void setupEndpoints() {
        post(API_CONTEXT + "/leader", "application/json", (request, response) -> {
            response.status(201);
            return pharmaService.createNewLeader(request.body());
        }, new JsonTransformer());

        put(API_CONTEXT + "/leader/:leaderId/addDrug", "application/json", (request, response) -> {
            try {
                response.status(200);
                return pharmaService.addDrug(request.params(":leaderId"), request.body());
            } catch (PharmaService.PharmaServiceException ex) {
                logger.error(ex.getMessage());
                response.status(Integer.parseInt(ex.getMessage().split(" ")[0]));
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());

        put(API_CONTEXT + "/leader/:leaderId/removeDrug", "application/json", (request, response) -> {
            try {
                response.status(200);
                return pharmaService.removeDrug(request.params(":leaderId"), request.body());
            } catch (PharmaService.PharmaServiceException ex) {
                logger.error(ex.getMessage());
                response.status(Integer.parseInt(ex.getMessage().split(" ")[0]));
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());

        get(API_CONTEXT + "/leader/:leaderId/displayCatalog", "application/json", (request, response) -> {
            try {
                response.status(200);
                return pharmaService.getAllOrders(request.params(":leaderId"));
            } catch (PharmaService.PharmaServiceException ex) {
                logger.error(ex.getMessage());
                response.status(Integer.parseInt(ex.getMessage().split(" ")[0]));
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());

        get(API_CONTEXT + "/leader/:leaderId/displayLeaderOrders", "application/json", (request, response) -> {
            try {
                response.status(200);
                return pharmaService.getLeaderOrders(request.params(":leaderId"));
            } catch (PharmaService.PharmaServiceException ex) {
                logger.error(ex.getMessage());
                response.status(Integer.parseInt(ex.getMessage().split(" ")[0]));
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());

        post(API_CONTEXT + "/donate", "application/json", (request, response) -> {
            response.status(200);
            pharmaService.donateToFunds(request.body());
            return pharmaService.useAvailableFunds();
        }, new JsonTransformer());

    }
}
