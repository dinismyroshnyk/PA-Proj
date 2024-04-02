# Descrição global da aplicação
### Gestão de acesso e utilizadores
- [x] [R1] Permitir aos utilizadores registarem-se e autenticarem-se na aplicação.
- [x] [R2] Permitir o acesso à aplicação por 3 tipos de utilizadores: gestores da plataforma (ou administradores), autores e revisores.
- [x] [R3] Os utilizadores são caracterizados pelos atributos nome, login, password, estado (ativo/inativo), email e tipo (gestor, autor ou revisor).
- [x] [R4] Tanto o atributo login como o email devem ser únicos.
- [x] [R5] O email deve apresentar um formato válido.
> [!NOTE]
> [designação] @ [entidade] . [domínio]
- [x] [R6] Cada utilizador apenas pode alterar a sua própria informação, não podendo alterar ou visualizar dados de outros utilizadores, ou criar utilizadores.
- [x] [R7] A exceção ao requisito anterior são os gestores, que podem visualizar e alterar dados de todos os utilizadores e criar utilizadores, nomeadamente outros gestores.
- [x] [R8] Tanto os autores como os revisores têm adicionalmente os parâmetros número de contribuinte, contacto telefónico e morada. Os autores possuem ainda estilo literário (e.g. drama, ficção, thriller) e data de início de atividade. Os revisores possuem, adicionalmente, área de especialização e formação académica.
> [!NOTE]
> (e.g. ciências, literatura, artes)
- [x] [R9] O atributo número de contribuinte deve ser único e possuir 9 dígitos.
- [x] [R10] O atributo contacto telefónico deve possuir 9 dígitos e iniciar pelos dígitos 9, 2 ou 3.
- [x] [R11] Os autores e revisores devem registar-se na plataforma. Os gestores são criados por outros gestores.
- [x] [R12] Todos os pedidos de registo de novos utilizadores devem ser notificados aos gestores através da aplicação.
- [x] [R13] Os gestores aprovam ou rejeitam os pedidos de registo dos utilizadores. Todos os pedidos devem ser aprovados antes de poderem ser usados para autenticação.
- [x] [R14] Caso uma conta seja reprovada pelo gestor ou ainda não tenha sido analisada, quando o utilizador tentar usar essas credenciais deve surgir uma mensagem informativa.
- [x] [R15] Em qualquer momento um gestor pode inativar ou ativar uma conta de um utilizador.
- [x] [R16] Um utilizador pode solicitar que a sua conta seja removida do sistema. Neste caso deve surgir uma notificação aos gestores que podem aceitar ou recusar o pedido. Se o pedido for aceite, a informação pessoal do utilizador deve ser omitida do sistema, embora os registos associados ao utilizador devem ser mantidos.
- [x] [R17] Caso não existiam utilizadores criados na primeira execução, a aplicação deve solicitar credenciais para criar uma conta gestor.
- [x] [R18] Após a autenticação, a aplicação deve apresentar a mensagem `Bem-vindo [nome utilizador]`.
- [x] [R19] Quando a aplicação estiver a encerrar, deve apresentar a mensagem `Adeus [nome utilizador]`.

### Ações → Intervenientes e responsabilidades
- [ ] [R20] Os revisores gerem (aprovam e finalizam) o processo de revisão.
- [ ] [R21] Os autores submetem obras para revisão, que devem ser revistas por um ou mais revisores, e realizam o respetivo pagamento.
- [ ] [R22] Os revisores são responsáveis rela realização da revisão.
- [x] [R23] Cada obra para revisão é caracterizada pelo autor, título, subtítulo (opcional), estilo literário (e.g. drama, ficção, thriller), tipo de publicação (e.g. capa dura, de bolso) número de páginas, número de palavras, código ISBN, número de edição, data de submissão e data de aprovação. O código ISBN deve possuir um valor único.
- [x] [R24] Uma revisão está associada a uma obra, autor, gestor e revisor responsável.
- [x] [R25] Uma revisão é caracterizada por um número de série, composto por um número sequencial (a cada pedido o número incrementa), seguido da data no formato `AAAAMMDDHHMMSS`. Por exemplo, se já ocorreram 95 pedidos até ao momento, e às 10h20m10s do dia 21 de fevereiro de 2023 surge um novo pedido, o mesmo terá o número 9620230221102010.
- [x] [R26] Adicionalmente, uma revisão inclui a data da realização, o tempo decorrido (calculado automaticamente desde o início até ao fim da revisão) uma listagem de anotações realizadas, observações genéricas inseridas pelo revisor, um custo do processo e o seu estado.
- [x] [R27] As anotações são caracterizadas por uma descrição, a página e o parágrafo onde ocorrem e a data em que foram realizadas.

