📚 Trabalho Prático: Sistema de Gestão de Biblioteca Universitária

Este projeto foi desenvolvido utilizando Spring Boot 3 com o objetivo de modelar e implementar corretamente diversas estruturas de relacionamentos e regras de negócio complexas, conforme as exigências da ADO 1.

O sistema simula a gestão de acervo, usuários, empréstimos, reservas e cobrança de multas em um ambiente universitário.

<img width="293" height="207" alt="image" src="https://github.com/user-attachments/assets/557bae6d-83ab-4e2c-932d-244c882495e3" />


1. Domínio Modelado e Entidades
   
O domínio escolhido é a Gestão de Biblioteca. A modelagem foi desenhada para garantir a abrangência de todas as estruturas de relacionamentos e chaves exigidas.

![licensed-image](https://github.com/user-attachments/assets/78b86178-6233-4680-92c6-d041121bffd3)

2. Diagrama e Implementação dos Relacionamentos
   
O projeto utiliza a seguinte estrutura para mapear as relações no JPA:
<img width="1092" height="869" alt="image" src="https://github.com/user-attachments/assets/64444614-7fe7-4ff0-8bf9-75d12f36d7a4" />


🔑 Estruturas de Chaves Especiais

<img width="517" height="133" alt="image" src="https://github.com/user-attachments/assets/994551bd-fdc3-4092-9ed2-db219b9eaef6" />


🔗 Tipos de Relacionamento

Relação N:N (Muitos para Muitos): Implementada entre Livro e Autor usando a entidade intermediária LivroAutor.

Relação 1:N (Um para Muitos): Implementada em Usuario → Emprestimo e Livro → Emprestimo.

3. Operações Lógicas e Regras de Negócio (Diferenciais)
   
O projeto vai além do CRUD tradicional, implementando regras essenciais:

<img width="520" height="469" alt="image" src="https://github.com/user-attachments/assets/175aa6e2-9398-4a14-9b48-31236d1b0313" />

4. Como Rodar e Exemplos de Uso da API
   
💻 Stack Tecnológica

Framework: Spring Boot 3.x

Construção: Maven

Banco de Dados: H2 Database (In-Memory, para desenvolvimento)

Interface: Thymeleaf (MVC) e REST API (JSON)

🚀 Instruções

Clone o repositório.

Construa o projeto com Maven: mvn clean install

Execute a aplicação: java -jar target/sistema-biblioteca-1.0.0.jar

Acesse o sistema no navegador: http://localhost:8080/

Exemplo de Chamada REST (Para testar o backend)

<img width="525" height="235" alt="image" src="https://github.com/user-attachments/assets/62bb390d-f01a-4965-8430-5c8955af9069" />


