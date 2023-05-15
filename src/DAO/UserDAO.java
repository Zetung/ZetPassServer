package DAO;

import com.zetung.zetpass.model.RecordModel;

import java.sql.*;
import java.util.ArrayList;


public class UserDAO {

    public static final String DB_URL = "jdbc:sqlite:c:/ZetPass/ZetPass.db";
    public static final String DB_Driver = "org.sqlite.JDBC";

    private Connection connection;
    private Statement statement;

    public UserDAO() {

        try {
            Class.forName(DB_Driver);
            connection = DriverManager.getConnection(DB_URL);

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }

        try{
            String queryMain = "CREATE TABLE user (" +
                    " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " mail VARCHAR(50) NOT NULL UNIQUE," +
                    " login VARCHAR(50) NOT NULL UNIQUE," +
                    " password VARCHAR(50) NOT NULL" +
                    " CHECK((mail !='') AND (login !=''))" +
                    " );";
            statement = connection.createStatement();
            statement.executeUpdate(queryMain);
            close();

        } catch (SQLException e) {

        }

    }

    public String registration(String mail, String login, String password){
        String query = "INSERT INTO user (mail, login, password) " +
                "VALUES ('"+mail+"', '"+login+"', '"+password+"');";
        try {
            statement = connection.createStatement();
            statement.executeUpdate(query);

            ResultSet rs = statement.executeQuery("SELECT id" +
                    " FROM user " +
                    "ORDER BY id DESC");
            int id = rs.getInt("id");


            String queryFriend = "CREATE TABLE user"+id+"_friends (" +
                    " idfriend INTEGER UNIQUE" +
                    " CHECK(idfriend !='')" +
                    ");";
            statement.executeUpdate(queryFriend);

            String queryInvite = "CREATE TABLE user"+id+"_invite (" +
                    " idfriend INTEGER UNIQUE" +
                    " CHECK(idfriend !='')" +
                    ");";
            statement.executeUpdate(queryInvite);

            String queryRecord = "CREATE TABLE user"+id+"_records (" +
                    " owner VARCHAR(50) NOT NULL," +
                    " name VARCHAR(50) NOT NULL," +
                    " login VARCHAR(50) NOT NULL UNIQUE," +
                    " password VARCHAR(100)," +
                    " description VARCHAR(100)" +
                    " CHECK((name !='') AND (login !=''))" +
                    ");";
            statement.executeUpdate(queryRecord);

            String queryInbox = "CREATE TABLE user"+id+"_inbox (" +
                    " owner VARCHAR(50) NOT NULL," +
                    " name VARCHAR(50) NOT NULL," +
                    " login VARCHAR(50) NOT NULL UNIQUE," +
                    " password VARCHAR(100)," +
                    " description VARCHAR(100)" +
                    " CHECK((name !='') AND (login !=''))" +
                    ");";
            statement.executeUpdate(queryInbox);


            close();
            return "OK";
        } catch (SQLException e) {
            return e.toString();
        }

    }

    public String enter(String login, String password){

        try {

            String query = "SELECT login, password FROM user WHERE login='"+login+"'";
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if(rs.getString("password").equals(password)){
                close();
                return "OK";
            }
            close();
            return "WRONG_PASSWORD";
        } catch (SQLException e) {
            return e.toString();
        }
    }

    public String save(String login, ArrayList<RecordModel> recordList){

        try {

            statement = connection.createStatement();
            int id = returnID(login);
            String queryDrop = "DELETE FROM user"+id+"_records";
            statement.executeUpdate(queryDrop);
            close();

            statement = connection.createStatement();
            for(RecordModel recordModel: recordList){
                String query = "INSERT INTO user"+id+"_records (owner, name, login, password, description)" +
                        "VALUES ('"+recordModel.getOwner()+"'," +
                        "'"+recordModel.getName()+"'," +
                        "'"+recordModel.getLogin()+"'," +
                        "'"+recordModel.getPassword()+"'," +
                        "'"+recordModel.getDescription()+"');";
                statement.executeUpdate(query);
            }

            close();
            return "OK";
        } catch (SQLException e) {
            return e.toString();
        }

    }

