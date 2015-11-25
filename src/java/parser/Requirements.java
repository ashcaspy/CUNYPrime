package parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gracie on 10/3/2015.
 */
public class Requirements {

    //A double that represent the number of credit or class needed to fulfill a requirement.
    private double num;

    //A integer the represents the how many options a user can choose from in cases where there are multiple choices to fulfill a requirement.
    private int numForCategory;

    //A list of string contaning classes that will fulfill a requirement.
    private List <String> myList;

    //A list of Requirement that holds other options that will fulfill a requirement if there are multiple choices to choose from.
    private List <Requirements> subList;

   //A list of string contaning specific classes that won't fulfill a requirement.
    private List <String> exceptList;

    //A string that holds whether the requirement is in terms of number of classes needed or number of credits needed. (Eg. 2 classes in or 6 credits in).
    private String c;

    //A boolean that indicates whether the list of classes in a sublist of classes that can also fulflll a requirement contains "and".
    private boolean containsAndSubList;

    //A boolean that indicates whether the list of classes that fulfils a requirement contains "and". This means every class in the list must be taken.
    private boolean containsAnd;

    //A integer that is the size of the array of classes that will fulfill the requirement.
    private int index;

    //A integer that is the size of the sublist array of classes that will fulfill the requirement.
    private int indexForSubList;

    public Requirements(){
        numForCategory = 0;
        myList = new ArrayList<>();
        subList = new ArrayList<>();
        c = "";
        exceptList = new ArrayList<>();
        containsAndSubList = false;
        containsAnd = false;
        index = -1;
        indexForSubList = -1;
    }

    public void addToList(String param){
        myList.add(param);
        index++;
    }

    public void addToExceptList(String param){
        exceptList.add(param);
    }

    public void editMyList(String param, int i){
        myList.set(i, param);
    }

    public String getIndexedElementFromMyList(int i){
        return myList.get(i);
    }

    public Requirements getElementFromSublist(int i) { return subList.get(i); }

    public void addToSubList(Requirements param){ subList.add(param); indexForSubList++; }

    public int getIndex(){
        return index;
    }

    public int getIndexForSubList() { return  indexForSubList; }

    public void setContainsAnd(boolean param){
        containsAnd = param;
    }

    public void setcontainsAndSubList (boolean param){ containsAndSubList = param; }

    public void setNumForCategory(int param){ numForCategory = param; }

    public void setNum(double num){ this.num = num; }

    public void setC(String param){ c = param; }

    public boolean getcontainsAndSubList(){ return containsAndSubList; }
	 

    public String println(){
    	  String output = "";
        
        if (num != 0) {
            System.out.println(num + " " + c + "in:");
            output = output + num + " " + c + "in:" + "\r\n";
        }
        if (numForCategory != 0) {
            System.out.println("Choose from " + numForCategory + " of the following: ");
            output = output + "Choose from " + numForCategory + " of the following: " + "\r\n";
        }

        for (int i = 0; i < myList.size(); i++){
            if(containsAnd) {
                if(i < myList.size() - 1) {
                    System.out.println(myList.get(i) + " &");
                    output = output + myList.get(i) + " &" + "\r\n";
                } else {
                    System.out.println(myList.get(i));
                    output = output + myList.get(i) + "\r\n";

                }
            } else {
                System.out.println(myList.get(i));
                output = output + myList.get(i) + "\r\n";

            }

        }

        if (!exceptList.isEmpty()){
            System.out.println("The following won't count towards fulfilling the requirement: ");
            output = output + "The following won't count towards fulfilling the requirement: " + "\r\n";
            for (int i = 0; i < exceptList.size(); i++){
                System.out.println(exceptList.get(i));
                output = output + exceptList.get(i) + "\r\n";
            }
        }

        if(!subList.isEmpty()){
            for(int i = 0; i < subList.size();i++){
                subList.get(i).println();
                if(i != subList.size() -1) {
                    if (containsAndSubList) {
                        System.out.println("and");
                        output = output + "and" + "\r\n";
                    } else {
                        System.out.println("or");
                        output = output + "or" + "\r\n";

                    }
                }
            }

            System.out.println("*********************");
            output = output + "*********************" + "\r\n";

        }
        return output;
    }

}
