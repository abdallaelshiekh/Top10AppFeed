package com.abdalla.android.applefeed;

import android.content.res.XmlResourceParser;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by abdalla on 4/13/2017.
 */

public class ParseApplications {
    private static final String TAG = "ParseApplications";
    private ArrayList<FeedEntry> applications;

    public ParseApplications() {
        this.applications = new ArrayList<>();
    }

    public ArrayList<FeedEntry> getApplications() {
        return applications;
    }
    public boolean parse(String xmlData){
        boolean status = true;
        FeedEntry currentEntry = null;
        boolean inEntry = false;
        boolean getImage = false;
        String textValue = "";
        try {
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            xmlPullParserFactory.setNamespaceAware(true);
            XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            while (eventType != xmlPullParser.END_DOCUMENT){
                String tagName = xmlPullParser.getName();
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        Log.d(TAG, "parse: Starting Tag :" + tagName);
                        if ("entry".equalsIgnoreCase(tagName)){
                            currentEntry = new FeedEntry();
                            inEntry = true;
                        }else if("image".equalsIgnoreCase(tagName) && inEntry){
                            String imageResolution = xmlPullParser.getAttributeValue(null,"height");
                            if(imageResolution != null){
                                getImage = "53".equalsIgnoreCase(imageResolution);
                            }
                        }
                        break;
                    case XmlPullParser.TEXT:
                        textValue = xmlPullParser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        Log.d(TAG, "parse: Ending Tag " + tagName);
                        if (inEntry){
                            if ("entry".equalsIgnoreCase(tagName)){
                                inEntry = false;
                                applications.add(currentEntry);
                            }else if ("name".equalsIgnoreCase(tagName)){
                                currentEntry.setName(textValue);

                            }else if ("artist".equalsIgnoreCase(tagName)){
                                currentEntry.setArtist(textValue);
                            }else if ("releaseDate".equalsIgnoreCase(tagName)){
                                currentEntry.setReleaseDate(textValue);
                            }else if("summary".equalsIgnoreCase(tagName)){
                                currentEntry.setSummary(textValue);
                            }else if("image".equalsIgnoreCase(tagName)){
                                if(getImage) {
                                    currentEntry.setImageURL(textValue);
                                }
                            }
                        }
                        break;
                    default:
                        // Take Cafe;
                }

                eventType = xmlPullParser.next();
            }
            for(FeedEntry app : applications){
                Log.d(TAG, "parse: *******************");
                Log.d(TAG, "parse: " + app.toString());
            }

        }catch (Exception e){
            status = false;
            e.printStackTrace();
        }
        return status;
          }
}
