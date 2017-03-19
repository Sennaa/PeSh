/*
 * Decompiled with CFR 0_118.
 */
package si.personalshopper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import si.personalshopper.MainRecommender;
import si.personalshopper.data.Shop;
import si.personalshopper.global.GlobalClass;

public class Main {
    private static final String composite = "composite";
    private static final String contentbased = "contentbased";
    private static final String random = "random";
    private MainRecommender compositeRecommender;
    private MainRecommender contentbasedRecommender;
    private MainRecommender randomRecommender;
    private String firstRecommender;
    private String secondRecommender;
    private String secondOrRightRecommender;
    private ArrayList<String> first;
    private ArrayList<String> second;
    private int coldStartIters;
    private int recommendersIters;
    private ArrayList<String> combinedRecommendedShops = new ArrayList();
    private ArrayList<String> allShopNames;
    private String recommendation1File;
    private String recommendation2File;
    private String sID;
    private String personaName;

    public Main(String sID) {
        this.sID = sID;
        this.compositeRecommender = new MainRecommender("composite");
        this.contentbasedRecommender = new MainRecommender("contentbased");
        this.randomRecommender = new MainRecommender("random");
        this.coldStartIters = 1;
        this.recommendersIters = 1;
        this.startMain();
    }

    public String getSessionID() {
        return this.sID;
    }

    public void startMain() {
        this.compositeRecommender.startMain(this.sID);
        this.contentbasedRecommender.startMain(this.sID);
        this.randomRecommender.startMain(this.sID);
        this.allShopNames = new ArrayList();
        for (Shop shop : MainRecommender.globalClass.getAllShops()) {
            this.allShopNames.add(shop.getName());
        }
    }

    public String getPersonaHTML() {
        return this.compositeRecommender.getPersonaHTML();
    }

    public void setPersona(String personaName) {
        this.compositeRecommender.setPersona(personaName, this.sID);
        this.contentbasedRecommender.setPersona(personaName, this.sID);
        this.randomRecommender.setPersona(personaName, this.sID);
        this.personaName = personaName;
    }

    public String getPersonaName() {
        return this.personaName;
    }

    public int calculateRouteAndShopSize(String leftRecommender, String rightRecommender) {
        int leftShopSize = -1;
        int rightShopSize = -1;
        if (leftRecommender.equals("composite")) {
            this.compositeRecommender.calculateRoute();
            leftShopSize = this.compositeRecommender.getShopSize();
            return leftShopSize;
        }
        if (leftRecommender.equals("contentbased")) {
            this.contentbasedRecommender.calculateRoute();
            leftShopSize = this.contentbasedRecommender.getShopSize();
        } else if (leftRecommender.equals("random")) {
            this.randomRecommender.calculateRoute();
            leftShopSize = this.randomRecommender.getShopSize();
        } else {
            System.out.println("Shouldn't come here, wrong recommender string!");
        }
        if (rightRecommender.equals("composite")) {
            this.compositeRecommender.calculateRoute();
            rightShopSize = this.compositeRecommender.getShopSize();
            return rightShopSize;
        }
        if (rightRecommender.equals("contentbased")) {
            this.contentbasedRecommender.calculateRoute();
            rightShopSize = this.contentbasedRecommender.getShopSize();
        } else if (rightRecommender.equals("random")) {
            this.randomRecommender.calculateRoute();
            rightShopSize = this.randomRecommender.getShopSize();
        } else {
            System.out.println("Shouldn't come here, wrong recommender string!");
        }
        return Math.min(leftShopSize, rightShopSize);
    }

    public String getRecommendationHTML(String recommender, String position, int shopSize) {
        if (recommender.equals("composite")) {
            return this.compositeRecommender.getRecommendationHTML(position, shopSize);
        }
        if (recommender.equals("contentbased")) {
            return this.contentbasedRecommender.getRecommendationHTML(position, shopSize);
        }
        if (recommender.equals("random")) {
            return this.randomRecommender.getRecommendationHTML(position, shopSize);
        }
        System.out.println("Should not come here! Recommender string invalid");
        return null;
    }

