package monitoramento.app

import monitoramento.Dados
import monitoramento.Porcentagem
import monitoramento.Repositorio
import org.springframework.dao.EmptyResultDataAccessException
import java.time.LocalDateTime
import javax.swing.JOptionPane
import kotlin.concurrent.thread
import kotlin.system.exitProcess

open class Main {
    companion object {
        @JvmStatic fun main(args: Array<String>) {

            // seu código ficará aqui
            /*
    trazer os últimos dados do Monitoramento
    trazer os parametros
    verificar se o último dados é maior que o parametro e inserir na tabela Aviso
    */
            var repositorio = Repositorio()
            repositorio.iniciar()
            var porcentagemCPU = Porcentagem()
            var porcentagemRam = Porcentagem()
            porcentagemRam = repositorio.getUltimoCodigoRam()
            porcentagemCPU = repositorio.getUltimoCodigoCPU()
            var dados = Dados()

            while(true){
                val email = JOptionPane.showInputDialog("Email")
                val senha = JOptionPane.showInputDialog("Senha")

                try {
                    val dadoslogado = repositorio.buscarDados(email,senha)
                    dados.fkPlanoEmpresa = dadoslogado.fkPlanoEmpresa
                    dados.fkEmpresa = dadoslogado.fkEmpresa
                    dados.idMaquina = dadoslogado.idMaquina
                    dados.fkTipoMaquina = dadoslogado.fkTipoMaquina
                    JOptionPane.showMessageDialog(null,"login efetuado com sucesso")
                    break
                } catch (excecao: EmptyResultDataAccessException) {
                    JOptionPane.showMessageDialog(null,"email e senha errado")
                }
            }
            var monitoramento = true
            val monitoramentoThread =  thread{
                while(monitoramento){

                    /*
                pegar os parametros dentro do if, se entrar ele vai mandar a notificação, tem que fazer isso para cada componente
                 */
                    var parametros = repositorio.getParametro()
                    var hora = LocalDateTime.now()
                    parametros.forEachIndexed { i, parametro ->

                        var porcentagem = Porcentagem()
                        if (i == 0) porcentagem = porcentagemCPU else porcentagem = porcentagemRam
                        if (porcentagem.porcentagem < parametro.critico && porcentagem.porcentagem >= parametro.alerta ) {
                            repositorio.enviarAlerta(hora,"alerta","yellow",porcentagem,dados)
                            //mandar alerta para o banco, aqui tem que criar uma função para enviar no banco
                        } else if(porcentagem.porcentagem > parametro.critico) {
                            repositorio.enviarAlerta(hora,"critico","red",porcentagem,dados)

                        }
                    }
                    Thread.sleep(6 * 1000L);
                }
            }
            val MenuThread = thread{
                var menu = true
                while(menu){
                    var salvar = JOptionPane.showInputDialog("Digite S para parar o monitoramento!")
                    when(salvar){
                        "S" -> {
                            menu = false
                            monitoramento = false
                            exitProcess(0)
                        } else -> {
                        JOptionPane.showMessageDialog(null,"Escolha uma opção válida")

                    }
                    }
                }
            }

        }
    }
}