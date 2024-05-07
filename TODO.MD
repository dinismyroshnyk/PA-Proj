## Introdução

Neste projecto, pretende-se a implementação de uma aplicação completa em linguagem Java, sem componente gráfica para interacção com o utilizador, recorrendo ao paradigma de Programação Orientada a Objectos e comunicação em rede. \
Espera-se o desenvolvimento e aplicação de conhecimentos e competências relacionadas com as temáticas abordadas na disciplina, com especial incidência para a:
- programação orientada a objectos;
- uso de ficheiros e bases de dados relacionais para armazenamento de dados de forma persistente;
- comunicação em rede através de Sockets;
- bem como o desenvolvimento de interfaces para interacção com o utilizador (em modo texto).

## Objectivos

### Descrição global da aplicação:

- [ ] [R1] Deve ser disponibilizada uma aplicação cliente que permita realizar as acções associadas ao utilizador autor, nomeadamente:
    - [ ] [R1.1] Autenticar-se, embora não necessite de implementar o registo;
    - [ ] [R1.2] Consultar e alterar informação pessoal;
    - [ ] [R1.3] Inserir informação de uma obra;
    - [ ] [R1.4] Pesquisar e consultar informação de uma obra;
    - [ ] [R1.5] Pesquisar e consultar pedido de uma revisão;
    - [ ] [R1.6] Listar as obras associadas ao autor;
    - [ ] [R1.7] Listar os pedidos de revisão.
- [ ] [R2] A aplicação cliente não deve armazenar localmente qualquer informação:
    - [ ] [R2.1] A aplicação cliente deve comunicar através da rede com a aplicação servidor para manipular qualquer informação, armazenada na base de dados.
    - [ ] [R2.2] A aplicação cliente deve implementar o nó cliente, enquanto a aplicação servidor deve implementar o nó servidor.
    - [ ] [R2.3] A aplicação servidor corresponde à aplicação desenvolvida no projecto anterior e que deve ser adaptada, de forma a comunicar através da rede.
- [ ] [R3] Não é necessário garantir que a aplicação servidor realize outras funções no momento em que comunica com aplicação cliente. Ou seja, não é necessário um ambiente multithreading.
- [ ] [R4] A aplicação servidor deve informar o utilizador dos pedidos que recebe através da rede e da informação que envia à aplicação cliente.

### Comunicação em rede:

- [ ] [R5] No caso de nó servidor, a aplicação fica a aguardar o pedido de ligação de uma aplicação cliente (nó cliente). No caso de nó cliente, a aplicação realiza um pedido ao nó servidor.
- [ ] [R6] Na opção de nó servidor deve ser possível indicar o `porto` para receber pedidos de ligação.
- [ ] [R7] Na opção de nó cliente deve ser possível indicar o `IP` e `porto` para remeter o pedido de ligação.
- [ ] [R8] Tanto no nó servidor como nó cliente a aplicação deve indicar o IP local da respectiva máquina.
- [ ] [R9] Quando um servidor aceita um pedido de ligação de um cliente, tanto o servidor como o cliente devem apresentar essa informação na interface.
- [ ] [R10] O nó servidor armazena toda a informação na base de dados. Por seu lado, o nó cliente não armazena qualquer informação.
- [ ] [R11] Após o estabelecimento de ligação todas as mensagens remetidas pelo nó servidor e pelo nó cliente devem ser iniciadas com a expressão `<login>`, onde `login` representa o `cliente` ou `servidor`. Desta forma, tanto o nó cliente como o nó servidor sabem qual é o interlocutor remoto.
- [ ] [R12] Todas as mensagens terminam com ponto-e-vírgula (`;`). Todas as mensagens devem incluir os caracteres `<` e `>`, bem como os espaços indicados.

### Sequência de comunicação:

