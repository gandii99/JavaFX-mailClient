package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.mail.AuthenticationFailedException;
import javax.mail.internet.AddressException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MailClientController {
    List<CheckBox> listMailToSend = new ArrayList<>();

    @FXML
    TextField recipientMail;
    @FXML
    TextField topicMail;
    @FXML
    TextField senderMail;
    @FXML
    PasswordField senderPassword;
    @FXML
    TextArea contentMail;
    @FXML
    ListView<Object> listMailSend;
    @FXML
    Label error;

    @FXML
    public void initialize() throws SQLException {
        Connect connect = new Connect("C:/Users/Radek/IdeaProjects/BazyDanychConnect-main/src/connection/database.txt");
        String sql = "SELECT * FROM roznosci.newsletter WHERE send = true";
        Statement statement = connect.getConnection().createStatement();
        ResultSet rs = statement.executeQuery(sql);
        while(rs.next()){
            listMailToSend.add(new CheckBox(rs.getString("mail")));
        }
        listMailSend.getItems().addAll(listMailToSend);
    }

    @FXML
    public void sendMail() throws Exception {
        for(int i=0; i < listMailToSend.size(); i++){
            try{
                if(senderMail.getText().length() == 0) throw new IllegalArgumentException("Nie podano adresu nadawcy!");
                if(senderPassword.getText().length() == 0) throw new IllegalArgumentException("Nie podano hasła nadawcy!");
                if(topicMail.getText().length() == 0) throw new IllegalArgumentException("Nie podano tematu maila!");
                if(contentMail.getText().length() == 0) throw new IllegalArgumentException("Nie podano treści maila!");
                if(listMailToSend.get(i).isSelected()){
                    JavaMailUtil obiekt = new JavaMailUtil(senderMail.getText(), senderPassword.getText());
                    obiekt.sendMail(listMailToSend.get(i).getText(), topicMail.getText(), contentMail.getText());
                }else{
                    throw new IllegalArgumentException("Nie wybrałeś adresu do wysłania!");
                }
                error.setText("Wysłano maila!");
            }catch (IllegalArgumentException e){
                error.setText(e.getMessage());
            }catch(AuthenticationFailedException e){
                error.setText("Problem z autoryzacją maila nadawcy!");
            }catch (NullPointerException | AddressException e){
                System.out.println(e.getMessage());
                error.setText("Problem z wysłaniem maila!");
            }
        }
    }

    @FXML
    private void switchToDataBase() throws IOException {
        App.setRoot("dataBaseUczelnia");
    }
}
