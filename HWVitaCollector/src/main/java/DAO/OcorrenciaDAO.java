package DAO;

import conexoes.Conexao;
import conexoes.ConexaoNuvem;
import helpers.Helper;
import helpers.Logging;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OcorrenciaDAO {

    public static void inserirOcorrencia(String fkMaquina, String dtOcorrencia, String componente, String categoria, String metrica, String descricao) {
        String sql = "INSERT INTO Ocorrencia (fkMaquina,dtOcorrencia,componente,categoria,metrica,descricao) VALUES (?,?,?,?,?,?);";
        PreparedStatement ps;
        String stacktrace = null;
        try {
            if (Conexao.conn == null) throw new RuntimeException("Sem conexão local");
            ps = Conexao.conn.prepareStatement(sql);
            setValues(fkMaquina, dtOcorrencia, componente, categoria, metrica, descricao, ps);
        } catch (Exception e) {
            stacktrace = Helper.getStackTraceAsString(e);
            Logging.AddLogInfo(Logging.fileHandler, e.getMessage() + stacktrace);
        }

        try {
            if (Conexao.conn == null) throw new RuntimeException("Sem conexão com a nuvem");
            ps = ConexaoNuvem.conn.prepareStatement(sql);
            setValues(fkMaquina, dtOcorrencia, componente, categoria, metrica, descricao, ps);
        } catch (Exception e) {
            stacktrace = Helper.getStackTraceAsString(e);
            Logging.AddLogInfo(Logging.fileHandler, e.getMessage() + stacktrace);
        }

    }

    public static boolean hasOcorrenciaIgualRecente(String fkMaquina, String metrica) {
        // MYSQL QUERY
        //String sql = "SELECT * FROM Ocorrencia o WHERE fkMaquina = ? and metrica = ? AND TIMESTAMPDIFF(second,o.dtOcorrencia,now()) < 60;";

        String sql = "SELECT * FROM Ocorrencia o WHERE fkMaquina = ? AND metrica = ? AND DATEDIFF(SECOND, o.dtOcorrencia, DATEADD(HOUR, -3, GETDATE())) < 60;";
        if(fkMaquina.contains("ec2")){
            sql = "SELECT * FROM Ocorrencia o WHERE fkMaquina = ? AND metrica = ? AND DATEDIFF(SECOND, o.dtOcorrencia, GETDATE()) < 60;";
        }
        //SQLSERVER QUERY
        PreparedStatement ps;


        try {

            if (Conexao.conn == null) throw new RuntimeException("Sem conexão local");
            ps = Conexao.conn.prepareStatement(sql);
            ps.setString(1, fkMaquina);
            ps.setString(2, metrica);
            ps.execute();

            ResultSet rs = ps.getResultSet();
            return rs.next();
        } catch (Exception e) {
            String stacktrace = Helper.getStackTraceAsString(e);
            Logging.AddLogInfo(Logging.fileHandler, e.getMessage() + stacktrace);
        }

        return true;
    }

    private static void setValues(String fkMaquina, String dtOcorrencia, String componente, String categoria, String metrica, String descricao, PreparedStatement ps) throws SQLException {
        ps.setString(1, fkMaquina);
        ps.setString(2, dtOcorrencia);
        ps.setString(3, componente);
        ps.setString(4, categoria);
        ps.setString(5, metrica);
        ps.setString(6, descricao);
        ps.execute();
    }
}
