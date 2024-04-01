/* Populate database with 15 pre-generated users */
INSERT INTO UTILIZADORES (ID_UTILIZADOR, NOME, USERNAME, PASSWORD, SALT, ESTADO, EMAIL, TIPO, CONTRIBUINTE, TELEFONE, MORADA, ESTILO_LITERARIO, DATA_INICIO, AREA_ESPECIALIZACAO, FORMACAO_ACADEMICA)
VALUES
/* Managers - password: Adm1n-pass */
(1, 'Nelia Carmo', 'admin-log', 'oPM6sSalNE/oNZ8dVrjT7g==', unhex('6ec49afee7a55118e4d76fd382419459'), 'active', 'n.carmo@hotmail.com', 'manager', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(2, 'Francisco Pais', 'admin2-log', 'ihwNsLeCZwMrOIKJx5fkYw==', unhex('68122a5194bdb0bc15354e05f3f4043f'), 'inactive', 'francisco.pais@gmail.com', 'manager', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(7, 'Elisa Freire', 'admin3-log', 'sFQdMDCSSDVBSlm50d6f7w==', unhex('4e133274f844f436521447122f9e251c'), 'active', 'elisafreire@hotmail.com', 'manager', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(15, 'Carlos Manuel Câmara', 'admin4-log', '1zcpnt3xiEtz6oPk8rsVlw==', unhex('9b0cf450182b7fe97ef501bbd2ced22d'), 'inactive', 'c.m.câmara@gmail.com', 'manager', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
/* Authors - password: Auth-pa5s */
(5, 'Almerinda Pedroso', 'auth-log', 'vVSiszHPfIoupexbPQsM4g==', unhex('16eeefc9eb05ea9747f119a3efda6895'), 'active', 'almerindap@gmail.com', 'author', '234810408', '289127663', 'R Armazéns 103, Porto', 'Science Fiction', '2017-10-12', NULL, NULL),
(6, 'Fernandes Faria', 'auth2-log', 'WORDbC62C27idL+NXJZbrA==', unhex('15f8deb9575db81b6c135b232dc967c7'), 'pending-activation', 'fernandesfaria@gmail.com', 'author', '273643177', '965559582', 'Rua Doutor Teófilo Braga 9, Évora', 'Romance', '2019-02-14', NULL, NULL),
(8, 'Elias Filipe', 'auth3-log', 'DcSp7BTtpLKYoL8ZXCxRGQ==', unhex('c87a4e932f9356a3d0aa7993a49cb420'), 'inactive', 'eliasf@hotmail.com', 'author', '279585284', '922555710', 'Avenida Boavista 59, Viana do Castelo', 'Mystery', '1988-04-05', NULL, NULL),
(10, 'Leandro Ramalho', 'auth4-log', '0sY93P+5CTeoEOLCDi7G5A==', unhex('0812e96c571a8d1f085152f7d1cde052'), 'active', 'leandro.ramalho@gmail.com', 'author', '269908668', '272555253', 'R Moura 59, Beja', 'Thriller', '1990-08-28', NULL, NULL),
(13, 'Lucília Catarino', 'auth5-log', 'LyiNNPB3RcBuU/gZe0/kPQ==', unhex('affa4c95af4830013f7c23262df99147'), 'pending-deletion', 'lucilia.catarino@hotmail.com', 'author', '201261235', '935557687', 'Rua Vale Formoso 34, Faro', 'Historical Fiction', '2009-06-25', NULL, NULL),
(14, 'Ronaldo Couto', 'auth6-log', 'FNiJFXHrUXnVi+ewwlJtIA==', unhex('123cba6daea0ca795174a11c80ea2a74'), 'pending-activation', 'ronaldo.couto@hotmail.com', 'author', '239716744', '915550759', 'Avenida José Costa Mealha 3, Faro', 'Fantasy', '1991-06-06', NULL, NULL),
/* Reviewers - password: R3v-pass */
(3, 'Ermelinda Frazão', 'rev-log', '0xR8mSfVrMSD/By3ees2XQ==', unhex('f88a1cdfe5c1101888a75b9782badb12'), 'pending-deletion', 'e.frazão@hotmail.com', 'reviewer', '231354460', '207555157', 'R Miguel Bombarda 39, Leiria', NULL, NULL, 'Science Fiction', 'MSc in Data Science'),
(4, 'Telma Ramalho', 'rev2-log', 'j9bp3ACtgGLKHi8XbWNRSw==', unhex('ca694b3c0d049d19ab18a4b44c2b560c'), 'active', 'telmaramalho@gmail.com', 'reviewer', '209448032', '935551998', 'R Doutor Alfredo Freitas 51, Aveiro', NULL, NULL, 'Drama', 'MFA in Dramatic Writing'),
(9, 'Caio Rolo', 'rev3-log', 'vuyOV5Bz0sVii+DdMBysiw==', unhex('c0307da8bb8f5e7cde268a2bec2220b7'), 'pending-activation', 'crolo@gmail.com', 'reviewer', '269539131', '915558640', 'R Cerdeira Ervas 99, Braga', NULL, NULL, 'Fiction', 'PhD in Creative Writing'),
(11, 'Marcos Alves', 'rev4-log', 'XtopJiWK/feqwLcRYHhgng==', unhex('61af4bc6e38428c0ed5177813c8161f6'), 'inactive', 'marcos.alves@hotmail.com', 'reviewer', '205009034', '965553698', 'R Muro Bacalhoeiros 14, Porto', NULL, NULL, 'Poetry', 'MSc in Digital Humanities'),
(12, 'Virgílio Vidal', 'rev5-log', 'neHkJvlliqnFEpFGCuUiHQ==', unhex('6fdde7d01d686c25374b7e4a7cf3cfbb'), 'pending-activation', 'vvidal@gmail.com', 'reviewer', '251289060', '965553211', 'R Doutor Manuel Arriaga 3, Lisboa', NULL, NULL, 'Non-Fiction', 'BA in Media Studies');

/* Populate database with 10 pre-generated books */
INSERT INTO OBRAS (ID_OBRA, ID_UTILIZADOR, TITULO, SUBTITULO, ESTILO_LITERARIO, TIPO_PUBLICACAO, N_PAGINAS, N_PALAVRAS, CODIGO_ISBN, N_EDICAO, DATA_SUBMISSAO, DATA_APROVACAO)
VALUES
(1, 5, 'O Planeta dos Macacos', 'A Revolta', 'Science Fiction', 'Livro', 548, 246835, '9795848929057', 1, '2011-08-24', NULL),
(2, 6, 'O Amor nos Tempos de Cólera', NULL, 'Romance', 'Livro', 223, 100763, '9797730188962', 1, '2016-08-11', NULL),
(3, 8, 'O Caso dos Dez Negrinhos', NULL, 'Mystery', 'Livro', 194, 87675, '9783253429576', 2, '2020-11-20', NULL),
(4, 10, 'O Código Da Vinci', NULL, 'Thriller', 'Livro', 226, 101897, '9780537153905', 1, '2021-03-08', NULL),
(5, 13, 'O Nome da Rosa', NULL, 'Historical Fiction', 'Livro', 290, 130622, '9781163856420', 1, '2024-06-12', NULL),
(6, 14, 'O Senhor dos Anéis', 'A Sociedade do Anel', 'Fantasy', 'Livro', 343, 154452, '9785626827774', 1, '2015-02-20', NULL),
(7, 8, 'A Sombra do Vento', NULL, 'Mystery', 'Livro', 555, 249760, '9798561577086', 3, '2021-12-15', NULL),
(8, 5, 'O Homem Duplicado', NULL, 'Science Fiction', 'Livro', 215, 96990, '9793418608333', 2, '2018-04-11', NULL),
(9, 6, 'O Velho e o Mar', NULL, 'Romance', 'Livro', 428, 192710, '9797771257658', 1, '2010-06-08', NULL),
(10, 10, 'O Cemitério dos Livros Esquecidos', NULL, 'Thriller', 'Livro', 314, 141384, '9780302366301', 1, '2022-06-30', NULL);

/* Populate database with 10 pre-generated licenses */
INSERT INTO LICENCAS (ID_LICENCA, NUMERO, DATA_INICIO, DATA_FIM, N_DISPONIVEL, COMENTARIOS)
VALUES
/* Valid as of 2024-04-01 */
(1, 'AB74920D', '2024-02-22', '2025-02-22', 3, NULL),
(3, 'AC123456', '2023-11-30', '2024-11-30', 2, 'Valid test License'),
(6, 'AF987654', '2023-06-30', '2024-06-30', 3, NULL),
/* Expired */
(2, 'AH767320', '2023-01-13', '2024-01-13', 5, 'Expired test License'),
(4, 'AD987654', '2021-09-15', '2022-09-15', 1, NULL),
(5, 'AE123456', '2021-08-01', '2022-08-01', 4, NULL),
(7, 'AG123456', '2020-05-15', '2021-05-15', 2, NULL),
(8, 'AH987654', '2019-03-31', '2020-03-31', 1, NULL),
(9, 'AI123456', '2019-02-15', '2020-02-15', 5, NULL),
(10, 'AJ987654', '2018-01-01', '2019-01-01', 4, NULL);