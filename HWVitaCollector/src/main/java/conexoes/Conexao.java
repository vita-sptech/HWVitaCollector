package conexoes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private static  final String url = "jdbc:mysql://localhost:3306/vita";
    private static  final String user = "root";
    private static  final String password = "Odranoel@6969";


    private static Connection conn;

    public static Connection getConexao(){

        try{
            if(conn == null){
                conn = DriverManager.getConnection(url,user,password);
            }
            return conn;
        }catch (SQLException e) {
            System.out.println("Conexão com banco local não foi iniciada.");
            return null;
        }

    }

}