-- Dump de banco de dados (PostgreSQL) com valores aleatórios de exemplo
-- Projeto: teste (Pacientes, Medicamentos, Receitas)
-- Gerado em: 2025-10-06 19:08
-- Observação: execute em um banco vazio. Use psql:
--   psql -h <host> -U <usuario> -d <database> -f db-dump-random-postgres.sql

BEGIN;

-- Remoção das tabelas na ordem correta para evitar conflitos de chaves estrangeiras
DROP TABLE IF EXISTS medicamento_receitado CASCADE;
DROP TABLE IF EXISTS receita CASCADE;
DROP TABLE IF EXISTS medicamento CASCADE;
DROP TABLE IF EXISTS paciente CASCADE;

-- Tabela PACIENTE
CREATE TABLE paciente (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    cpf  VARCHAR(14)  NOT NULL,
    CONSTRAINT uk_paciente_cpf UNIQUE (cpf)
);

-- Tabela MEDICAMENTO
CREATE TABLE medicamento (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL
);

-- Tabela RECEITA
CREATE TABLE receita (
    id BIGSERIAL PRIMARY KEY,
    id_paciente BIGINT NOT NULL,
    CONSTRAINT fk_receita_paciente FOREIGN KEY (id_paciente)
        REFERENCES paciente(id)
);

-- Tabela MEDICAMENTO_RECEITADO
CREATE TABLE medicamento_receitado (
    id BIGSERIAL PRIMARY KEY,
    id_receita BIGINT NOT NULL,
    id_medicamento BIGINT NOT NULL,
    CONSTRAINT fk_item_receita FOREIGN KEY (id_receita) REFERENCES receita(id),
    CONSTRAINT fk_item_medicamento FOREIGN KEY (id_medicamento) REFERENCES medicamento(id)
);

-- Pacientes (30 registros)
INSERT INTO paciente (id, nome, cpf) VALUES
  (1,  'Ana Souza',            '102.334.455-01'),
  (2,  'Bruno Lima',           '213.987.654-02'),
  (3,  'Carla Mendes',         '321.654.987-03'),
  (4,  'Diego Silva',          '456.123.789-04'),
  (5,  'Eduarda Alves',        '567.890.123-05'),
  (6,  'Felipe Rocha',         '678.901.234-06'),
  (7,  'Gabriela Costa',       '789.012.345-07'),
  (8,  'Henrique Pereira',     '890.123.456-08'),
  (9,  'Isabela Fernandes',    '901.234.567-09'),
  (10, 'João Carvalho',        '112.233.445-10'),
  (11, 'Karen Duarte',         '223.344.556-11'),
  (12, 'Lucas Azevedo',        '334.455.667-12'),
  (13, 'Mariana Ribeiro',      '445.566.778-13'),
  (14, 'Nathan Oliveira',      '556.677.889-14'),
  (15, 'Olivia Martins',       '667.788.990-15'),
  (16, 'Paulo Teixeira',       '778.899.001-16'),
  (17, 'Queila Barros',        '889.900.112-17'),
  (18, 'Rafael Nogueira',      '990.011.223-18'),
  (19, 'Sofia Pacheco',        '135.792.468-19'),
  (20, 'Thiago Monteiro',      '246.813.579-20'),
  (21, 'Ulysses Prado',      '357.159.258-21'),
  (22, 'Vanessa Moraes',     '468.024.680-22'),
  (23, 'Wagner Tavares',     '579.135.791-23'),
  (24, 'Xavier Campos',      '680.246.802-24'),
  (25, 'Yara Gonçalves',     '791.357.913-25'),
  (26, 'Zilda Farias',       '802.468.024-26'),
  (27, 'Ângela Brito',       '913.579.135-27'),
  (28, 'Érico Leitão',       '024.680.246-28'),
  (29, 'Ícaro Queiroz',      '135.791.357-29'),
  (30, 'Órion Sales',        '246.802.468-30');

-- Medicamentos (20 registros)
INSERT INTO medicamento (id, nome) VALUES
  (1,  'Paracetamol'),
  (2,  'Ibuprofeno'),
  (3,  'Amoxicilina'),
  (4,  'Losartan'),
  (5,  'Omeprazol'),
  (6,  'Metformina'),
  (7,  'Sinvastatina'),
  (8,  'Dipirona'),
  (9,  'Azitromicina'),
  (10, 'Hidroclorotiazida'),
  (11, 'Cetirizina'),
  (12, 'Loratadina'),
  (13, 'Prednisona'),
  (14, 'Naproxeno'),
  (15, 'Atenolol'),
  (16, 'Captopril'),
  (17, 'Cloroquina'),
  (18, 'Claritromicina'),
  (19, 'Ranitidina'),
  (20, 'Diclofenaco');