    public void addFeedback(String shopName) {
        String recommender;
        if (this.first.contains(shopName)) {
            recommender = this.firstRecommender;
            if (recommender.equals("composite")) {
                this.compositeRecommender.addFeedback(shopName);
            } else if (recommender.equals("contentbased")) {
                this.contentbasedRecommender.addFeedback(shopName);
            } else if (recommender.equals("random")) {
                this.randomRecommender.addFeedback(shopName);
            } else {
                System.out.println("Should not come here! Recommender string invalid");
            }
        }
        if (this.second.contains(shopName)) {
            recommender = this.secondRecommender;
            if (recommender.equals("composite")) {
                this.compositeRecommender.addFeedback(shopName);
            } else if (recommender.equals("contentbased")) {
                this.contentbasedRecommender.addFeedback(shopName);
            } else if (recommender.equals("random")) {
                this.randomRecommender.addFeedback(shopName);
            } else {
                System.out.println("Should not come here! Recommender string invalid");
            }
        }
    }

    public void confirmRecommendation() {
        if (this.firstRecommender.equals("composite") || this.secondRecommender.equals("composite")) {
            this.compositeRecommender.confirmRecommendation();
        }
        if (this.firstRecommender.equals("contentbased") || this.secondRecommender.equals("contentbased")) {
            this.contentbasedRecommender.confirmRecommendation();
        }
        if (this.firstRecommender.equals("random") || this.secondRecommender.equals("random")) {
            this.randomRecommender.confirmRecommendation();
        }
    }

    public void setTimeBudget(int timeBudget) {
        this.compositeRecommender.setTimeBudget(timeBudget);
        this.contentbasedRecommender.setTimeBudget(timeBudget);
        this.randomRecommender.setTimeBudget(timeBudget);
    }

    public String getCombinedRecommendedShopsHTML() {
        String html = "";
        ArrayList<String> combinedRecommendedShops = this.combineRecommendedShops();
        for (String shop : combinedRecommendedShops) {
            html = html + "<li class=\"list-group-item\">" + shop + "</li>\n";
        }
        return html;
    }

    private ArrayList<String> combineRecommendedShops() {
        ArrayList<String> firstCopy = new ArrayList<String>(this.first);
        ArrayList<String> secondCopy = new ArrayList<String>(this.second);
        firstCopy.removeAll(secondCopy);
        firstCopy.addAll(secondCopy);
        Collections.shuffle(firstCopy, new Random(System.nanoTime()));
        this.combinedRecommendedShops = firstCopy;
        return this.combinedRecommendedShops;
    }

    private ArrayList<String> getShops(String recommender) {
        if (recommender.equals("composite")) {
            return this.compositeRecommender.getRecommendedShops();
        }
        if (recommender.equals("contentbased")) {
            return this.contentbasedRecommender.getRecommendedShops();
        }
        if (recommender.equals("random")) {
            return this.randomRecommender.getRecommendedShops();
        }
        System.out.println("Should not come here! Recommender string invalid");
        return null;
    }

    public void setRecommenders(String firstRecommender, String secondRecommender) {
        this.firstRecommender = firstRecommender;
        this.secondRecommender = secondRecommender;
        this.first = this.getShops(firstRecommender);
        this.second = this.getShops(secondRecommender);
    }

    public String getLeftRecommender() {
        return this.firstRecommender;
    }

    public String getRightRecommender() {
        return this.secondRecommender;
    }

    public String getFirstOrLeftRecommender() {
        if (Math.random() >= 0.5) {
            this.secondOrRightRecommender = "contentbased";
            return "composite";
        }
        this.secondOrRightRecommender = "composite";
        return "contentbased";
    }

    public void addIteration() {
        if (this.firstRecommender.equals("random") || this.secondRecommender.equals("random")) {
            ++this.coldStartIters;
        } else {
            ++this.recommendersIters;
        }
    }

    public String getSecondOrRightRecommender() {
        return this.secondOrRightRecommender;
    }

    public int getRecommendersIters() {
        return this.recommendersIters;
    }

