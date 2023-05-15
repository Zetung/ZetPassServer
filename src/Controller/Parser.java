package Controller;

import com.zetung.zetpass.model.RecordModel;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Parser {

    private String szDelemiters = "%";

    public ArrayList<RecordModel> parsingRecords (String data){

        StringTokenizer st = new StringTokenizer(data, szDelemiters, true);
        ArrayList<RecordModel> resultArray = new ArrayList<>();

        while(st.hasMoreTokens()){
            String owner = st.nextToken();
            st.nextToken();
            String name = st.nextToken();
            st.nextToken();
            String login = st.nextToken();
            st.nextToken();
            String password = st.nextToken();
            st.nextToken();
            String description = st.nextToken();
            st.nextToken();
            resultArray.add(new RecordModel(owner,name,login,password,description));
        }
        return resultArray;
    }

    public String buildRecords (ArrayList<RecordModel> data){
        StringBuilder result = new StringBuilder();
        for(RecordModel record: data){
            result.append(record.getOwner()).append("%")
                    .append(record.getName()).append("%")
                    .append(record.getLogin()).append("%")
                    .append(record.getPassword()).append("%")
                    .append(record.getDescription()).append("%");

        }

        return result.toString();
    }


    public ArrayList<String> parsingString(String data){
        StringTokenizer st = new StringTokenizer(data, szDelemiters, true);
        ArrayList<String> resultArray = new ArrayList<>();
        while(st.hasMoreTokens()){
            String login = st.nextToken();
            st.nextToken();
            resultArray.add(login);
        }
        return resultArray;
    }

    public String buildString (ArrayList<String> data){
        StringBuilder result = new StringBuilder();
        for(String record: data){
            result.append(record).append("%");
        }
        return result.toString();
    }

}
