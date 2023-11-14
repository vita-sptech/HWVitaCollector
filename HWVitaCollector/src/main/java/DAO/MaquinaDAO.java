package DAO;

import conexoes.Conexao;
import conexoes.ConexaoNuvem;
import entidades.Maquina;
import helpers.Helper;
import helpers.Logging;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class MaquinaDAO {

    public static Maquina getMaquinaByUUID(String UUID){
        String sql = "SELECT * FROM Maquina WHERE uuid = ?;";
        PreparedStatement ps = null;
        try{
        ps = Conexao.conn.prepareStatement(sql);
        ps.setString(1,UUID);
        ps.execute();
        ResultSet resultSet = ps.getResultSet();
        Maquina maquina = null;
        while(resultSet.next()){
            maquina = new Maquina();
            maquina.setId(resultSet.getString(1));
            maquina.setFkHospital(resultSet.getInt(2));
            maquina.setLocal(resultSet.getString(3));
            maquina.setResponsavel(resultSet.getString(4));
        }
        ps.close();
        return maquina;
        }catch (SQLException e){
            e.fillInStackTrace();
            return null;
        }
    }


    public static void registrarMaquina(Maquina maquina){
        try {
            registrarMaquinaLocal(maquina);
        }catch (Exception e){
            System.out.println("Não foi possivel conectar no banco Local");
        }
        try {
            registrarMaquinaNuvem(maquina);
        }catch (Exception e){
            System.out.println("Não foi possivel conectar no banco em Nuvem");
        }

    }
    public static void registrarMaquinaLocal(Maquina maquina){
        PreparedStatement ps = null;
        String sql = "INSERT INTO Maquina (uuid,fkHospital,localidade,responsavel) VALUES (?,?,?,?)";
        try{
            ps = Conexao.conn.prepareStatement(sql);
            ps.setString(1,maquina.getUuid());
            ps.setInt(2,maquina.getFkHospital());
            ps.setString(3, maquina.getLocal());
            ps.setString(4, maquina.getResponsavel());
            ps.execute();
            System.out.println(String.format(
                    """
             |---------------------------------------------------------------------------------------------------------|
             | Cadastrando Máquina (Local)                                                                             |
             | %s                                                                                                       \s
             |---------------------------------------------------------------------------------------------------------|
             """,maquina));
            ps.close();
        }catch (SQLException e){
            System.out.println("Ai não né");
            e.fillInStackTrace();
        }
    }
    public static void registrarMaquinaNuvem(Maquina maquina){
        PreparedStatement ps = null;
        String sql = "INSERT INTO Maquina (uuid,fkHospital,localidade,responsavel) VALUES (?,?,?,?)";
        try{
            ps = ConexaoNuvem.conn.prepareStatement(sql);
            ps.setString(1,maquina.getUuid());
            ps.setInt(2,maquina.getFkHospital());
            ps.setString(3, maquina.getLocal());
            ps.setString(4, maquina.getResponsavel());
            ps.execute();
            System.out.println(String.format(
                    """
             |---------------------------------------------------------------------------------------------------------|
             | Cadastrando Máquina (Nuvem)                                                                             |
             | %s                                                                                                       \s
             |---------------------------------------------------------------------------------------------------------|
             """,maquina));
            ps.close();
        }catch (SQLException e){
            String stackTrace = Helper.getStackTraceAsString(e);
            Logging.AddLogInfo( Logging.fileHandler,"Erro ao cadastrar máquina (Nuvem) "+ e.getMessage() + stackTrace);
        }
    }
}
