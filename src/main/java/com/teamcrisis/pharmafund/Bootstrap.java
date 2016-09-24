//-------------------------------------------------------------------------------------------------------------//
// Code based on a tutorial by Shekhar Gulati of SparkJava at
// https://blog.openshift.com/developing-single-page-web-applications-using-java-8-spark-mongodb-and-angularjs/
//-------------------------------------------------------------------------------------------------------------//


package com.teamcrisis.pharmafund;

import static spark.Spark.*;

public class Bootstrap {
    public static final String IP_ADDRESS = "localhost";
    public static final int PORT = 8080;

    public static void main(String[] args) throws Exception {

        //Specify the IP address and Port at which the server should be run

        int portValue = getHerokuAssignedPort();
        if (portValue == PORT) { ipAddress(IP_ADDRESS); }
        port(portValue);

        //Specify the sub-directory from which to serve static resources (like html and css)
        staticFileLocation("/public");

        //Create the model instance and then configure and start the web service
        PharmaService model = new PharmaService();
        new PharmaController(model);
    }

    private static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return PORT; //return default port if heroku-port isn't set (i.e. on localhost)
    }

}
