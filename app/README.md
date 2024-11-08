## Requisitos do Aplicativo

Criar um aplicativo em Android que utilize o banco de dados SQLite para realizar operações CRUD (inserir, selecionar, atualizar e apagar) de registros de alunos. Além das funcionalidades básicas de CRUD, o aplicativo deve incluir campos de endereço e uma funcionalidade de mapa que marque a localização geográfica de cada aluno.

### Funcionalidades Principais

1. **CRUD de Alunos**: O aplicativo deve permitir a criação, leitura, atualização e exclusão de alunos no banco de dados.
2. **Campos de Endereço**: Além dos campos básicos de aluno, o modelo de dados deve incluir os seguintes campos para endereço:
    - Rua
    - Número
    - Cidade
    - Estado
    - País
3. **Marcação no Mapa**: As informações de endereço serão usadas para exibir a localização geográfica do aluno em um mapa.
4. **Navegação para o Mapa**: A interface deve incluir um botão que, quando um aluno está selecionado, leva a uma visualização do mapa com a localização do aluno marcada.
5. **Feedback de Carregamento**: Enquanto o mapa carrega, o aplicativo deve exibir um `SeekBar` ou `ProgressBar` para informar o usuário do progresso do carregamento.

### Modelo de Dados do Aluno

- `id` (inteiro, autoincrementável)
- `nome` (varchar, 100 caracteres)
- `email` (varchar, 100 caracteres)
- `rua` (varchar)
- `número` (varchar)
- `cidade` (varchar)
- `estado` (varchar)
- `país` (varchar)

### Estrutura do Projeto

1. **Banco de Dados SQLite**: Estrutura o banco de dados com as tabelas e colunas necessárias para os dados do aluno e os campos de endereço.
2. **Interface do Usuário**:
    - Formulário para entrada de dados de aluno e endereço.
    - Listagem dos alunos cadastrados com possibilidade de seleção.
    - Botão para abrir o mapa com a localização do aluno selecionado.
3. **Integração com Mapa**:
    - Utilização da API do Google Maps (ou equivalente) para marcar o endereço do aluno em um mapa.
    - Conversão do endereço em coordenadas geográficas (geocodificação) para a marcação.
4. **Indicador de Carregamento**: `SeekBar` ou `ProgressBar` para indicar o carregamento do mapa, melhorando a experiência do usuário.
