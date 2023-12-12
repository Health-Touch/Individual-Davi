package monitoramento

import java.time.LocalDateTime

 class Porcentagem {
    var idMonitoramento:Int = 0
    var porcentagem:Double = 0.0
    lateinit var date: LocalDateTime
    var fkComponente:Int = 0
    var fkMaquina:Int = 0
    var fkEmpresaDispositivo:Int = 0
    var fkPlanoEmpresa:Int = 0
    var fkTipoDispositivo:Int = 0
}