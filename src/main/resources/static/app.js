// Funções utilitárias para a aplicação
document.addEventListener('DOMContentLoaded', function() {
    console.log('Sistema de Biblioteca carregado!');

    // Adicionar comportamentos comuns aqui
    inicializarTooltips();
    inicializarConfirmacoes();
});

function inicializarTooltips() {
    // Inicializar tooltips se estiver usando alguma biblioteca
    console.log('Tooltips inicializados');
}

function inicializarConfirmacoes() {
    // Comportamento padrão para botões de confirmação
    const botoesConfirmacao = document.querySelectorAll('[data-confirm]');
    botoesConfirmacao.forEach(botao => {
        botao.addEventListener('click', function(e) {
            const mensagem = this.getAttribute('data-confirm');
            if (!confirm(mensagem)) {
                e.preventDefault();
            }
        });
    });
}

// Função para mostrar loading
function mostrarLoading(mensagem = 'Carregando...') {
    // Implementar overlay de loading
    console.log(mensagem);
}

// Função para formatar datas
function formatarData(data) {
    return new Date(data).toLocaleDateString('pt-BR');
}

// Função para fazer requisições API com tratamento de erro
async function fazerRequisicao(url, options = {}) {
    try {
        mostrarLoading();
        const response = await fetch(url, {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        });

        if (!response.ok) {
            throw new Error(`Erro ${response.status}: ${response.statusText}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Erro na requisição:', error);
        alert('Erro: ' + error.message);
        throw error;
    } finally {
        // Esconder loading
    }
}