    public ArrayList<RecordModel> load (String login){

        try {
            String query = "SELECT * " +
                    " FROM user"+returnID(login)+"_records";
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            ArrayList<RecordModel> recordList = new ArrayList<>();
            while (rs.next()){
                recordList.add(new RecordModel(
                        rs.getString("owner"),
                        rs.getString("name"),
                        rs.getString("login"),
                        rs.getString("password"),
                        rs.getString("description")));
            }
            close();
            return recordList;
        } catch (SQLException e) {
            return null;
        }
    }

    public String share (String loginUser, String loginFriend, RecordModel recordModel){
        int idUser = returnID(loginUser);
        int idFriend = returnID(loginFriend);
        if(idFriend!=0){
            if(friendChecker(idUser,idFriend)){
                try {

                    String queryShare = "INSERT INTO user"+idFriend+"_inbox (owner, name, login, password, description)" +
                            "VALUES ('"+recordModel.getOwner()+"'," +
                            "'"+recordModel.getName()+"'," +
                            "'"+recordModel.getLogin()+"'," +
                            "'"+recordModel.getPassword()+"'," +
                            "'"+recordModel.getDescription()+"');";
                    statement = connection.createStatement();
                    statement.executeUpdate(queryShare);
                    close();
                    return "OK";
                } catch (SQLException e) {
                    return e.toString();
                }
            }
        }

        return "NOT_FOUND";
    }
    public String addFriend(String loginUser, String loginFriend){
        int idFriend = returnID(loginFriend);
        if(idFriend!=0){
            try {
                String queryFriend = "INSERT INTO user"+idFriend+"_invite (idfriend)" +
                        "VALUES ('"+returnID(loginUser)+"');";
                statement = connection.createStatement();
                statement.executeUpdate(queryFriend);

                close();
                return "OK";
            } catch (SQLException e) {
                return e.toString();
            }
        }
        return "NOT_FOUND";
    }


    public String confirmFriend(String loginUser, String loginFriend){
        int idUser = returnID(loginUser);
        int idFriend = returnID(loginFriend);
        if(idFriend!=0) {
            if (inviteChecker(idUser,idFriend)) {
                try {
                    String queryFriend = "INSERT INTO user" + idUser + "_friends (idfriend)" +
                            "VALUES ('" + idFriend + "');";
                    statement = connection.createStatement();
                    statement.executeUpdate(queryFriend);

                    queryFriend = "INSERT INTO user" + idFriend + "_friends (idfriend)" +
                            "VALUES ('" + idUser + "');";
                    statement.executeUpdate(queryFriend);

                    String queryDeleteInvite = "DELETE FROM user"+idUser+"_invite" +
                            " WHERE idfriend='"+idFriend+"';";
                    statement.executeUpdate(queryDeleteInvite);

                    close();
                    return "OK";
                } catch (SQLException e) {
                    return e.toString();
                }
            }
        }
        return "NOT_FOUND";
    }


    public ArrayList<String> checkInvite(String login){
        int idUser = returnID(login);
        boolean chechkEmpty = false;
        try {
            statement = connection.createStatement();

            StringBuilder inviteID = new StringBuilder();

            ArrayList<String> inviteNamedList = new ArrayList<>();
            String queryInvite = "SELECT idfriend FROM" +
                    " user"+idUser+"_invite;";
            ResultSet rs = statement.executeQuery(queryInvite);


            while(rs.next()) {
                int id = rs.getInt("idfriend");
                inviteID.append(id).append(",");
                chechkEmpty = true;
            }
            close();
//            closeConnection();
//
//            connection = DriverManager.getConnection(DB_URL);
            if (chechkEmpty){
                statement = connection.createStatement();

                inviteID.deleteCharAt(inviteID.lastIndexOf(","));
                String query = "SELECT login FROM user WHERE id IN ("+inviteID+")";

                ResultSet loginInvite = statement.executeQuery(query);
                while (loginInvite.next()){
                    inviteNamedList.add(loginInvite.getString("login"));
                }
                close();
            }
            return inviteNamedList;
        } catch (SQLException e) {
            return null;
        }
    }