### Ações → Processo
- [x] [R28] O processo inicia com o autor, que solicita uma revisão para uma das suas obras. Cada pedido recebe um número ISBN aleatório entre 1 e 1 000 000.
- [x] [R29] O pedido de revisão tem de ser aprovado pelo gestor. O pedido pode ser aceite ou rejeitado. Se aceitar o pedido, o gestor deve atribuir um ou mais revisores. Deve sempre existir um revisor responsável. Se o pedido for rejeitado, o autor deve ser notificado e o processo arquivado.
- [ ] [R30] Quando um processo de revisão é atribuído a um revisor este deve ser notificado. O revisor pode aceitar ou rejeitar o processo.
- [ ] [R31] Caso um revisor rejeite um processo, o gestor responsável pelo processo deve ser notificado e selecionar outro revisor. Na nova listagem de possíveis revisores a atribuir, não deve surgir o(s) revisor(s) que rejeitou (rejeitaram) anteriormente o pedido.
- [ ] [R32] Após aceitar o processo, o revisor inicia a revisão. Quando concluir a tarefa, encerra essa revisão. A revisão não tem de ter terminada numa única sessão da aplicação.
- [ ] [R33] No final o autor realiza o pagamento do processo e o gestor será notificado.
- [ ] [R34] Após a confirmação do autor, o gestor arquiva o processo.
- [ ] [R35] Tanto o autor como o gestor podem consultar o estado de uma revisão.
> [!NOTE]
> a) iniciada (autor submeteu pedido, a aguardar autorização do gestor)\
> b) aceite (gestor aceitou o pedido, em espera de execução)\
> c) decorrer (revisor iniciou a revisão)\
> d) finalizada (revisor terminou a revisão)\
> e) arquivado (o gestor encerra o processo após o autor pagar o processo)
- [x] [R36] Cada autor pode possuir diversas obras e cada obra só está associada a um autor.
- [x] [R37] Sempre que uma nova obra é inserida no sistema deve-se verificar se o título já existe, bloqueando a sua inserção em caso de duplicação.
- [x] [R38] Uma obra pode possuir várias revisões.
- [ ] [R39] Para garantir que as obras não contêm plágio, são utilizadas aplicações específicas. Estas aplicações possuem um número limitado de licenças, com prazo de utilização (usualmente um ano). Cada revisão pode usar uma ou mais licenças.
- [x] [R40] O gestor é responsável pela inserção de licenças no sistema e atualização do seu número.

### Notificações
- [x] [R41] Quando um novo utilizador se registou na plataforma o gestor deve ser notificado.
- [ ] [R42] Sempre que existe um pedido de revisão o gestor deve ser notificado.
- [ ] [R43] Se um pedido de revisão for rejeitado, o autor deve ser notificado.
- [ ] [R44] Quando um pedido de revisão é atribuído a um revisor, este deve ser notificado.
- [ ] [R45] Se um revisor rejeitar um pedido, o gestor deve ser notificado.
- [ ] [R46] O gestor deve ser notificado quando um processo de revisão for confirmado/pago pelo autor.
- [ ] [R47] Sempre que um processo de revisão passou 10 dias sem ser finalizado, o gestor deve ser notificado.

