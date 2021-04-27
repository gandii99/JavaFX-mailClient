package org.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class DataBaseUczelniaControler {

    List<String> nazwyTabel = new ArrayList<>();
    Map<String, String> schamatTablica = new HashMap<>();
    Connect connect = new Connect("C:/Users/Radek/IdeaProjects/BazyDanychConnect-main/src/connection/database.txt");
    @FXML
    private TableView<Object> tabela;
    @FXML
    private ComboBox<Object> tableName;
    @FXML
    private Label errorText;

    @FXML
    void initialize() throws SQLException {
        schamatTablica.put("adresy", "dziekanat");
        schamatTablica.put("fakultety", "dziekanat");
        schamatTablica.put("kierunki_studiow", "dziekanat");
        schamatTablica.put("oceny", "dziekanat");
        schamatTablica.put("osobiste", "dziekanat");
        schamatTablica.put("przedmioty", "dziekanat");
        schamatTablica.put("studenci", "dziekanat");
        schamatTablica.put("studenci_kierunkow", "dziekanat");
        schamatTablica.put("wojsko", "dziekanat");
        schamatTablica.put("zapisy", "dziekanat");
        schamatTablica.put("jednostki_organizacyjne", "kadry");
        schamatTablica.put("prowadzacy", "kadry");
        schamatTablica.put("wyplaty", "kadry");
        schamatTablica.put("numbers", "roznosci");
        schamatTablica.put("sala", "roznosci");
        schamatTablica.put("seans", "roznosci");
        schamatTablica.put("newsletter", "roznosci");

        tableName.getItems().addAll(connect.takeTableNames());
        nazwyTabel.addAll(Arrays.asList(connect.takeTableNames()));
    }

    @FXML
    private void downloadData() throws SQLException {
        ObservableList<Object> data = FXCollections.observableArrayList();

        tabela.getColumns().clear();
        if(tableName.getValue() != null){
            String sql = "SELECT * FROM " + schamatTablica.get(tableName.getValue()) + "."+tableName.getValue();
            Statement statement = connect.getConnection().createStatement();
            try{
                ResultSet rs = statement.executeQuery(sql);


                for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                    final int j = i;
                    TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                    col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> {
                        if (param.getValue().get(j) != null) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        } else {
                            return null;
                        }
                    });
                    tabela.getColumns().addAll(col);
                }
                while(rs.next()){
                    ObservableList<String> row = FXCollections.observableArrayList();
                    for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                        row.add(rs.getString(i));
                    }
                    data.add(row);
                }
                errorText.setText("");
            }catch (SQLException e){
                errorText.setText("Problem z połączeniem z Bazą Danych!");
            }
            tabela.setItems(data);
        }else{
            errorText.setText("Nie wybrałeś tabeli!");
        }
    }

    @FXML
    private void goToMail() throws IOException {
        App.setRoot("mailClient");
    }

    @FXML
    private void readProperties(){
        try{
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files","*.txt"));
            List<File> f = fc.showOpenMultipleDialog(null);
            for (File file: f){
                connect = new Connect(file.getAbsolutePath());
            }
            errorText.setText("Załadowano ustawienia z pliku!");
        }catch(NullPointerException e){
            errorText.setText("Nie znaleziono pliku!");
        }catch(ArrayIndexOutOfBoundsException e){
            errorText.setText("Błąd podczas wczytywania pliku!");
        }


    }
}
