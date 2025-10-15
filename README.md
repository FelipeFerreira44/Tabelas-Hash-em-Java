# Tabelas-Hash-em-Java

### PONTIFÍCIA UNIVERSIDADE CATÓLICA DO PARANÁ

**Matéria:** Resolução de Problemas Estruturados em Computação  
**Curso:** Ciência da Computação  
**Professor:** Andrey Cabral Meira  

**Nome dos Estudantes:**  
- Felipe Willian Barros Ferreira  
- André Vinícius Martins de Souza Acosta de Jesus  
- Luis Gustavo Freitas Kulzer  

**Nome no GitHub:** FelipeFerreira44  

---

##  Descrição do Projeto

Este projeto tem como objetivo **implementar e analisar o desempenho de diferentes tabelas hash em Java**, utilizando tanto **estratégias de rehashing** (endereçamento aberto) quanto **encadeamento**.  
A atividade envolve a geração de datasets determinísticos com **seeds**, análise de tempo de inserção e busca, **contagem de colisões**, e estudo estatístico de gaps e listas encadeadas.

O trabalho segue integralmente os requisitos do **RA3**, conforme enunciado oficial da disciplina.

---

##  Requisitos do Projeto

- Implementar **três variações de tabelas hash**, sendo:
  - Pelo menos **uma com rehashing** (linear, quadrático ou duplo)
  - Pelo menos **uma com encadeamento**
  - Uma terceira variação livre, a critério do grupo

- Gerar **três conjuntos de dados** com:
  - 100 mil registros  
  - 1 milhão de registros  
  - 10 milhões de registros  

- Utilizar **seeds fixas** para garantir a reprodutibilidade dos resultados  
- Medir:
  - Tempo de inserção
  - Tempo de busca
  - Número de colisões
  - Tamanho das listas encadeadas
  - Gaps entre elementos (endereçamento aberto)

- Apresentar resultados em **tabelas e gráficos** no relatório final



---

## Como executar
(execute nesta ordem):

# 1. Compilar
javac -d bin src/*.java

# 2. Executar testes (ISSO É O PRINCIPAL)
java -cp bin ExecutorTestes

# 3. Analisar resultados
java -cp bin AnalisadorResultados

# 4. Gerar gráficos (opcional)
python3 gerar_graficos_reais.py

# 5. Ver gráficos
open resultados/grafico_tempo.png
