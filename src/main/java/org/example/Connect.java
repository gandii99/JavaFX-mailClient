package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The Connect class represents connect from database. The new object one is constant.
 * We can create a Connect object like this:
 * Connect connect = new Connect ("195.150.230.210", "5434", "2021_urban_radoslaw", "2021_urban_radoslaw", "33209") or
 * Connect connect = new Connect ("path to file with properties").
 */
public class Connect {

    private String driver = "org.postgresql.Driver";
    private String host = "195.150.230.210";
    private String port = "5434";//wymagane kiedy nie jest domyślny dla bazy
    private String dbname = "2021_urban_radoslaw";
    private String user = "2021_urban_radoslaw";
    private String url = "jdbc:postgresql://" + host+":"+port + "/" + dbname;
    private String pass = "33209";
    private Connection connection;


    /**
     * The constructor for creating objects of the Connect class, holds a connection to the database.
     *
     * @param host String variable taking address of database
     * @param port String variable taking port of database
     * @param dbname String variable taking name of database
     * @param user String variable taking username of database
     * @param password String variable taking user password of database
     */
    public Connect (String host, String port, String dbname, String user, String password) {
        this.host = host;
        this.port = port;
        this.dbname = dbname;
        this.user = user;
        this.pass = pass;
        connection = makeConnection();
    }

    /**
     * The constructor for creating objects of the Connect class, holds a connection to the database.
     *
     * @param plik String variable taking path to file with database properties.
     */
    public Connect (String plik) {
        String[] dane = new String[5];
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(plik));
            String line;
            int i=0;
            while ((line = reader.readLine()) != null){
                dane[i] = line;
                i++;
            }
            this.host = dane[0];
            this.port = dane[1];
            this.dbname = dane[2];
            this.user = dane[3];
            this.pass = dane[4];
            connection = makeConnection();
        } catch (IOException e) {
            System.out.println("Nie można odnaleźć pliku");
        }
    }

    /**
     * This method return connection to database;
     *
     * @return Object Connection class, holds connection to database;
     */
    public Connection getConnection(){
        return(connection);
    }

    /**
     * This method close database connection.
     */
    public void close() {
        try {
            connection.close(); }
        catch (SQLException sqle){
            System.err.println("Blad przy zamykaniu polaczenia: " + sqle);
        }
    }

    /**
     * This method connect to database .
     *
     * @return Object Connection class
     */
    public Connection makeConnection(){
        try {
            Class.forName(driver);
            Connection connection = DriverManager.getConnection(url, user, pass); return(connection);
        }
        catch(ClassNotFoundException cnfe) {
            System.err.println("Blad ladowania sterownika: " + cnfe);
            return(null);
        }
        catch(SQLException sqle) {
            System.err.println("Blad przy nawiązywaniu polaczenia: " + sqle);
            return(null);
        }
    }

    /**
     * This method gets name of tables from database.
     *
     * @return An array of String with names of the database table
     * @throws SQLException
     */
    public String[] takeTableNames() throws SQLException {
        String[] types = {"TABLE"};
        List<String> temp_list_name_table = new ArrayList<String>();
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tables = metaData.getTables(null, null, "%", types);
        int x = 0;
        while (tables.next()) {
            temp_list_name_table.add(tables.getString("TABLE_NAME"));
        }
        String[] nazwy_tablicy = new String[temp_list_name_table.size()];
        for(int i=0; i< temp_list_name_table.size(); i++){
            nazwy_tablicy[i] = temp_list_name_table.get(i);
        }
        return nazwy_tablicy;
    }

    /**
     * This method gets content of tables from database.
     *
     * @param table_name String variable taking name of database table
     * @return An two-dimensional array of String with content of the database table
     * @throws SQLException
     */
    public String[][] takeContentTable(String table_name) throws SQLException {

        String sql = "SELECT * FROM " + table_name;
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();


        int y = 0;
        while (rs.next()) {
            y++;
        }
        String[][] content_table = new String[y][columnsNumber];
        y=0;
        rs = statement.executeQuery(sql);

        while (rs.next()) {
            for(int i=0; i<columnsNumber; i++){
                content_table[y][i] = rs.getString(i+1);
            }
            y++;
        }
        return content_table;
    }

    /**
     * This method print content table.
     *
     * @param table An two-dimensional array of String taking content of the database table
     */
    public static void printTable(String[][] table){
        for(int i=0; i<table.length; i++){
            for(int j=0; j<table[i].length; j++){
                System.out.print(table[i][j] + "\t\t\t");
            }
            System.out.println();
        }
    }

}