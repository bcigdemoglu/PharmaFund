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
        post(API_CONTEXT + "/leader/", "application/json", (request, response) -> {
            response.status(201);
            return pharmaService.createNewLeader(request.body());
        }, new JsonTransformer());

        post(API_CONTEXT + "/leader/:leaderId/addDrug", "application/json", (request, response) -> {
            try {
                response.status(200);
                return pharmaService.addDrug(request.params(":gameId"), request.body());
            } catch (PharmaService.PharmaServiceException ex) {
                logger.error(ex.getMessage());
                response.status(Integer.parseInt(ex.getMessage().split(" ")[0]));
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());


















        put(API_CONTEXT + "/games/:gameId", "application/json", (request, response) -> {
            try {
                response.status(200);
                return boardService.joinGame(request.params(":gameId"));
            } catch (BoardService.BoardServiceException ex) {
                logger.error(ex.getMessage());
                response.status(Integer.parseInt(ex.getMessage().split(" ")[0]));
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());

        post(API_CONTEXT + "/games/:gameId/hmove", "application/json", (request, response) -> {
            try {
                response.status(200);
                boardService.hmove(request.params(":gameId"), request.body());
            } catch (BoardService.BoardServiceException ex) {
                logger.error(ex.getMessage());
                response.status(Integer.parseInt(ex.getMessage().split(" ")[0]));
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

        post(API_CONTEXT + "/games/:gameId/vmove", "application/json", (request, response) -> {
            try {
                response.status(200);
                boardService.vmove(request.params(":gameId"), request.body());
            } catch (BoardService.BoardServiceException ex) {
                logger.error(ex.getMessage());
                response.status(Integer.parseInt(ex.getMessage().split(" ")[0]));
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

        get(API_CONTEXT + "/games/:gameId/board", "application/json", (request, response) -> {
            try {
                response.status(200);
                return boardService.getBoard(request.params(":gameId"));
            } catch (BoardService.BoardServiceException ex) {
                logger.error(ex.getMessage());
                response.status(Integer.parseInt(ex.getMessage().split(" ")[0]));
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());

        get(API_CONTEXT + "/games/:gameId/state", "application/json", (request, response) -> {
            try {
                response.status(200);
                return boardService.getState(request.params(":gameId"));
            } catch (BoardService.BoardServiceException ex) {
                logger.error(ex.getMessage());
                response.status(Integer.parseInt(ex.getMessage().split(" ")[0]));
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());
    }
}