-- Receitas (60 registros) atribuídas a pacientes pseudo-aleatoriamente
INSERT INTO receita (id, id_paciente) VALUES
  (1,  2),  (2,  3),  (3,  4),  (4,  5),  (5,  6),
  (6,  7),  (7,  8),  (8,  9),  (9, 10),  (10, 11),
  (11, 12), (12, 13), (13, 14), (14, 15), (15, 16),
  (16, 17), (17, 18), (18, 19), (19, 20), (20, 1),
  (21, 2),  (22, 3),  (23, 4),  (24, 5),  (25, 6),
  (26, 7),  (27, 8),  (28, 9),  (29, 10), (30, 11),
  (31, 12), (32, 13), (33, 14), (34, 15), (35, 16),
  (36, 17), (37, 18), (38, 19), (39, 20), (40, 1),
  (41, 21), (42, 22), (43, 23), (44, 24), (45, 25),
  (46, 26), (47, 27), (48, 28), (49, 29), (50, 30),
  (51, 21), (52, 22), (53, 23), (54, 24), (55, 25),
  (56, 26), (57, 27), (58, 28), (59, 29), (60, 30);

-- Itens de medicamentos por receita (todas as inserções unificadas)
-- Padrão base: para cada receita r, medicamentos ( (r-1)%20+1 ), ( (r+5)%20+1 ), ( (r+11)%20+1 ) e itens adicionais conforme descrito abaixo.
INSERT INTO medicamento_receitado (id_receita, id_medicamento) VALUES
  -- r=1..40 (base de 3 itens por receita)
  (1, 1), (1, 7), (1, 13),
  (2, 8), (2, 14),
  (3, 3), (3, 9), (3, 15),
  (4, 4), (4, 10), (4, 16),
  (5, 5), (5, 11), (5, 17),
  (6, 6), (6, 12), (6, 18),
  (7, 7), (7, 13), (7, 19),
  (8, 8), (8, 14), (8, 20),
  (9, 9), (9, 15), (9, 1),
  (10, 10), (10, 16), (10, 2),
  (11, 11), (11, 17), (11, 3),
  (12, 12), (12, 18), (12, 4),
  (13, 13), (13, 19), (13, 5),
  (14, 14), (14, 20), (14, 6),
  (15, 15), (15, 1),  (15, 7),
  (16, 16), (16, 2),  (16, 8),
  (17, 17), (17, 3),  (17, 9),
  (18, 18), (18, 4),  (18, 10),
  (19, 19), (19, 5),  (19, 11),
  (20, 20), (20, 6),  (20, 12),
  (21, 1),  (21, 7),  (21, 13),
  (22, 2),  (22, 8),  (22, 14),
  (23, 3),  (23, 9),  (23, 15),
  (24, 4),  (24, 10), (24, 16),
  (25, 5),  (25, 11), (25, 17),
  (26, 6),  (26, 12), (26, 18),
  (27, 7),  (27, 13), (27, 19),
  (28, 8),  (28, 14), (28, 20),
  (29, 9),  (29, 15), (29, 1),
  (30, 10), (30, 16), (30, 2),
  (31, 11), (31, 17), (31, 3),
  (32, 12), (32, 18), (32, 4),
  (33, 13), (33, 19), (33, 5),
  (34, 14), (34, 20), (34, 6),
  (35, 15), (35, 1),  (35, 7),
  (36, 16), (36, 2),  (36, 8),
  (37, 17), (37, 3),  (37, 9),
  (38, 18), (38, 4),  (38, 10),
  (39, 19), (39, 5),  (39, 11),
  (40, 20), (40, 6),  (40, 12),
  -- r=1..40 (itens adicionais: +2 por receita)
  (1, 4), (1, 10),
  (2, 5), (2, 11),
  (3, 6), (3, 12),
  (4, 7), (4, 13),
  (5, 8), (5, 14),
  (6, 9), (6, 15),
  (7, 10), (7, 16),
  (8, 11), (8, 17),
  (9, 12), (9, 18),
  (10, 13), (10, 19),
  (11, 14), (11, 20),
  (12, 15), (12, 1),
  (13, 16), (13, 2),
  (14, 17), (14, 3),
  (15, 18), (15, 4),
  (16, 19), (16, 5),
  (17, 20), (17, 6),
  (18, 1), (18, 7),
  (19, 2), (19, 8),
  (20, 3), (20, 9),
  (21, 4), (21, 10),
  (22, 5), (22, 11),
  (23, 6), (23, 12),
  (24, 7), (24, 13),
  (25, 8), (25, 14),
  (26, 9), (26, 15),
  (27, 10), (27, 16),
  (28, 11), (28, 17),
  (29, 12), (29, 18),
  (30, 13), (30, 19),
  (31, 14), (31, 20),
  (32, 15), (32, 1),
  (33, 16), (33, 2),
  (34, 17), (34, 3),
  (35, 18), (35, 4),
  (36, 19), (36, 5),
  (37, 20), (37, 6),
  (38, 1), (38, 7),
  (39, 2), (39, 8),
  (40, 3), (40, 9),
  -- r=41..60 (base de 3 itens por receita)
  (41, 1),  (41, 7),  (41, 13),
  (42, 2),  (42, 8),  (42, 14),
  (43, 3),  (43, 9),  (43, 15),
  (44, 4),  (44, 10), (44, 16),
  (45, 5),  (45, 11), (45, 17),
  (46, 6),  (46, 12), (46, 18),
  (47, 7),  (47, 13), (47, 19),
  (48, 8),  (48, 14), (48, 20),
  (49, 9),  (49, 15), (49, 1),
  (50, 10), (50, 16), (50, 2),
  (51, 11), (51, 17), (51, 3),
  (52, 12), (52, 18), (52, 4),
  (53, 13), (53, 19), (53, 5),
  (54, 14), (54, 20), (54, 6),
  (55, 15), (55, 1),  (55, 7),
  (56, 16), (56, 2),  (56, 8),
  (57, 17), (57, 3),  (57, 9),
  (58, 18), (58, 4),  (58, 10),
  (59, 19), (59, 5),  (59, 11),
  (60, 20), (60, 6),  (60, 12),
  -- r=41..60 (itens adicionais: +2 por receita)
  (41, 4),  (41, 10),
  (42, 5),  (42, 11),
  (43, 6),  (43, 12),
  (44, 7),  (44, 13),
  (45, 8),  (45, 14),
  (46, 9),  (46, 15),
  (47, 10), (47, 16),
  (48, 11), (48, 17),
  (49, 12), (49, 18),
  (50, 13), (50, 19),
  (51, 14), (51, 20),
  (52, 15), (52, 1),
  (53, 16), (53, 2),
  (54, 17), (54, 3),
  (55, 18), (55, 4),
  (56, 19), (56, 5),
  (57, 20), (57, 6),
  (58, 1),  (58, 7),
  (59, 2),  (59, 8),
  (60, 3),  (60, 9),
  -- Desbalanceamento intencional para o relatório
  (20, 1), (20, 1), (20, 1), (20, 1),
  (40, 1), (40, 1), (40, 1), (40, 1),
  (1, 1), (1, 1), (1, 1),
  (21, 1), (21, 1), (21, 1),
  (2, 2), (2, 2),
  (22, 2), (22, 2),
  (41, 2), (41, 2), (41, 2), (41, 2), (41, 2), (41, 9),
  (51, 2), (51, 2), (51, 2), (51, 2), (51, 2), (51, 9);





-- Ajuste das sequências para ficarem alinhadas com os IDs máximos
-- Desbalanceamento intencional para o relatório: adiciona itens extras
-- Objetivo: garantir que os totais exibidos no relatório "medicamentos-prescritos" fiquem diferentes
-- Estratégia:
--  - Aumentar a incidência dos medicamentos 1 (Paracetamol) e 2 (Ibuprofeno)
--  - Atribuir itens extras a receitas de pacientes específicos para variar o total por paciente
-- Observação: permitir itens repetidos por receita (não há restrição de unicidade)

-- Ajuste das sequências para ficarem alinhadas com os IDs máximos após todos os inserts
SELECT setval(pg_get_serial_sequence('paciente','id'), (SELECT COALESCE(MAX(id), 1) FROM paciente));
SELECT setval(pg_get_serial_sequence('medicamento','id'), (SELECT COALESCE(MAX(id), 1) FROM medicamento));
SELECT setval(pg_get_serial_sequence('receita','id'), (SELECT COALESCE(MAX(id), 1) FROM receita));
SELECT setval(pg_get_serial_sequence('medicamento_receitado','id'), (SELECT COALESCE(MAX(id), 1) FROM medicamento_receitado));

COMMIT;
