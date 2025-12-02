// Funções utilitárias para a aplicação
document.addEventListener('DOMContentLoaded', function() {
    console.log('Sistema de Biblioteca carregado!');

    // Adicionar comportamentos comuns aqui
    inicializarTooltips();
    inicializarConfirmacoes();
});

function inicializarTooltips() {
    // Inicializar tooltips se estiver usando alguma biblioteca (ex: Bootstrap)
    // console.log('Tooltips inicializados');
}

function inicializarConfirmacoes() {
    // Comportamento padrão para botões de confirmação (ex: deletar)
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

// Função para mostrar loading (Pode ser melhorada com um Spinner visual no CSS)
function mostrarLoading(mensagem = 'Carregando...') {
    document.body.style.cursor = 'wait'; // Muda o cursor para "aguardando"
    console.log('⏳ ' + mensagem);
}

function esconderLoading() {
    document.body.style.cursor = 'default'; // Volta o cursor ao normal
    console.log('✅ Carregamento finalizado.');
}

// Função para formatar datas no padrão brasileiro
function formatarData(data) {
    if (!data) return '';
    return new Date(data).toLocaleDateString('pt-BR');
}

// --- FUNÇÃO DE REQUISIÇÃO (CORE) ---
// Faz requisições API com tratamento de erro e redirecionamento de login
async function fazerRequisicao(url, options = {}) {
    try {
        mostrarLoading();
        
        const response = await fetch(url, {
            headers: {
                'Content-Type': 'application/json',
                // Não precisa adicionar Authorization aqui, o Cookie vai sozinho!
                ...options.headers
            },
            ...options
        });

        // 1. TRATAMENTO DE SESSÃO EXPIRADA
        if (response.status === 401 || response.status === 403) {
            alert('Sua sessão expirou. Por favor, faça login novamente.');
            window.location.href = '/login'; // Redireciona para o login
            return null;
        }

        // 2. TRATAMENTO DE ERROS GENÉRICOS (400, 500, etc)
        if (!response.ok) {
            // Tenta ler a mensagem de erro do servidor, se houver
            let errorMsg = `Erro ${response.status}: ${response.statusText}`;
            try {
                const errorBody = await response.text(); // Tenta ler como texto
                if (errorBody) errorMsg = errorBody;     // Usa a mensagem do backend se existir
            } catch (e) { /* ignora erro de parse */ }
            
            throw new Error(errorMsg);
        }

        // 3. TRATAMENTO DE RESPOSTAS (JSON vs VAZIO)
        // Verifica se a resposta tem conteúdo antes de tentar converter para JSON
        const contentType = response.headers.get("content-type");
        if (contentType && contentType.includes("application/json")) {
            return await response.json();
        }
        
        // Se não for JSON (ex: resposta vazia de um DELETE), retorna null ou texto
        return null;

    } catch (error) {
        console.error('Erro na requisição:', error);
        alert('❌ Ocorreu um erro: ' + error.message);
        throw error;
    } finally {
        esconderLoading();
    }
}