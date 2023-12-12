import psutil
import time
import platform
import datetime
from mysql.connector import connect

# se seu python não rodar verifique se os pips necessários estão instalados

# conectando com o workbench para fazer os selects
# colocar suas credencias do banco
conn = connect(
    host='localhost',
    user='root',
    password='22928512',
    database='HealthTouch'
)

cursor = conn.cursor()

print("Bem Vindo à Aplicação Health Touch")
email = input("Digite seu e-mail:")
senha = input("Digite sua senha:")

# puxando todos os dados do colaborador
query = "SELECT * FROM Colaborador WHERE email = %s AND senha = %s"
cursor.execute(query, (email, senha))
resultado = cursor.fetchone()

# puxando somente o nome do colaborador
query = "SELECT nome FROM Colaborador WHERE email = %s AND senha = %s"
cursor.execute(query, (email, senha))
nome = cursor.fetchone()

# puxando a fkEmpresa
query = "SELECT fkEmpresa FROM Colaborador WHERE email = %s AND senha = %s"
cursor.execute(query, (email, senha))
fkEmpresa = cursor.fetchone()

# puxando o cargo
query = "SELECT fkNivelAcesso FROM Colaborador WHERE email = %s AND senha = %s"
cursor.execute(query, (email, senha))
fkNivelAcesso = cursor.fetchone()


# completando o nome dos cargos
if fkNivelAcesso:
    fkNivelAcesso = fkNivelAcesso[0]

    if fkNivelAcesso == 1:
        cargo = "Representante Legal"
    elif fkNivelAcesso == 2:
        cargo = "Gerente de TI"
    elif fkNivelAcesso == 3:
        cargo = "Equipe de TI"

# validando login
if resultado:
    print('\r\n')
    print(f"Login bem-sucedido. Logado como {nome[0]} - {cargo}")

    # conectando com o workbench para fazer os inserts
    def mysql_connection(host, user, passwd, database=None):
        connection = connect(
            host=host,
            user=user,
            passwd=passwd,
            database=database
        )
        return connection

    # aqui colocar suas credencias do banco
    connection = mysql_connection('localhost', 'root', '22928512', 'HealthTouch')

    # puxando todas as máquinas cadastradas
    query = "select idMaquina, SO, IP, sala, andar, nome from Maquina JOIN LocalSala on fkLocal = idLocalSala join setor on fkSetor = idSetor;"
    cursor.execute(query)
    maquinas = cursor.fetchall()

    # printando as máquinas disponíveis
    print('\r\n')
    print('Lista de máquinas disponíveis para monitoramento:\r\n')
    for maquina in maquinas:
        print("ID da Máquina:", maquina[0],"- Sistema Operacional:", maquina[1],"- Endereço IP:",
              maquina[2],"- Sala:", maquina[3],"- Andar:", maquina[4], "- Setor:", maquina[5], "\n")

    # usuário escolhendo a máquina que quer monitorar
    idMaquinaSelect = input("Qual o ID da máquina que você quer monitorar?")
    query = "SELECT idMaquina FROM Maquina WHERE idMaquina = %s"
    cursor.execute(query, (idMaquinaSelect,))
    idMaquinaInsert = cursor.fetchone()

    # verificando se a máquina existe
    if idMaquinaInsert:
        print("Iniciando o Monitoramento")

        # Puxando a fkPlanoEmpresa
        query = "SELECT fkPlanoEmpresa FROM Maquina WHERE idMaquina = %s"
        cursor.execute(query, (idMaquinaSelect,))
        fkPlanoEmpresa = cursor.fetchone()        

        # Puxando a fkTipoMaquina
        query = "SELECT fkTipoMaquina FROM Maquina WHERE idMaquina = %s"
        cursor.execute(query, (idMaquinaSelect,))
        fkTipoMaquina = cursor.fetchone()

        # rodando o monitoramento
        while True:
            cpu = round(psutil.cpu_percent(interval=1), 2)
            memoria = round(psutil.virtual_memory().percent, 2)
            data = datetime.datetime.now()

            query = '''
            insert into Monitoramento(porcentagem, dataHora, fkComponente, fkMaquina, fkPlanoEmpresa, fkTipoMaquina, fkEmpresaMaquina)
            VALUES (%s, %s, %s, %s, %s, %s, %s), (%s, %s, %s, %s, %s, %s, %s);
            '''
            insert = [
                cpu, data, 1, idMaquinaInsert[0], fkPlanoEmpresa[0], fkTipoMaquina[0], fkEmpresa[0],
                memoria, data, 3, idMaquinaInsert[0], fkPlanoEmpresa[0], fkTipoMaquina[0], fkEmpresa[0],
            ]

            cursor = connection.cursor()
            cursor.execute(query, insert)
            connection.commit()

            print(f"Uso da CPU: {cpu}%")
            print(f"Uso do Disco: {memoria}%")

            time.sleep(5)

    else:
        print("Máquina não está cadastrada")

else:
    print("Login Inválido")
    cursor.close()