    public int getColdStartIters() {
        return this.coldStartIters;
    }

    private int getRouteLength(String recommender) {
        if (recommender.equals("composite")) {
            return this.compositeRecommender.getRouteTime();
        }
        if (recommender.equals("contentbased")) {
            return this.contentbasedRecommender.getRouteTime();
        }
        if (recommender.equals("random")) {
            return this.randomRecommender.getRouteTime();
        }
        System.out.println("Should not come here! Recommender string invalid");
        return -1;
    }

    public void saveColdStartPrefRec(String userID, String persona, String recLeftName, String recRightName, String prefRecName) {
        String path = "C:/Users/Senna/Prive/Universiteit/Scriptie/webApp/feedbackData/feedbackColdStartPreferredRecSys.csv";
        BufferedWriter br = this.getFile(path);
        String newLine = "";
        newLine = newLine + userID + "^";
        newLine = newLine + persona + "^";
        newLine = newLine + recLeftName + "^";
        newLine = newLine + this.getRouteLength(recLeftName) + "^";
        newLine = newLine + recRightName + "^";
        newLine = newLine + this.getRouteLength(recRightName) + "^";
        newLine = newLine + prefRecName + "^";
        newLine = newLine + this.getDate();
        try {
            br.newLine();
            br.append(newLine);
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveColdStartPrefShops(String userID, String persona, String recName, String posFeedbackString) {
        String path = "C:/Users/Senna/Prive/Universiteit/Scriptie/webApp/feedbackData/feedbackColdStartPreferredShops.csv";
        BufferedWriter br = this.getFile(path);
        String newLine = "";
        newLine = newLine + userID + "^";
        newLine = newLine + persona + "^";
        newLine = newLine + recName + "^";
        newLine = newLine + this.getRouteLength(recName) + "^";
        ArrayList shops = new ArrayList();
        if (recName.equals("composite")) {
            shops = this.compositeRecommender.getRecommendedShops();
        } else if (recName.equals("contentbased")) {
            shops = this.contentbasedRecommender.getRecommendedShops();
        } else if (recName.equals("random")) {
            shops = this.randomRecommender.getRecommendedShops();
        }
        String recShopNames = "";
        String recShopIndices = "";
        for (String shopName : shops) {
            recShopNames = recShopNames + shopName + ";";
            recShopIndices = recShopIndices + this.allShopNames.indexOf(shopName) + ";";
        }
        if (recShopNames.length() > 0) {
            recShopNames = recShopNames.substring(0, recShopNames.length() - 1);
            recShopIndices = recShopIndices.substring(0, recShopIndices.length() - 1);
        }
        newLine = newLine + recShopNames + "^";
        newLine = newLine + recShopIndices + "^";
        String currentPosFeedback = "";
        String[] posFeedbackList = posFeedbackString.split(";");
        ArrayList<String> newPosFeedbackList = new ArrayList<String>();
        for (String pos : posFeedbackList) {
            if (!shops.contains(pos)) continue;
            currentPosFeedback = currentPosFeedback + pos + ";";
            newPosFeedbackList.add(pos);
        }
        if (currentPosFeedback.length() > 0) {
            currentPosFeedback = currentPosFeedback.substring(0, currentPosFeedback.length() - 1);
        }
        newLine = newLine + currentPosFeedback + "^";
        String negFeedbackString = "";
        for (String shopName2 : shops) {
            boolean isNeg = true;
            for (String pos2 : newPosFeedbackList) {
                if (!pos2.equals(shopName2)) continue;
                isNeg = false;
            }
            if (!isNeg) continue;
            negFeedbackString = negFeedbackString + shopName2 + ";";
        }
        if (negFeedbackString.length() > 0) {
            negFeedbackString = negFeedbackString.substring(0, negFeedbackString.length() - 1);
        }
        newLine = newLine + negFeedbackString + "^";
        newLine = newLine + this.getDate();
        try {
            br.newLine();
            br.append(newLine);
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveIterationsPrefRec(String userID, String persona, String prefRecName) {
        String path = "C:/Users/Senna/Prive/Universiteit/Scriptie/webApp/feedbackData/feedbackIterationsPreferredRecSys.csv";
        BufferedWriter br = this.getFile(path);
        String newLine = "";
        newLine = newLine + userID + "^";
        newLine = newLine + persona + "^";
        newLine = newLine + this.getFirstOrLeftRecommender() + "^";
        newLine = newLine + this.getRouteLength(this.getFirstOrLeftRecommender()) + "^";
        newLine = newLine + this.getSecondOrRightRecommender() + "^";
        newLine = newLine + this.getRouteLength(this.getSecondOrRightRecommender()) + "^";
        newLine = newLine + prefRecName + "^";
        newLine = newLine + this.getRecommendersIters() + "^";
        newLine = newLine + this.getDate();
        try {
            br.newLine();
            br.append(newLine);
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveIterationsPrefShops(String userID, String persona, String recName, String posFeedbackString) {
        String path = "C:/Users/Senna/Prive/Universiteit/Scriptie/webApp/feedbackData/feedbackIterationsPreferredShops.csv";
        BufferedWriter br = this.getFile(path);
        String newLine = "";
        newLine = newLine + userID + "^";
        newLine = newLine + persona + "^";
        newLine = newLine + recName + "^";
        newLine = newLine + this.getRouteLength(recName) + "^";
        ArrayList shops = new ArrayList();
        if (recName.equals("composite")) {
            shops = this.compositeRecommender.getRecommendedShops();
        } else if (recName.equals("contentbased")) {
            shops = this.contentbasedRecommender.getRecommendedShops();
        } else if (recName.equals("random")) {
            shops = this.randomRecommender.getRecommendedShops();
        }
        String recShopNames = "";
        String recShopIndices = "";
        for (String shopName : shops) {
            recShopNames = recShopNames + shopName + ";";
            recShopIndices = recShopIndices + this.allShopNames.indexOf(shopName) + ";";
        }
        if (recShopNames.length() > 0) {
            recShopNames = recShopNames.substring(0, recShopNames.length() - 1);
            recShopIndices = recShopIndices.substring(0, recShopIndices.length() - 1);
        }
        newLine = newLine + recShopNames + "^";
        newLine = newLine + recShopIndices + "^";
        String currentPosFeedback = "";
        String[] posFeedbackList = posFeedbackString.split(";");
        ArrayList<String> newPosFeedbackList = new ArrayList<String>();
        for (String pos : posFeedbackList) {
            if (!shops.contains(pos)) continue;
            currentPosFeedback = currentPosFeedback + pos + ";";
            newPosFeedbackList.add(pos);
        }
        if (currentPosFeedback.length() > 0) {
            currentPosFeedback = currentPosFeedback.substring(0, currentPosFeedback.length() - 1);
        }
        newLine = newLine + currentPosFeedback + "^";
        String negFeedbackString = "";
        for (String shopName2 : shops) {
            boolean isNeg = true;
            for (String pos2 : newPosFeedbackList) {
                if (!pos2.equals(shopName2)) continue;
                isNeg = false;
            }
            if (!isNeg) continue;
            negFeedbackString = negFeedbackString + shopName2 + ";";
        }
        if (negFeedbackString.length() > 0) {
            negFeedbackString = negFeedbackString.substring(0, negFeedbackString.length() - 1);
        }
        newLine = newLine + negFeedbackString;
        newLine = newLine + this.getRecommendersIters() + "^";
        newLine = newLine + this.getDate();
        try {
            br.newLine();
            br.append(newLine);
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    private BufferedWriter getFile(String path) {
        BufferedWriter file = null;
        try {
            file = new BufferedWriter(new FileWriter(path, true));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public void setRecommendation1File(String recommendation1File) {
        this.recommendation1File = recommendation1File;
    }

    public String getRecommendation1File() {
        return this.recommendation1File;
    }

    public void setRecommendation2File(String recommendation2File) {
        this.recommendation2File = recommendation2File;
    }

    public String getRecommendation2File() {
        return this.recommendation2File;
    }

    public static void main(String[] args) {
    }
}

