package com.teamcrisis.pharmafund;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.util.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class AmazonLinkConverter {

   
    private static final String SECRET_KEY = "T/E6UpBZK/QFsSplGy+DS2P/wpDBhHa762MpaQ98";
    private static final String AWS_KEY = "AKIAIB2HBOIAGSDV4AQA";

    public static ArrayList<String> getAmazonData(String address) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        ArrayList<String> idPriceTitle = new ArrayList<>();
        String marker = "/dp/";
        int locationOfMarker = address.indexOf(marker)+3;
        int count = 1;
        String iD = "";
        for(int i=0; i<10; i++){
            iD = iD + address.charAt(locationOfMarker+count);
            count++;
        }
      
        SignedRequestsHelper helper = SignedRequestsHelper.getInstance("ecs.amazonaws.com", AWS_KEY, SECRET_KEY);

        Map<String, String> params = new HashMap<String, String>();
        params.put("Operation", "ItemLookup");
        params.put("Service", "AWSECommerceService");
        params.put("Version", "2009-03-31");
        params.put("ItemId", iD);
        params.put("ResponseGroup", "Medium");
        params.put("AssociateTag",iD);

        String url = helper.sign(params);
        try {
            Document response = getResponse(url);
            idPriceTitle.add(iD);
            idPriceTitle.addAll(printResponse(response));
        } catch (Exception ex) { }
        return idPriceTitle;
    }

    public static Double getPriceFromId(String iD) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {


        SignedRequestsHelper helper = SignedRequestsHelper.getInstance("ecs.amazonaws.com", AWS_KEY, SECRET_KEY);

        Map<String, String> params = new HashMap<String, String>();
        params.put("Operation", "ItemLookup");
        params.put("Service", "AWSECommerceService");
        params.put("Version", "2009-03-31");
        params.put("ItemId", iD);
        params.put("ResponseGroup", "OfferSummary");
        params.put("AssociateTag",iD);

        String url = helper.sign(params);
        try {
            Document response = getResponse(url);
            return Double.parseDouble(printResponse(response).get(0));
        } catch (Exception ex) { }
        return 0.0;
    }

    public static String getTitleFromId(String iD) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {

        SignedRequestsHelper helper = SignedRequestsHelper.getInstance("ecs.amazonaws.com", AWS_KEY, SECRET_KEY);

        Map<String, String> params = new HashMap<String, String>();
        params.put("Operation", "ItemLookup");
        params.put("Service", "AWSECommerceService");
        params.put("Version", "2009-03-31");
        params.put("ItemId", iD);
        params.put("ResponseGroup", "OfferSummary");
        params.put("AssociateTag",iD);

        String url = helper.sign(params);
        try {
            Document response = getResponse(url);
            return printResponse(response).get(1);
        } catch (Exception ex) { }
        return "";
    }



    private static Document getResponse(String url) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(url);
        return doc;
    }

    private static ArrayList<String> printResponse(Document doc) throws TransformerException, FileNotFoundException {
        ArrayList<String> priceTitle = new ArrayList<>();
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        Properties props = new Properties();
        props.put(OutputKeys.INDENT, "yes");
        trans.setOutputProperties(props);
        StreamResult res = new StreamResult(new StringWriter());
        DOMSource src = new DOMSource(doc);
        trans.transform(src, res);
        String toString = res.getWriter().toString();
        
        int fixedLocation = toString.indexOf("FormattedPrice");
            //fixedLocation = toString.indexOf("FormattedPrice");
        //System.out.println("Fixed Index=" + fixedLocation);
        char search = 0;
        int move = 1;
        String price = "";
        fixedLocation = fixedLocation+15;
        while(search != '.'){
             search = toString.charAt(fixedLocation+move);
             move++;
             price = price + search; 
        }
               
        for(int i=0; i<2; i++){
             search = toString.charAt(fixedLocation+move);
             move++;
             price = price + search;
        }
        System.out.println("Price: $"+price);

        fixedLocation = toString.indexOf("<Title>");
        //System.out.println("Fixed Index=" + fixedLocation);
        search = 0;
        move = 0;
        String title = "";
        fixedLocation = fixedLocation+6;
        while(search != '<'){
             move++;
             search = toString.charAt(fixedLocation+move);
             title = title + search;

        }
        title = title.substring(0,title.length()-1);

        System.out.println("Title: "+ title);

        priceTitle.add(price);
        priceTitle.add(title);
    
        return priceTitle;
    }
     
}