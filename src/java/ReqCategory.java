import java.util.ArrayList;
import java.util.List;

/**
 * Created by gracie on 10/24/2015.
 */
public class ReqCategory {
    private String name;
    private String creditReq;
    private String creditApp;
    private List<Requirements> myList;
    private int index;

    public ReqCategory(){
        name = "";
        creditReq = "";
        creditApp = "";
        myList = new ArrayList<>();
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setCreditReq(String creditReq){
        this.creditReq = creditReq;
    }

    public String getCreditReq(){
        return creditReq;
    }

    public void setCreditApp(String creditApp){
        this.creditApp = creditApp;
    }

    public String getCreditApp(){
        return creditApp;
    }

    public void setMyList(List<Requirements> listy){
        myList = new ArrayList<Requirements>(listy);
    }


    public void print(){
        System.out.println(name);
        System.out.println(creditReq);
        System.out.println(creditApp);
        for(int i = 0; i < myList.size(); i++){
            myList.get(i).println();
        }
        System.out.println("~~~~~~~~~~~~~~~~");
    }

}
