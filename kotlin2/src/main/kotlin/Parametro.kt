package monitoramento

import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate

class Parametro {
    var idParametro:Int = 0
    var critico: Double = 0.0
    var alerta: Double = 0.0
    var fkComponente: Int = 0
}