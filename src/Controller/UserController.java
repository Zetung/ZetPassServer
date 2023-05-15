package Controller;

import DAO.UserDAO;
import com.zetung.zetpass.model.RecordModel;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class UserController implements Runnable{

    private String requestType;
    private String request;
    private UserDAO userDAO = new UserDAO();
    private Socket socket;

    String szDelemiters = "%";

    private String status = "NON";
    private String mail;
    private String login;
    private String password;

    public UserController(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run(){

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outMess = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            ObjectOutputStream outObj = new ObjectOutputStream (socket.getOutputStream());
            ObjectInputStream inObj = new ObjectInputStream(socket.getInputStream())){

            while (!socket.isClosed()) {
                socket.setTcpNoDelay(true);
                socket.setKeepAlive(true);
                socket.setSoTimeout(20000);

                //System.out.println("Listen...");
                request = reader.readLine();
                //System.out.println(request);
                StringTokenizer st = new StringTokenizer(request, szDelemiters, true);
                requestType = st.nextToken();
                st.nextToken();
                mail = st.nextToken();
                st.nextToken();
                login = st.nextToken();
                st.nextToken();
                password = st.nextToken();

                System.out.println(login+" want to "+requestType);
//                System.out.println(login);
//                System.out.println(password);
                switch (requestType) {
                    case "REG":
                        outMess.println(userDAO.registration(mail, login, password));

                        break;
                    case "ENTER":
                        outMess.println(userDAO.enter(login, password));

                        break;
                    case "SAVE":
                        status = userDAO.enter(login,password);
                        if(status.equals("OK")){
                            outMess.println(status);

                            ArrayList<RecordModel> temp = (ArrayList<RecordModel>) inObj.readObject();
                            outMess.println(userDAO.save(login,temp));

                            break;
                        }
                        outMess.println(status);

                        break;
                    case "LOAD":
                        status = userDAO.enter(login,password);
                        if(status.equals("OK")){
                            outMess.println(status);
                            Parser parser = new Parser();
                            outMess.println(parser.buildRecords(userDAO.load(login)));
                            break;
                        }
                        outMess.println(status);

                        break;
                    case "ADD_FRIEND":
                        status = userDAO.enter(login,password);
                        //System.out.println(status);
                        if(status.equals("OK")){
                            outMess.println(status);
                            //System.out.println(status);

                            String loginFriend = reader.readLine();
                            //System.out.println(loginFriend);

                            outMess.println(userDAO.addFriend(login,loginFriend));

                            break;
                        }
                        outMess.println(status);

                        break;
                    case "CHECK_INVITE":
                        status = userDAO.enter(login,password);
                        //System.out.println(status);
                        if(status.equals("OK")){
                            outMess.println(status);
                            Parser parser = new Parser();
                            outMess.println(parser.buildString(userDAO.checkInvite(login)));
                            break;
                        }
                        outMess.println(status);

                        break;
                    case "CHECK_INBOX":
                        status = userDAO.enter(login,password);
                        //System.out.println(status);
                        if(status.equals("OK")){
                            outMess.println(status);
                            Parser parser = new Parser();
                            outMess.println(parser.buildRecords(userDAO.checkShare(login)));

                            break;
                        }
                        outMess.println(status);

                        break;
                    case "CONFIRM_FRIEND":
                        status = userDAO.enter(login,password);
                        //System.out.println(status);
                        if(status.equals("OK")){
                            outMess.println(status);

                            String loginFriend = reader.readLine();
                            outMess.println(userDAO.confirmFriend(login,loginFriend));

                            break;
                        }
                        outMess.println(status);

                        break;
                    case "SHARE":
                        status = userDAO.enter(login,password);
                        //System.out.println(status);
                        if(status.equals("OK")){
                            outMess.println(status);
                            String loginFriend = reader.readLine();
                            RecordModel recordModel = (RecordModel) inObj.readObject();
                            outMess.println(userDAO.share(login,loginFriend,recordModel));

                            break;
                        }
                        outMess.println(status);

                        break;
                    case "CLEAR_INVITE":
                        status = userDAO.enter(login,password);
                        if(status.equals("OK")){
                            outMess.println(userDAO.clearInvite(login));

                            break;
                        }
                        outMess.println(status);

                        break;
                    case "CLEAR_INBOX":
                        status = userDAO.enter(login,password);
                        if(status.equals("OK")){
                            outMess.println(userDAO.clearShare(login));

                            break;
                        }
                        outMess.println(status);

                        break;
                    case "DELETE_FRIEND":
                        status = userDAO.enter(login,password);
                        if(status.equals("OK")){
                            outMess.println(status);
                            String loginFriend = reader.readLine();
                            outMess.println(userDAO.deleteFriend(login,loginFriend));

                            break;
                        }
                        outMess.println(status);

                        break;
                    case "LOAD_FRIENDS":
                        status = userDAO.enter(login,password);
                        if(status.equals("OK")){
                            outMess.println(status);
                            Parser parser = new Parser();
                            outMess.println(parser.buildString(userDAO.loadFriend(login)));
                            break;
                        }
                        outMess.println(status);

                        break;
                    default: socket.setTcpNoDelay(false);
                        socket.setKeepAlive(false);
                        socket.close();
                }
                userDAO.close();
                userDAO.closeConnection();
                System.out.println(requestType+" from "+login+" is done");
                socket.setTcpNoDelay(false);
                socket.setKeepAlive(false);
                socket.close();
                System.gc();
            }

        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


}
