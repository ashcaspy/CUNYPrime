import java.util.ArrayList;
import java.util.List;

/**
 * Created by gracie on 10/3/2015.
 */
public class Requirements {

    private double num;
    private int numForCategory;
    private List <Requirements> subList;
    private List <String> myList;
    private List<String> classInProgress;
    private List <String> exceptList;
    private String c;
    private boolean containsAndSubList;
    private boolean containsAnd;
    private int index;
    private int indexForSubList;

    public Requirements(){
        numForCategory = 0;
        myList = new ArrayList<>();
        subList = new ArrayList<>();
        classInProgress = new ArrayList<>();
        c = "";
        exceptList = new ArrayList<>();
        containsAndSubList = false;
        containsAnd = false;
        index = -1;
        indexForSubList = -1;
    }

    public void addToInProgressList(String param) { classInProgress.add(param); }

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

    public void editSublist(Requirements req, int i){ subList.set(i, req); }

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

    public void println(){
        /*if(!classInProgress.isEmpty()){
            System.out.println("Potential Class: ");
            for (int i = 0; i < classInProgress.size(); i++){
                System.out.println(classInProgress.get(i));
            }
            System.out.println("~~~~~~~~~");

        }*/
        if (num != 0) {
            System.out.println(num + " " + c + "in:");
        }
        if (numForCategory != 0) {
            System.out.println("Choose from " + numForCategory + " of the following: ");
        }
        //System.out.println("Choose " + numForCategory + " from the following: ");


        for (int i = 0; i < myList.size(); i++){
            if(containsAnd) {
                if(i < myList.size() - 1) {
                    System.out.println(myList.get(i) + " &");
                } else {
                    System.out.println(myList.get(i));

                }
            } else {
                System.out.println(myList.get(i));

            }

        }

        if (!exceptList.isEmpty()){
            System.out.println("The following won't count towards fulfilling the requirement: ");
            for (int i = 0; i < exceptList.size(); i++){
                System.out.println(exceptList.get(i));
            }
        }

        if(!subList.isEmpty()){
            System.out.println("SubList: ");
            for(int i = 0; i < subList.size();i++){
                subList.get(i).println();
                if(i != subList.size() -1) {
                    if (containsAndSubList) {
                        System.out.println("and");
                    } else {
                        System.out.println("or");

                    }
                }
            }

            /*if(!exceptList.isEmpty()){
                for(int i = 0; i < exceptList.size(); i++){
                    System.out.println(exceptList.get(i));
                }
            }*/
            System.out.println("*********************");

        }
    }

}