- [ ] [R13] A primeira mensagem logo após o estabelecimento de comunicação deverá ser iniciada pelo servidor com a sintaxe `<login> <hello>;`. O cliente responderá da mesma forma. Se o servidor não receber como resposta uma mensagem do tipo `<hello>`, deve remeter novamente a sua mensagem `<hello>` até receber a resposta correcta. Se o servidor receber a resposta correcta do cliente deve enviar o comando `<login> <ack>;`.
- [ ] [R14] A comunicação termina quando o nó cliente envia a mensagem `<login> <bye>;`.
- [ ] [R15] O nó cliente envia pedidos ao nó servidor com a seguinte estrutura: `<login> <acção> [<argumentos>];` descritos nos pontos seguintes. O nó servidor responde com um comando semelhante, disponibilizando a informação solicitada.
- [ ] [R16] Para autenticar, o nó cliente envia o comando `<login> <autenticar> <username,password>;`. Caso a autenticação seja realizada com sucesso, o nó servidor responde com `<login> <autenticar> <success>;`. Caso contrário, o nó servidor responde com `<login> <autenticar> <fail>;`.
- [ ] [R17] Para consultar os dados pessoais, o nó cliente envia o comando `<login> <info>;`. O nó servidor responde com `<login> <info> <username,password,nome,email,estado,nif,telefone,morada>;`.
- [ ] [R18] Para alterar os dados pessoais, o nó cliente envia o comando `<login> <update> <username,password,nome,email,estado,nif,telefone,morada>;`. O nó servidor responde com `<login> <update> <ok>;`.
- [ ] [R19] Para inserir uma nova obra, o nó cliente envia o comando `<login> <inserir> <obra> <titulo,estilo_literario,tipo,num_paginas,num_palavras,ISBN,num_edicao>;`. O autor da obra deve ser o utilizador autenticado e a data de submissão a data do sistema. O nó servidor responde com `<login> <inserir> <obra> <ok>;`.
- [ ] [R20] Para pesquisar uma obra, o nó cliente envia o comando `<login> <pesquisa> <obra> <titulo>;`. O nó servidor responde com `<login> <pesquisa> <obra> <titulo,estilo_literario,tipo,num_paginas,num_palavras,ISBN,num_edicao,data_submissao>;`. Caso não exista a obra o nó servidor responde com `<login> <pesquisa> <obra> <fail>;`.
- [ ] [R21] Para pesquisar uma revisão, o nó cliente envia o comando `<login> <pesquisa> <revisao> <num_serie>;`. O nó servidor responde com `<login> <pesquisa> <revisao> <gestor,revisor,data_realizacao,tempo_decorrido,observacoes,custo,estado>;`. Caso não exista a revisão o nó servidor responde com `<login> <pesquisa> <revisão> <fail>;`.
- [ ] [R22] Para listar todas as obras do autor, o nó cliente envia o comando `<login> <listar> <obra>;`. O nó servidor responde com `<login> <listar> <obra> <obra1,obra2,…,obraN>;`. Onde `obraX` corresponde à informação de cada obra associada ao autor, sendo uma listagem. Caso não exista a obra o nó servidor responde com `<login> <listar> <obra> <fail>;`.
- [ ] [R23] Para listar todas as revisões associadas ao autor, o nó cliente envia o comando `<login> <listar> <revisao>;`. O nó servidor responde com `<login> <listar> <revisao> <revisao1, revisao2,…, revisaoN>;`. Onde `revisaoM` corresponde à informação de cada revisão associada a obras do autor, sendo uma listagem. Caso não exista a obra o nó servidor responde com `<login> <listar> <revisao> <fail>;`.
- [ ] [R24] Sempre que o nó cliente recebe informação do nó servidor, deve responder com o comando `<login> <ack>;`. Por sua vez, o nó servidor deve responder com `<login> <ack>;`.

### Gestão geral da aplicação:

#### Armazenamento persistente de dados:

- [ ] [R25] Devem ser implementados métodos de armazenamento persistentes dos dados do lado do servidor, desenvolvidos no projecto anterior. Nenhuma informação deve ser armazenada do lado cliente.

#### Interacção com o utilizador:

- [ ] [R26] Disponibilizar uma interface em modo texto onde o utilizador possa interagir e controlar a aplicação. A organização da interface e escolha de componentes fica ao critério do aluno. Soluções eficientes de menus, respeitando as regras de interacção com o utilizador serão bonificadas.

## Implementação

O programa deve ser implementado na linguagem Java. Lembre-se que é uma linguagem orientada a objectos, pelo que deverá ter em conta os seguintes aspectos:
- Cada classe deve gerir internamente os seus dados, pelo que deverá cuidar da protecção das suas variáveis e métodos.
- Cada objecto deverá ser responsável por uma tarefa ou objectivo específico, não lhe devendo ser atribuídas funções indevidas.
- Utilize a keyword static apenas quando tal se justifique e não para evitar erros do compilador.
- Recomenda-se que elabore um diagrama com as suas classes e objectos antes de iniciar o projecto, para prever a estrutura do projecto.

Tenha ainda em conta os seguintes pontos que serão importantes na avaliação:
- Comente as classes, métodos e variáveis públicas segundo o formato Javadoc. Isto permitir-lhe-á gerar automaticamente uma estrutura de ficheiros HTML, descritivos do seu código, que deve incluir no seu relatório.
- Comente o restante código sempre que a leitura dos algoritmos não seja óbvia.
- Tal como sugerido acima, evite o uso abusivo de static e de variáveis e métodos public.
- Na escolha de nomes para variáveis, classes e métodos siga as convenções adoptadas na linguagem Java.
- Procure uma interface agradável com o utilizador (na obtenção de dados e disponibilização de informação).
- As entradas de dados por parte do utilizador deverão ser testadas e protegidas contra erros ou falhas que possam surgir.