package monitoramento

import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import java.time.LocalDateTime
import javax.swing.JOptionPane
import javax.swing.JPasswordField

class Repositorio {
    lateinit var jdbcTemplate: JdbcTemplate

    fun iniciar() {
        jdbcTemplate = Conexao.jdbcTemplate!!
    }
    fun getUltimoCodigoRam():Porcentagem{
        val ultimoCodigo = jdbcTemplate.queryForObject("""
        SELECT * FROM Monitoramento where fkComponente = 3 ORDER BY idMonitoramento DESC LIMIT 1 ;
        """,BeanPropertyRowMapper(Porcentagem::class.java)
        )

        return ultimoCodigo
    }
    fun getUltimoCodigoCPU():Porcentagem{
        val ultimoCodigo = jdbcTemplate.queryForObject("""
        SELECT * FROM Monitoramento where fkComponente = 1 ORDER BY idMonitoramento DESC LIMIT 1 ;
        """,BeanPropertyRowMapper(Porcentagem::class.java)
        )

        return ultimoCodigo
    }

    fun getParametro():List<Parametro>{
        /*val listaparametro = jdbcTemplate.queryForList(
            """
                SELECT * FROM Parametro WHERE idParametro IN (1, 3);
            """,Parametro::class.java
        )
        return parametro
        */
        val listaParametro:List<Parametro> = jdbcTemplate.query(
            "SELECT * FROM Parametro WHERE idParametro IN (1, 3);",
            BeanPropertyRowMapper(Parametro::class.java)
        )
        return listaParametro

    }

    fun buscarDados(email:String, senha:String):Dados{
        val buscar = jdbcTemplate.queryForObject<Dados>(
            """
                select maquina.idMaquina,maquina.fkEmpresa,maquina.fkPlanoEmpresa, maquina.fkTipoMaquina from colaborador join Empresa 
                on Colaborador.fkEmpresa = Empresa.idEmpresa join Maquina on Empresa.idEmpresa = Maquina.fkEmpresa 
                where colaborador.email = ? and colaborador.senha = ?;
            """,arrayOf(email,senha),BeanPropertyRowMapper(Dados::class.java)
        )
        return buscar
        // select maquina.idMaquina,maquina.fkEmpresa,maquina.fkPlanoEmpresa, maquina.fkTipoMaquina from colaborador join Empresa
        //on Colaborador.fkEmpresa = Empresa.idEmpresa join Maquina on Empresa.idEmpresa = Maquina.fkEmpresa where colaborador.email ="caramico@gmail.com" and colaborador.senha = "123123";
    }

    fun enviarAlerta(hora: LocalDateTime,nivelAviso:String,cor:String,porcentagem:Porcentagem,dados:Dados){
        // insert into aviso values (null,now(),'critico','red',15,3,1,1,1,1);
        jdbcTemplate.update(
            """
                insert into aviso(dataHora,nivelAviso,cor,fkMonitoramento,fkComponente,fkMaquina,fkEmpresa,fkPlanoEmpresa,fkTipoMaquina)
                 values(?,?,?,?,?,?,?,?,?)
            """,hora,nivelAviso,cor,porcentagem.idMonitoramento,porcentagem.fkComponente,dados.idMaquina,dados.fkEmpresa,dados.fkPlanoEmpresa,dados.fkTipoMaquina
        )
    }
}