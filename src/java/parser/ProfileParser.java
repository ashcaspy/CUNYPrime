package parser;

import org.apache.pdfbox.cos.COSDocument;

/**
 * Created by gracie on 9/27/2015.
 */

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import java.util.HashMap;
import java.util.Map;


public class ProfileParser {

    /***
     * A function that returns whether the first character in a String is contains a digit
     * @param param, A String that check if the first character is a digit.
     * @return A boolean indicating whether the first character in a string contains a digit.
     */
    public static boolean isFirstCharDigit(String param) {
        final int zero = 0;
        param = param.replaceAll(" ", "");
        if (Character.isDigit(param.charAt(zero))) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * A function that returns the department of a course given a string containing a course.
     * @param param, A string that contains a list of courses, either with the department (eg. ENGL 2100) or just a course number (2100).
     * @return A string containing the department of a course, or if it the string passed in is just a course number it will return an empty string.
     */
    public static String getDept(String param) {
        String theDept = "";
        for (int i = 0; i < param.length(); i++) {
            if (!Character.isDigit(param.charAt(i))) {
                theDept += param.charAt(i);
            } else {
                break;
            }
        }
        return theDept;
    }


    /**
     * This function will add a course into a Requirement object.
     *
     * @param myarr,        A string array that that holds the course code (ENGL 21007) or just the course number(21007).
     * @param requirements, An Requirements object that a course would be added to.
     * @param dept,         The department to prepend to a course number if it is missing. The default value is an empty string.
     *                      It gets updated to be previously known department. Eg. ENGL 21007, 21008, 31008, dept would get updated to ENGL
     *                      and get prepended to 21008 and 31008.
     */
    public static String putCourses(String myarr[], Requirements requirements, String dept, boolean dealingWithException, int param) {
        String classCode = "";
        final int numFour = 4;
        for (int j = 0; j < myarr.length; j++) {
            if (myarr[j].equals("")) {

            } else if (isFirstCharDigit(myarr[j].replaceAll(" ", ""))) {
                myarr[j] = myarr[j].replaceAll(" ","");
                if(myarr[j].length() == numFour){
                    if(myarr[j].contains("@")){
                        classCode = dept.concat(myarr[j]);

                    } else {
                        classCode = myarr[j];
                    }
                } else {
                    classCode = dept.concat(myarr[j]);
                }
                if (dealingWithException) {
                    requirements.addToExceptList(classCode);
                } else {
                    requirements.addToList(classCode);
                }

            } else if (!isFirstCharDigit(myarr[j].replaceFirst(" ", ""))) {
                if ((myarr[j].equals("residence = Y ") || myarr[j].equals("= Y ") || myarr[j].equals("Y ")) && param > -1) {
                    int index = requirements.getIndex();
                    String temp = requirements.getIndexedElementFromMyList(index);
                    temp = temp.concat(" " + myarr[j]);

                    requirements.editMyList(temp, index);


                } else {
                    if(dealingWithException){
                        requirements.addToExceptList(myarr[j]);
                        String temp = dept;
                        dept = getDept(myarr[j]);
                        if(dept.contains("FALL") || dept.contains("SPRING") || dept.contains(">") || dept.contains("Term") || dept.equals("<")){
                            dept = temp;
                        }
                    } else {
                        requirements.addToList(myarr[j]);
                        String temp = dept;
                        dept = getDept(myarr[j]);
                        if(dept.contains("FALL") || dept.contains("SPRING") || dept.contains(">") || dept.contains("Term") || dept.equals("<")){
                            dept = temp;
                        }
                    }
                }

            }

        }

        return dept;
    }


    /**
     * A function that checks if a string contains all digits.
     * @param param, A string that is being checked to see if it contains all digits.
     * @return A boolean that indicates whether the passed String, param, contains all digits.
     */
    public static boolean isAllDigit(String param) {
        int counter = 0;
        for (int i = 0; i < param.length(); i++) {
            if (!Character.isDigit(param.charAt(i))) {
                return false;
            } else {
                counter++;
            }
        }
        if (counter == param.length()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * A function that checks to see if all the characters in a given String is in all caps.
     * @param param, A String to be checked to see if all its character is in all caps.
     * @return A boolean that indciates whether the string passed to the function, param, is in all caps.
     */
    public static boolean isAllCaps(String param) {
        for (int i = 0; i < param.length(); i++) {
            if (Character.isLowerCase(param.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    /**
     * A function that returns the first word in a String.
     * @param param, A string, that is passed into the function, to get the first word from.
     * @return A string that holds the first word of the string, param.
     */
    public static String getFirstWord(String param) {
        final int index = 0;
        String temp[] = param.split(" ");
        return temp[index];
    }



    /**
     * A function that returns if the string given potentially contains information that is needed.
     * @param param, A string that is passed into the function to be checked if it contains information needed.
     * @return A boolean indicating whether there is potentially information needed in the string passed in to the function.
     */
    public static boolean isInfoIneed(String param) {
        String temp[] = param.split(" ");
        int len = temp.length - 2;
        if(len > 0) {
            if (temp[len].contains("FALL") || temp[len].contains("SPRING") || temp[len].contains("SUMMER") || temp[len].contains("WINTER")) {
                if (!param.contains("Term")) {
                    return false;
                }
            }
        }
        for (int i = 0; i < temp.length; i++) {

            final int num3 = 3;
            final int num4 = 4;
            final int num5 = 5;
            final int num11= 11;

            if ((temp[i].length() == num5 || temp[i].length() == num3) && isAllDigit(temp[i])) {
                if (i == 0) {
                    return true;
                } else if (temp[i - 1].equals("or")) {
                    return true;
                } else if (temp[i].contains("and")) {
                    return true;
                } else if ((temp[i - 1].length() == num3 || temp[i - 1].length() == num4 || temp[i - 1].length() == num5) && isAllCaps(temp[i - 1])) {
                    return true;
                }
            } else if (temp[i].contains("@") && !param.contains("advisor")) {
                return true;
            } else if (temp[i].length() == num11 && temp[i].contains(":")) {
                return true;
            }
        }
        return false;
    }


    /**
     * A function that parses the information of interest from a pdf or cgi file, and returns the information as a string.
     * @param fileInput, An InputSTream that is the pdf or cgi file to be parsed.
     * @return, A string that contains information parsed from the pdf or cgi file.
     */
    public String parseProfile(InputStream fileInput) {
        String myStr = "";
        InputStream in = fileInput;
        COSDocument cosDoc = null;
        PDDocument pdDoc = null;
        PDFTextStripper textStripper = null;
        try {
            PDFParser myParser = new PDFParser(in);
            myParser.parse();
            cosDoc = myParser.getDocument();
            pdDoc = new PDDocument(cosDoc);
            textStripper = new PDFTextStripper();
            textStripper.setStartPage(1);
            textStripper.setEndPage(pdDoc.getNumberOfPages());
            myStr = textStripper.getText(pdDoc);
            pdDoc.close();
            cosDoc.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        List<Requirements> myRequirements = new ArrayList<>();
        List<ReqCategory> myReqCat = new ArrayList<>();

        String data[] = myStr.split("\\r?\\n");
        String stringy[];
        String courses[];

        final int indexOf1 = 1;
        final int indexOf0 = 0;
        String dept = "";
        boolean lastOne = false;
        boolean dealingWithException = false;
        ReqCategory reqCategory = new ReqCategory();
        Requirements requirements = new Requirements();
        Requirements mySubReq = new Requirements();
        Requirements mySubReq2 = new Requirements();
        String stringOfInt = "";
        boolean hasSubSubby = false;
        boolean hasSub = false;
        boolean easyStyle = false;
        boolean justOneClassInALine = false;
        int counter = -1;
        String catName = "";
        String creditReq = "";
        String creditApp = "";
        boolean needToPutInCat = false;
        boolean needCollege = true;
        String college = "";
        String major = "";
        boolean hasReq = false;
        boolean isNew = true;
        boolean isLastCat = false;
        boolean containsAnd = false;
        boolean tocontinue = false;
        List<String> coursesTaken = new ArrayList<>();
        List<String> withdrawnCourses = new ArrayList<>();
        List<String> npCourses = new ArrayList<>();
        List<String> inProgressCourses = new ArrayList<>();
        List<String> transferCourses = new ArrayList<>();
        Map inProgressMap = new HashMap<>();
        Map trasnferMap = new HashMap<>();

        for (int i = 0; i < data.length; i++) {
            if(data[i].equals("Catalog Year:") || data[i].equals("Academic Year:")|| data[i].contains("Catalog Year:")){
                if(easyStyle) {
                    easyStyle = false;
                    if(dealingWithException){
                        dealingWithException = false;
                    }
                    myRequirements.add(requirements);
                    requirements = new Requirements();
                }

                if(justOneClassInALine){
                    justOneClassInALine = false;
                    lastOne = false;
                    if(!hasSub) {
                        requirements.addToSubList(mySubReq);
                    } else {
                        hasSub = false;
                    }
                    myRequirements.add(requirements);

                    mySubReq = new Requirements();
                    requirements = new Requirements();
                    tocontinue = false;

                }

                if(data[i].equals("Catalog Year:") || data[i].equals("Academic Year:")) {
                    if (!data[i - 1].contains("CUNY Skills") ) {
                        isNew = true;

                        if(needToPutInCat && hasReq) {
                            String temp [] = creditReq.split(":");
                            String temp2 [] = creditApp.split(":");
                            if(!temp[indexOf1].equals(temp2[indexOf1])){

                                reqCategory.setName(catName);
                                reqCategory.setCreditReq(creditReq);
                                reqCategory.setCreditApp(creditApp);

                                reqCategory.setMyList(myRequirements);
                                myReqCat.add(reqCategory);
                            }
                            reqCategory = new ReqCategory();
                            myRequirements = new ArrayList<>();
                            needToPutInCat = false;
                            hasReq = false;
                        }



                        counter++;
                        catName = data[i-1];
                        if(catName.contains("Major")){
                            major = data[i-1];
                        }

                        if (counter >= 0) {
                            needToPutInCat = true;
                        }


                    }
                } else {
                    if(!data[i].contains("Undeclared") && !data[i].contains("CUNY Skills ")) {
                        isNew = true;

                        if(needToPutInCat && hasReq){

                            String temp [] = creditReq.split(":");
                            String temp2 [] = creditApp.split(":");
                            if(!temp[indexOf1].equals(temp2[indexOf1])){
                                reqCategory.setName(catName);
                                reqCategory.setCreditReq(creditReq);
                                reqCategory.setCreditApp(creditApp);
                                reqCategory.setMyList(myRequirements);
                                myReqCat.add(reqCategory);
                            }
                            reqCategory = new ReqCategory();
                            myRequirements = new ArrayList<>();
                            needToPutInCat = false;
                            hasReq = false;

                        }

                        counter++;
                        catName = data[i];
                        if(catName.contains("Major")){
                            major = data[i];
                        }

                        if (counter >= 0) {
                            needToPutInCat = true;
                        }

                    }
                }
            }
            if(isNew) {
                if (data[i].contains("Credits Required")) {
                    creditReq = data[i];


                    hasReq = true;
                }

                if (data[i].contains("Credits Applied") && !data[i].contains("Classes Applied")) {
                    isNew = false;
                    String temp[] = data[i].split("Credits");
                    creditApp = "Credits".concat(temp[indexOf1]);
                }
            }


            if((data[i].contains("FALL") || data[i].contains("Fall")|| data[i].contains("SPRING") || data[i].contains("Spring") || data[i].contains("SUMMER") || data[i].contains("Summer") || data[i].contains("WINTER") || data[i].contains("Winter")|| data[i].contains("IP") || data[i].contains("TRANSFER")) && !data[i].contains("Term")){
                String hold = "";
                boolean infoNeed = false;
                if(data[i].equals("SUMMER") && isInfoIneed(data[i - 1])){
                    hold = data[i - 1];
                    infoNeed = true;
                } else if(data[i].equals("SPRING") && isInfoIneed(data[i-1])){
                    hold = data[i-1];
                    infoNeed = true;
                } else if(data[i].equals("FALL") && isInfoIneed(data[i -1])){
                    hold = data[i -1];
                    infoNeed = true;
                } else if(data[i].equals("WINTER") && isInfoIneed(data[i-1])){
                    hold = data[i-1];
                    infoNeed = true;
                } else {
                    hold = data[i];
                }


                String temp [] = hold.split(" ");
                boolean withdrewFromClass = false;
                boolean isNotCompleted = false;
                boolean isInProgress = false;
                boolean isTransfer = false;
                String output = "";
                boolean start = false;
                if(temp.length > 0){
                    if(data[i].contains("TRANSFER") || temp[temp.length - 2].contains("FALL") || temp[temp.length - 2].contains("Fall")|| temp[temp.length - 2].contains("SPRING") ||temp[temp.length - 2].contains("Spring") || temp[temp.length - 2].contains("SUMMER") || temp[temp.length - 2].contains("Summer")|| temp[temp.length - 2].contains("WINTER") || temp[temp.length - 2].contains("Winter")|| infoNeed){


                        for (int n = 0; n < temp.length; n++){
                            if(temp[n].equals("W")){
                                withdrewFromClass = true;
                            } else if(temp[n].equals("NC")){
                                isNotCompleted = true;
                            }  else if(temp[n].equals("IP")){
                                isInProgress = true;
                            } else if(temp[n].contains("TRANSFER")){
                                isTransfer = true;
                            }
                            if(n < temp.length - 1) {

                                if (isAllCaps(temp[n]) && temp[n].length() > 1 && temp[n + 1].length() == 5) {


                                    start = true;
                                }
                            }
                            if (start){
                                output = output.concat(temp[n] + " ");
                            }
                        }
                        if(!output.isEmpty()) {
                            if (!withdrewFromClass && !isNotCompleted && !isInProgress && !isTransfer) {
                                coursesTaken.add(output);
                            } else if (withdrewFromClass) {
                                withdrawnCourses.add(output);
                            } else if (isNotCompleted) {
                                npCourses.add(output);
                            } else if(isInProgress){
                                String splitOutput [] = output.split(" ");
                                String theKey = splitOutput[indexOf0].concat(splitOutput[indexOf1]);
                                if(!inProgressMap.containsKey(theKey)) {
                                    inProgressCourses.add(output);
                                    inProgressMap.put(theKey, inProgressCourses.size() - 1);
                                }
                            } else if(isTransfer){
                                String splitOutput [] = output.split(" ");
                                String theKey = splitOutput[indexOf0].concat(splitOutput[indexOf1]);
                                if(!trasnferMap.containsKey(theKey)) {
                                    transferCourses.add(output);
                                    trasnferMap.put(theKey, transferCourses.size() - 1);
                                }
                            }
                        }

                    }
                }
            }
            if(data[i].contains("College") && needCollege){
                college = data[i];
                needCollege = false;

            } else if (isInfoIneed(data[i]) || data[i].contains("Choose")) {

                if (data[i].contains("Choose from")) {

                    if(justOneClassInALine){
                        justOneClassInALine = false;
                        lastOne = false;
                        if(!hasSub) {
                            requirements.addToSubList(mySubReq);
                        } else {
                            hasSub = false;
                        }
                        myRequirements.add(requirements);

                        mySubReq = new Requirements();
                        requirements = new Requirements();
                        tocontinue = false;

                    }
                    if(easyStyle) {
                        easyStyle = false;
                        if(dealingWithException){
                            dealingWithException = false;
                        }
                        myRequirements.add(requirements);
                        requirements = new Requirements();
                    }

                    stringy = data[i].split("from ");
                    stringOfInt = getFirstWord(stringy[indexOf1]);
                    if (data[i].contains("(")) {
                        lastOne = false;
                        if (!data[i].contains("OR")) {
                            if(hasSub && !hasSubSubby){
                                requirements.addToSubList(new Requirements());
                                requirements.getElementFromSublist(requirements.getIndexForSubList()).setNumForCategory(Integer.parseInt(stringOfInt));

                            }
                            lastOne = true;


                        } else {
                            hasSub = true;
                            requirements.addToSubList(new Requirements());
                            requirements.getElementFromSublist(requirements.getIndexForSubList()).setNumForCategory(Integer.parseInt(stringOfInt));
                        }
                        if(hasSubSubby){

                            mySubReq.addToSubList(new Requirements());
                            mySubReq.getElementFromSublist(mySubReq.getIndexForSubList()).setNumForCategory(Integer.parseInt(stringOfInt));

                        }
                        if(!hasSub && !hasSubSubby){
                            requirements.setNumForCategory(Integer.parseInt(stringOfInt));
                        }

                    } else {
                        requirements.setNumForCategory(Integer.parseInt(stringOfInt));

                        lastOne = true;
                    }
                } else if (data[i].contains("Class ") || data[i].contains("Classes ") || data[i].contains("Credit")) {

                    containsAnd = false;
                    if(easyStyle) {
                        easyStyle = false;
                        if(dealingWithException){
                            dealingWithException = false;
                        }
                        myRequirements.add(requirements);
                        requirements = new Requirements();
                    }

                    if(justOneClassInALine){
                        justOneClassInALine = false;
                        lastOne = false;
                        if(!hasSub) {
                            requirements.addToSubList(mySubReq);
                        } else {
                            hasSub = false;
                        }
                        myRequirements.add(requirements);

                        mySubReq = new Requirements();
                        requirements = new Requirements();
                        tocontinue = false;

                    }

                    if (data[i].contains("Still Needed: (")) {
                        String[] tempy = data[i].split("Still Needed: \\( ");
                        stringy = tempy[indexOf1].split("in ");
                    } else if(data[i].contains("Still Needed: ")){

                        String[] tempy = data[i].split("Still Needed: ");
                        stringy = tempy[indexOf1].split("in ");
                    } else {
                        stringy = data[i].split("in ");

                    }
                    String hold = "";
                    double num = 0;
                    String temp[] = null;
                    if (data[i].contains("Credits and")) {
                        hold = "Credit ";
                    } else if (data[i].contains("Class") || data[i].contains("Classes ")) {
                        hold = "Class ";
                    } else {
                        hold = "Credit ";
                    }

                    if (data[i].contains("(")) {
                        if (!data[i].contains(")")) {
                            temp = stringy[indexOf0].split("\\( ");
                            num = Double.parseDouble(getFirstWord(temp[indexOf1]));
                            mySubReq2.setC(hold);
                            mySubReq2.setNum(num);
                            if(data[i].contains("Except ")){
                                dealingWithException = true;
                                String spiltExcept[] = stringy[indexOf1].split("Except ");
                                temp = spiltExcept[indexOf1].split("or ");
                                courses = spiltExcept[indexOf0].split("or ");
                                dept = putCourses(courses, mySubReq2, dept, false, mySubReq2.getIndex());
                                dept = putCourses(temp, mySubReq2, dept, dealingWithException, mySubReq2.getIndex());

                            } else {
                                if (stringy[indexOf1].contains("or ")) {
                                    courses = stringy[indexOf1].split("or ");
                                    dept = putCourses(courses, mySubReq2, dept, dealingWithException, mySubReq2.getIndex());
                                } else if (stringy[indexOf1].contains("and ")) {
                                    containsAnd = true;
                                    mySubReq2.setContainsAnd(containsAnd);
                                    courses = stringy[indexOf1].split("and ");
                                    dept = putCourses(courses, mySubReq2, dept, dealingWithException, mySubReq2.getIndex());

                                }
                            }

                        } else {

                            if(data[i].contains("Except ") || dealingWithException == true){
                                dealingWithException = true;
                                String spiltExcept[] = stringy[indexOf1].split("Except ");
                                String exceptClass[] = spiltExcept[indexOf1].split("or ");

                                courses = spiltExcept[indexOf0].split("or ");
                                dept = putCourses(courses, mySubReq2, dept, false, mySubReq2.getIndex());

                                dept = putCourses(exceptClass, mySubReq2, dept, dealingWithException, mySubReq2.getIndex());
                                dealingWithException = false;
                            } else {

                                if (stringy[indexOf1].contains("or ")) {
                                    courses = stringy[indexOf1].split("or ");
                                    dept = putCourses(courses, mySubReq2, dept, dealingWithException, mySubReq2.getIndex());
                                } else if (stringy[indexOf1].contains("and ")) {
                                    containsAnd = true;
                                    mySubReq2.setContainsAnd(containsAnd);
                                    courses = stringy[indexOf1].split("and ");
                                    dept = putCourses(courses, mySubReq2, dept, dealingWithException, mySubReq2.getIndex());

                                } else {
                                    mySubReq2.addToList(stringy[indexOf1]);
                                }
                            }
                            if (data[i].contains(") OR") || data[i].contains(") or") || data[i].contains(") and")) {
                                hasSubSubby = true;
                                num = Double.parseDouble(getFirstWord(stringy[indexOf0]));
                                mySubReq2.setNum(num);
                                mySubReq2.setC(hold);
                                if(hasSubSubby){

                                    if(mySubReq.getIndexForSubList() >= 0) {
                                        mySubReq.getElementFromSublist(mySubReq.getIndexForSubList()).addToSubList(mySubReq2);

                                    } else {
                                        mySubReq.addToSubList(new Requirements());
                                        mySubReq.getElementFromSublist(mySubReq.getIndexForSubList()).addToSubList(mySubReq2);
                                    }
                                    mySubReq2 = new Requirements();


                                }


                            } else {
                                if(hasSubSubby) {
                                    num = Double.parseDouble(getFirstWord(stringy[indexOf0]));
                                    mySubReq2.setNum(num);
                                    mySubReq2.setC(hold);

                                    mySubReq.getElementFromSublist(mySubReq.getIndexForSubList()).addToSubList(mySubReq2);
                                    hasSubSubby = false;

                                    requirements.addToSubList(mySubReq);
                                    myRequirements.add(requirements);
                                    mySubReq = new Requirements();
                                    mySubReq2 = new Requirements();
                                    requirements = new Requirements();
                                }

                            }

                        }

                    } else {
                        if(justOneClassInALine){
                            justOneClassInALine = false;
                            lastOne = false;
                            if(!hasSub) {
                                requirements.addToSubList(mySubReq);
                            } else {
                                hasSub = false;
                            }
                            myRequirements.add(requirements);

                            mySubReq = new Requirements();
                            requirements = new Requirements();
                            tocontinue = false;

                        }
                        requirements = new Requirements();
                        easyStyle = true;

                        num = Double.parseDouble(getFirstWord(stringy[indexOf0]));
                        if(data[i].contains("Except")){
                            //back
                            dealingWithException = true;
                            String spiltExcept[] = stringy[indexOf1].split("Except ");
                            temp = spiltExcept[indexOf1].split("or ");
                            courses = spiltExcept[indexOf0].split("or ");

                            dept = putCourses(courses, requirements, dept, false, requirements.getIndex());
                            dept = putCourses(temp, requirements, dept, dealingWithException, requirements.getIndex());

                        } else {
                            if (stringy[indexOf1].contains("or ")) {
                                courses = stringy[indexOf1].split("or ");
                                dept = putCourses(courses, requirements, dept, dealingWithException, requirements.getIndex());

                            } else if (stringy[indexOf1].contains("and ")) {
                                containsAnd = true;
                                requirements.setContainsAnd(containsAnd);
                                courses = stringy[indexOf1].split("and ");
                                dept = putCourses(courses, requirements, dept, dealingWithException, requirements.getIndex());

                            } else {
                                requirements.addToList(stringy[indexOf1]);
                            }
                        }
                        //}

                        requirements.setNum(num);
                        requirements.setC(hold);
                    }
                } else if (data[i].contains(")") && !data[i].contains("(")) {

                    if (data[i].contains("or ")) {
                        courses = data[i].split("or ");
                        if(!dealingWithException) {
                            dept = putCourses(courses, mySubReq2, dept, dealingWithException, mySubReq2.getIndex());
                        } else {
                            dept = putCourses(courses, mySubReq2, dept, dealingWithException, mySubReq2.getIndex());
                            dealingWithException = false;
                        }

                    } else if (data[i].contains("and ")) {
                        courses = data[i].split("and ");
                        if(!dealingWithException) {
                            dept = putCourses(courses, mySubReq2, dept, dealingWithException, mySubReq2.getIndex());
                        } else {
                            dept = putCourses(courses, mySubReq2, dept, dealingWithException, mySubReq2.getIndex());
                            dealingWithException = false;
                        }

                    } else {
                        if(!dealingWithException) {
                            if(isFirstCharDigit(data[i])){
                                mySubReq2.addToList(dept + data[i]);

                            } else {
                                mySubReq2.addToList(data[i]);
                            }
                        } else if(dealingWithException){
                            mySubReq2.addToExceptList(data[i]);
                        }
                    }

                    if(data[i].contains(") OR") || data[i].contains(") and") || data[i].contains(") or")){
                        hasSubSubby = true;
                        if(hasSubSubby){
                            if (mySubReq.getIndexForSubList() >= 0) {

                                mySubReq.getElementFromSublist(mySubReq.getIndexForSubList()).addToSubList(mySubReq2);

                            } else {

                                mySubReq.addToSubList(new Requirements());
                                mySubReq.getElementFromSublist(mySubReq.getIndexForSubList()).addToSubList(mySubReq2);
                                //mySubReq.addToSubList(mySubReq2);

                                if(data[i].contains(") and")) {
                                    mySubReq.getElementFromSublist(mySubReq.getIndexForSubList()).setcontainsAndSubList(true);
                                }
                            }
                        }
                    } else {
                        if(hasSubSubby){
                            if(hasSub){
                                if(lastOne){

                                    requirements.getElementFromSublist(requirements.getIndexForSubList()).addToSubList(mySubReq);
                                    myRequirements.add(requirements);
                                    requirements = new Requirements();
                                    mySubReq = new Requirements();
                                    hasSub = false;
                                    lastOne = false;
                                }
                            } else {
                                mySubReq.getElementFromSublist(mySubReq.getIndexForSubList()).addToSubList(mySubReq2);
                                requirements.addToSubList(mySubReq);
                                myRequirements.add(requirements);
                                requirements = new Requirements();
                                mySubReq = new Requirements();
                            }
                            hasSubSubby = false;
                        }
                        else {
                            if(!data[i].contains(") OR") && !data[i].contains(") or") && !data[i].contains(") and")){
                                if(hasSub){

                                    requirements.getElementFromSublist(requirements.getIndexForSubList()).addToSubList(mySubReq2);
                                    mySubReq2 = new Requirements();

                                    if(lastOne){

                                        myRequirements.add(requirements);
                                        requirements = new Requirements();
                                        hasSub = false;
                                    }

                                } else{
                                    mySubReq.addToSubList(mySubReq2);
                                    if(lastOne) {
                                        //PROBLEM?
                                        requirements.addToSubList(mySubReq);
                                        mySubReq = new Requirements();
                                        mySubReq2 = new Requirements();
                                        myRequirements.add(requirements);
                                        requirements = new Requirements();
                                    }

                                }

                            }
                        }
                    }

                    mySubReq2 = new Requirements();

                } else {

                    boolean isLineIWant = true;
                    String temp [] = null;


                    if(data[i].contains("Except ")){

                        dealingWithException = true;
                        String spiltExcept[] = data[i].split("Except ");
                        temp = spiltExcept[indexOf1].split("or ");
                        courses = spiltExcept[indexOf0].split("or ");

                        if(!easyStyle) {
                            dept = putCourses(courses, mySubReq2, dept, false, mySubReq2.getIndex());
                            dept = putCourses(temp, mySubReq2, dept, dealingWithException, mySubReq2.getIndex());
                        } else {
                            dept = putCourses(courses, requirements, dept, false, requirements.getIndex());
                            dept = putCourses(temp, requirements, dept, dealingWithException, requirements.getIndex());

                        }

                    } else {
                        if(containsAnd){
                            courses = data[i].split("and ");
                        } else {
                            courses = data[i].split("or ");
                        }

                        if(courses.length == 1){

                            String test [] = courses[indexOf0].split(" ");
                            if(test.length == 2){
                                if(!isAllCaps(test[indexOf0])){
                                    isLineIWant = false;
                                }
                                for (int c = 0; c < test[indexOf1].length();c++){
                                    if(test[indexOf1].charAt(c) != '@'){
                                        if(!Character.isDigit(test[indexOf1].charAt(c))){
                                            isLineIWant = false;
                                        }
                                    }

                                }
                            } else if(test.length == 1){
                                if(!isAllDigit(test[indexOf0])){
                                    isLineIWant = false;
                                }
                                for (int c = 0; c < test[indexOf0].length();c++){
                                    if(test[indexOf0].charAt(c) != '@'){
                                        if(!Character.isDigit(test[indexOf0].charAt(c))){
                                            isLineIWant = false;
                                        }
                                    }

                                }
                            } else {
                                isLineIWant = false;
                            }


                        } else {


                            if (!data[i].contains("residence") && !data[i].contains("Term") ) {
                                String testy [] = null;

                                for (int g = 0; g < courses.length; g++) {
                                    if (courses[g].contains(" or") || courses[g].contains("and ")) {
                                        testy = courses[g].split(" ");
                                        if(testy.length == 2){
                                            if(!courses[g].contains(":")) {
                                                for (int c = 0; c < testy[indexOf0].length();c++){
                                                    if(testy[indexOf0].charAt(c) != '@'){
                                                        if(!Character.isDigit(testy[indexOf0].charAt(c))){
                                                            isLineIWant = false;
                                                        }
                                                    }

                                                }
                                            }
                                        } else if (testy.length == 3){
                                            if(!courses[g].contains(":")) {

                                                if (!isAllCaps(testy[indexOf0])) {
                                                    isLineIWant = false;
                                                }
                                                for (int c = 0; c < testy[indexOf1].length();c++){
                                                    if(testy[indexOf1].charAt(c) != '@'){
                                                        if(!Character.isDigit(testy[indexOf1].charAt(c))){
                                                            isLineIWant = false;
                                                        }
                                                    }

                                                }
                                            }
                                        }


                                    } else {

                                        for (int m = 0; m < courses[g].length(); m++) {
                                            if (Character.isAlphabetic(courses[g].charAt(m)) && Character.isLowerCase(courses[g].charAt(m))) {
                                                isLineIWant = false;
                                            }
                                        }

                                    }
                                }
                            }
                        }
                        //}


                        if(data[i].contains("Still Needed")){
                            isLineIWant = false;
                        }
                        if (isLineIWant) {
                            if(easyStyle){
                                if(dealingWithException && !data[i].contains("Except")){
                                    courses = data[i].split("or ");
                                    dept = putCourses(courses, requirements, dept, dealingWithException, requirements.getIndex());
                                } else if(!dealingWithException){
                                    dept = putCourses(courses, requirements, dept, dealingWithException, requirements.getIndex());
                                }

                            } else {

                                if(dealingWithException && !data[i].contains("Except")){
                                    dept = putCourses(courses, mySubReq2, dept, false, mySubReq2.getIndex());
                                    dept = putCourses(temp, mySubReq2, dept, dealingWithException, mySubReq2.getIndex());
                                } else if(!dealingWithException){
                                    dept = putCourses(courses, mySubReq2, dept, false, mySubReq2.getIndex());
                                }
                                if(!hasSub && !hasSubSubby){
                                    if((!data[i - 1].contains("or ") && !data[i].contains("Class") && data[i-1].contains("Choose") && !data[i].contains("Credit")) || tocontinue){

                                        mySubReq.addToSubList(mySubReq2);
                                        mySubReq2 = new Requirements();
                                        justOneClassInALine = true;
                                        tocontinue = true;

                                    }
                                } else if(hasSub){
                                    if(!data[i - 1].contains("or ") && !data[i].contains("Class") && !data[i].contains("Credit") ){
                                        requirements.getElementFromSublist(requirements.getIndexForSubList()).addToList(data[i]);
                                        justOneClassInALine = true;


                                    }
                                }
                            }
                        }
                    }



                }
            } else if (data[i].contains(")") && isInfoIneed(data[i - 1]) && !isInfoIneed(data[i]) && !data[i].contains("Catalog")) {


                if(data[i].contains(") OR") || data[i].contains(") and") || data[i].contains(") or")){
                    hasSubSubby = true;
                    if(hasSubSubby){
                        if (mySubReq.getIndexForSubList() >= 0) {
                            mySubReq.getElementFromSublist(mySubReq.getIndexForSubList()).addToSubList(mySubReq2);

                        } else {

                            mySubReq.addToSubList(new Requirements());
                            mySubReq.getElementFromSublist(mySubReq.getIndexForSubList()).setcontainsAndSubList(true);

                            mySubReq.getElementFromSublist(mySubReq.getIndexForSubList()).addToSubList(mySubReq2);
                        }
                    }
                } else {
                    if(hasSubSubby){
                        if(hasSub){
                            if(lastOne){

                                mySubReq.getElementFromSublist(mySubReq.getIndexForSubList()).addToSubList(mySubReq2);

                                requirements.getElementFromSublist(requirements.getIndexForSubList()).addToSubList(mySubReq);
                                myRequirements.add(requirements);
                                requirements = new Requirements();
                                mySubReq = new Requirements();
                                hasSub = false;
                            }
                        } else {
                            mySubReq.getElementFromSublist(mySubReq.getIndexForSubList()).addToSubList(mySubReq2);
                            requirements.addToSubList(mySubReq);
                            myRequirements.add(requirements);
                            requirements = new Requirements();
                            mySubReq = new Requirements();
                        }
                        hasSubSubby = false;
                        mySubReq2 = new Requirements();
                    } else {
                        if (hasSub) {

                            requirements.getElementFromSublist(requirements.getIndexForSubList()).addToSubList(mySubReq2);
                            mySubReq2 = new Requirements();

                            if (lastOne) {
                                myRequirements.add(requirements);
                                requirements = new Requirements();
                                hasSub = false;
                            }

                        }
                    }
                }

            } else if(data[i].contains("Disclaimer")){

                if (easyStyle){
                    myRequirements.add(requirements);
                }
                if(justOneClassInALine){
                    justOneClassInALine = false;
                    lastOne = false;
                    if(!hasSub) {
                        requirements.addToSubList(mySubReq);
                    } else {
                        hasSub = false;
                    }
                    myRequirements.add(requirements);

                    mySubReq = new Requirements();
                    requirements = new Requirements();
                    tocontinue = false;

                }
                if(!isLastCat) {
                    needToPutInCat = true;
                    if (needToPutInCat) {
                        reqCategory.setName(catName);
                        reqCategory.setCreditReq(creditReq);
                        reqCategory.setCreditApp(creditApp);
                        reqCategory.setMyList(myRequirements);
                        myReqCat.add(reqCategory);
                        reqCategory = new ReqCategory();
                        myRequirements = new ArrayList<>();
                        needToPutInCat = false;
                    }
                    isLastCat = true;
                }
            }
        }

        ByteArrayOutputStream required = new ByteArrayOutputStream();
        String parsedOutput = "";
        PrintStream ps = new PrintStream(required);

        PrintStream old = System.out;

        System.setOut(ps);

        System.out.println(college);
        parsedOutput = parsedOutput + college + "\r\n";

        if(!major.equals("")){
            System.out.println(major);
            parsedOutput = parsedOutput + major + "\r\n";
        }
        for(int j = 0; j < myReqCat.size(); j++){
            myReqCat.get(j).print();

        }





        if(!coursesTaken.isEmpty()) {
            System.out.println("Courses Taken:");
            parsedOutput = parsedOutput + "Courses Taken:" + "\r\n";
            for (int i = 0; i < coursesTaken.size(); i++) {
                System.out.println(coursesTaken.get(i));
                parsedOutput = parsedOutput + coursesTaken.get(i) + "\r\n";

            }
        }



        if(!inProgressCourses.isEmpty()) {
            System.out.println("Courses In-Progress:");
            parsedOutput = parsedOutput + "Courses In-Progress:" + "\r\n";
            for (int i = 0; i < inProgressCourses.size(); i++) {
                System.out.println(inProgressCourses.get(i));
                parsedOutput = parsedOutput + inProgressCourses.get(i) + "\r\n";
            }
        }




        if(!withdrawnCourses.isEmpty()) {
            System.out.println("Courses withdrawal:");
            parsedOutput = parsedOutput + "Courses withdrawal:" + "\r\n";
            for (int i = 0; i < withdrawnCourses.size(); i++) {
                System.out.println(withdrawnCourses.get(i));
                parsedOutput = parsedOutput + withdrawnCourses.get(i) + "\r\n";

            }
        }


        if(!npCourses.isEmpty()) {
            System.out.println("Courses not-completed:");
            parsedOutput = parsedOutput + "Courses not-completed:" + "\r\n";
            for (int i = 0; i < npCourses.size(); i++) {
                System.out.println(npCourses.get(i));
                parsedOutput = parsedOutput + npCourses.get(i) + "\r\n";

            }
        }

        if(!transferCourses.isEmpty()) {
            System.out.println("Courses transfer:");
            parsedOutput = parsedOutput + "Courses transfer:" + "\r\n";
            for (int i = 0; i < transferCourses.size(); i++) {
                System.out.println(transferCourses.get(i));
                parsedOutput = parsedOutput + transferCourses.get(i) + "\r\n";

            }
        }
        System.out.flush();
        System.setOut(old);
        String finalOutput = required.toString();
        System.out.println(finalOutput);

        return finalOutput;
    }
}