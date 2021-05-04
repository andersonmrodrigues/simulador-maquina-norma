# simulador-maquina-norma

# Objetivo:
- Ampliar a compreensão de máquinas universais, computação e programas
por meio do desenvolvimento de um simulador de máquina Norma.
# Especificações:
- Deve ser possível executar programas monolíticos com uma quantidade de
registradores escolhida pelo usuário ao iniciar o software. O usuário deve poder
inicializar esses registradores com os valores que desejar, pois essa será a entrada
da computação).
- Além das funcionalidades mínimas devem ser escolhidas 2 dentre as 4
opções de funcionalidades optativas.
- A linguagem para implementação do simulador é livre, bem como o sistema
operacional no qual ele irá executar.
# Funcionalidades mínimas:
-  Aceitar como entrada (digitada em uma GUI ou console ou ainda via leitura
de um arquivo texto) um programa monolítico com instruções rotuladas como
o exemplo apresentado a seguir:
- 1: se zero_b então vá_para 9 senão vá_para 2
- 2: faça ad_a vá_para 3
- 3: faça ad_a vá_para 4
- 4: faça sub_b vá_para 1
• Tal como a especificação da máquina Norma, o simulador deve possibilitar:
# Teste:
- x_zero: Testa se o registrador x vale zero. Exemplos: a_zero testa
o registrador a, b_zero testa o registrador b e assim por diante;
- Operações:
    - ad_x: Incrementa o registrador x. Exemplo ad_c faz o registrador c
aumentar uma unidade;
    - sub_x: Decrementa o registrador x. Exemplo sub_d faz o
registrador d diminuir uma unidade;
- Como saída deve ser apresentada a computação completa.
# Funcionalidades optativas (escolher 2):
- Possibilitar executar um programa recursivo (no formato trabalhado em aula);
- Possibilitar informar um programa iterativo (no formato trabalhado em aula) e
traduzi-lo para um programa monolíco que por usa vez pode ser executado
na máquina;
- Possibilitar a leitura de arquivos com macros e disponibilizá-los ao
desenvolvedor do programa;
- Implementar (e disponibilizar ao programador) tipos de dados de máquinas
reais que podem ser simuladas em Norma (como números inteiros, racionais,
arranjos, ou cadeia de caracteres);
- Desenvolver (e apresentar o funcionamento) de um programa para realizar a
codificação e decodificação de conjuntos estruturados.