### Listagens e pesquisas
- [x] [R48] Todas as listagens devem permitir visualizar os resultados por páginas de 10 registos cada.
- [x] [R49] Todas as listagens devem permitir ordenar os resultados, no sentido ascendente ou descendente.
- [x] [R50] Todas as listagens devem permitir realizar pesquisas para filtrar os resultados.
- [x] [R51] Os gestores podem listar todos os utilizadores, ordenando por nome.
- [x] [R52] Os gestores podem pesquisar utilizadores por nome, login ou tipo.
- [x] [R53] Os gestores podem listar todas os pedidos de revisão ordenados por data de criação, título de obra ou por autor.
- [ ] [R54] Os gestores podem listar todas os pedidos de revisão ainda não finalizados, ordenados por data.
- [ ] [R55] Os gestores podem pesquisar pedidos de revisão por identificador, estado ou autor.
- [ ] [R56] Os gestores podem pesquisar pedidos de revisão submetidos num intervalo temporal (período entre duas datas a indicar pelo utilizador).
- [ ] [R57] Os gestores podem listar os processos de revisão de qualquer obra a partir do título.
- [ ] [R58] Os autores podem listar os seus pedidos de revisão ordenando por data de criação ou por número de série.
- [ ] [R59] Os autores podem pesquisar os seus pedidos de revisão por data de criação, por título ou estado.
- [ ] [R60] Os autores podem listar as suas obras ordenadas por data de submissão ou por título.
- [ ] [R61] Os autores podem pesquisar as suas obras por data de registo ou matrícula.
- [ ] [R62] Os revisores podem listar os seus pedidos de revisão ordenando por data de criação ou por título.
- [ ] [R63] Os revisores podem pesquisar os seus pedidos de revisão por data de criação, por título ou estado.
- [x] [R64] Devem ser implementados mecanismos de pesquisa avançada, ou seja, apresentar todos os registos que apresentem um termo de pesquisa, mesmo que parcialmente.
> [!NOTE]
> (e.g., termo de pesquisa “Ana” deve apresentar como resultado “Ana Sousa”, “Ana Silva” e “Anabela”).

### Manipulação e gestão de bases de dados relacionais
- [ ] [R65] Acesso a uma base de dados relacional que permita gerir toda a informação necessária para a execução da aplicação.
- [ ] [R66] O acesso à aplicação deve ser restringido com credenciais (login/password), informação que deverá ser armazenada numa base de dados relacional.
- [ ] [R67] Os parâmetros de acesso à base de dados (IP, porto, nome da base de dados, login e password) devem ser armazenados num ficheiro de texto (Properties), sendo disponibilizada uma interface que possibilite a sua alteração antes do arranque da aplicação (antes do processo de autenticação). Estes parâmetros não necessitam de ser definidos em todas as execuções da aplicação, apenas quando o utilizador solicitar.

### Interação com o utilizador
- [ ] [R68] Disponibilizar uma interface em modo texto onde o utilizador possa interagir e controlar a aplicação. A organização da interface e escolha de componentes fica ao critério do aluno. Soluções eficientes de menus, respeitando as regras de interação com o utilizador serão bonificadas.
- [x] [R69] Calcular o tempo que a aplicação demora a executar a aplicação, desde o arranque até ao utilizador selecionar a opção de saída, apresentando, no final do processo, a seguinte informação:
```
Início do processo: Terça-Feira; 2023-03-01 11:21:11
Fim do processo:    Terça-Feira; 2023-03-01 11:21:56
Tempo de execução:  45132 Milissegundos (45 Segundos; 0 Minutos; 0 Horas)
```

### Monitorização de acessos
- [ ] [R70] Deverá ser mantida a informação genérica do sistema, como, por exemplo, a data da última execução da aplicação e o número total (até ao momento) de execuções do sistema.
- [ ] [R71] Deve existir um registo de ações (log) dos utilizadores no formato: `<data> <hora> <utilizador> <acção>`. O log deve ser armazenado numa base de dados relacional.
- [ ] [R72] Deve ser possível listar o conteúdo do log através da aplicação.
- [ ] [R73] Deve ser possível pesquisar registos por utilizador no log.

### Programação Orientada a Objetos
- [ ] [R74] A aplicação deve estar corretamente estruturada, tendo em conta o paradigma Orientado a Objetos, recorrendo à linguagem Java.
- [ ] [R75] Implemente as estruturas de armazenamento necessárias, procurando otimizar os recursos utilizados.

### Validação de dados e notificações
- [ ] [R76] Valide todas as leituras de dados do utilizador.
> [!NOTE]
> (e.g. verifique se os nomes são únicos)
- [ ] [R77] Sempre que necessário, apresentar ao utilizador mensagens informativas adequadas. Quando um utilizador realizar uma ação sobre a aplicação, esta deve informar se ação foi realizada com sucesso ou insucesso.