    public ArrayList<RecordModel> checkShare(String login){
        int idUser = returnID(login);
        try {
            statement = connection.createStatement();

            ArrayList<RecordModel> recordModels = new ArrayList<>();

            String query = "SELECT * FROM user"+idUser+"_inbox";
            ResultSet rs = statement.executeQuery(query);

            while(rs.next()){
                recordModels.add(new RecordModel(
                        rs.getString("owner"),
                        rs.getString("name"),
                        rs.getString("login"),
                        rs.getString("password"),
                        rs.getString("description")));
            }
            close();
            return recordModels;
        } catch (SQLException e) {
            return null;
        }
    }

    public ArrayList<String> loadFriend(String login){
        int idUser = returnID(login);
        boolean chechkEmpty = false;
        try {
            statement = connection.createStatement();

            StringBuilder inviteID = new StringBuilder();

            ArrayList<String> inviteNamedList = new ArrayList<>();
            String queryInvite = "SELECT idfriend FROM" +
                    " user"+idUser+"_friends;";
            ResultSet rs = statement.executeQuery(queryInvite);


            while(rs.next()) {
                int id = rs.getInt("idfriend");
                inviteID.append(id).append(",");
                chechkEmpty = true;
            }
            close();
//            closeConnection();
//
//            connection = DriverManager.getConnection(DB_URL);
            if (chechkEmpty){
                statement = connection.createStatement();

                inviteID.deleteCharAt(inviteID.lastIndexOf(","));
                String query = "SELECT login FROM user WHERE id IN ("+inviteID+")";

                ResultSet loginInvite = statement.executeQuery(query);
                while (loginInvite.next()){
                    inviteNamedList.add(loginInvite.getString("login"));
                }
                close();
            }
            return inviteNamedList;
        } catch (SQLException e) {
            return null;
        }
    }

    public String clearInvite(String login){
        try {
            statement = connection.createStatement();
            int idUser = returnID(login);

            String queryDelete = "DELETE FROM user"+idUser+"_invite;";
            statement.executeUpdate(queryDelete);

            close();

            return "OK";
        } catch (SQLException e) {
            return e.toString();
        }
    }

    public String clearShare(String login){
        try {
            statement = connection.createStatement();
            int idUser = returnID(login);

            String queryDelete = "DELETE FROM user"+idUser+"_inbox;";
            statement.executeUpdate(queryDelete);

            close();

            return "OK";
        } catch (SQLException e) {
            return e.toString();
        }
    }

    public String deleteFriend(String loginUser, String loginFriend){
        int idUser = returnID(loginUser);
        int idFriend = returnID(loginFriend);
        if(idFriend!=0) {
            if (inviteChecker(idUser,idFriend)) {
                try {
                    String queryFriend = "DELETE FROM user" + idUser + "_friends" +
                            " WHERE idfriend='"+idFriend+"';";
                    statement = connection.createStatement();
                    statement.executeUpdate(queryFriend);

                    queryFriend = "DELETE FROM user" + idFriend + "_friends" +
                            " WHERE idfriend='"+idUser+"';";
                    statement.executeUpdate(queryFriend);

                    close();
                    return "OK";
                } catch (SQLException e) {
                    return e.toString();
                }
            }
        }
        return "NOT_FOUND";

    }

    private int returnID(String login) {
        String queryID = "SELECT id" +
                " FROM user" +
                " WHERE login='"+login+"'";
        try {
            ResultSet rs = statement.executeQuery(queryID);
            return rs.getInt("id");
        } catch (SQLException e) {
            return 0;
        }
    }

    private boolean friendChecker(int idUser, int idFriend){
        String queryID = "SELECT idfriend" +
                " FROM user"+idUser+"_friends" +
                " WHERE idfriend='"+idFriend+"'";
        try {
            statement.executeQuery(queryID);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean inviteChecker(int idUser, int idFriend){
        String queryID = "SELECT idfriend" +
                " FROM user"+idUser+"_invite" +
                " WHERE idfriend='"+idFriend+"'";
        try {
            statement.executeQuery(queryID);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public void close() throws SQLException {
        if(!statement.isClosed()){
            statement.close();
        }
    }

    public void closeConnection() throws SQLException{
        if(!connection.isClosed()){
            connection.close();
        }
    }

}
