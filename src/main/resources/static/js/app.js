/* ============================================
   Funções compartilhadas do Banco Bootcamp
   ============================================ */

function formatarMoeda(valor) {
    return Number(valor).toLocaleString('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    });
}

function formatarData(dataIso) {
    return new Date(dataIso).toLocaleString('pt-BR');
}

async function carregarSaldo() {
    try {
        const resposta = await fetch('/api/conta/saldo');
        if (!resposta.ok) {
            window.location.href = '/login.html';
            return;
        }
        const dados = await resposta.json();

        const saldoEl = document.getElementById('saldo');
        if (saldoEl) saldoEl.textContent = formatarMoeda(dados.saldo);

        const nomeEl = document.getElementById('nome-cliente');
        if (nomeEl) nomeEl.textContent = dados.nome;

        const agenciaEl = document.getElementById('agencia');
        if (agenciaEl) agenciaEl.textContent = dados.agencia;

        const contaEl = document.getElementById('numero-conta');
        if (contaEl) contaEl.textContent = dados.numero;
    } catch (e) {
        console.error('Erro ao carregar saldo', e);
    }
}

function mostrarMensagem(texto, sucesso) {
    const el = document.getElementById('mensagem');
    el.className = 'alerta ' + (sucesso ? 'alerta-sucesso' : 'alerta-erro');
    el.textContent = texto;
    el.style.display = 'block';
}

/* ---------- Cadastro ---------- */

function configurarFormularioCadastro() {
    const form = document.getElementById('form-cadastro');
    form.addEventListener('submit', async (evento) => {
        evento.preventDefault();

        const senha = document.getElementById('senha').value;
        const confirma = document.getElementById('senha-confirma').value;
        if (senha !== confirma) {
            mostrarMensagem('As senhas não conferem. Digite novamente.', false);
            return;
        }

        const corpo = {
            nome: document.getElementById('nome').value,
            username: document.getElementById('username').value,
            senha: senha
        };

        const resposta = await fetch('/api/cadastro', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(corpo)
        });

        if (resposta.ok) {
            window.location.href = '/login.html?cadastrado=true';
        } else {
            const erro = await resposta.json();
            mostrarMensagem(erro.erro || 'Não foi possível criar a conta.', false);
        }
    });
}

/* ---------- PIX ---------- */

function configurarFormularioPix() {
    const form = document.getElementById('form-pix');
    form.addEventListener('submit', async (evento) => {
        evento.preventDefault();

        const corpo = {
            chaveDestino: document.getElementById('chave').value,
            valor: parseFloat(document.getElementById('valor').value),
            descricao: document.getElementById('descricao').value
        };

        const resposta = await fetch('/api/pix', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(corpo)
        });

        if (resposta.ok) {
            mostrarMensagem('PIX enviado com sucesso! 🎉', true);
            form.reset();
            carregarSaldo();
        } else {
            const erro = await resposta.json();
            mostrarMensagem(erro.erro || 'Não foi possível enviar o PIX.', false);
        }
    });
}

/* ---------- Extrato ---------- */

function rotuloTipo(tipo) {
    const rotulos = {
        'PIX_ENVIADO': 'PIX enviado',
        'PIX_RECEBIDO': 'PIX recebido',
        'DEPOSITO': 'Depósito'
    };
    return rotulos[tipo] || tipo;
}

function rotuloStatus(status) {
    return status === 'EM_ANALISE' ? 'Em análise' : 'Concluída';
}

async function carregarExtrato() {
    try {
        const resposta = await fetch('/api/transacoes');
        if (!resposta.ok) {
            window.location.href = '/login.html';
            return;
        }
        const transacoes = await resposta.json();
        const tbody = document.getElementById('lista-transacoes');
        tbody.innerHTML = '';

        if (transacoes.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6">Nenhuma transação encontrada.</td></tr>';
            return;
        }

        for (const t of transacoes) {
            const saida = t.tipo === 'PIX_ENVIADO';
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${formatarData(t.dataHora)}</td>
                <td>${t.descricao}</td>
                <td>${rotuloTipo(t.tipo)}</td>
                <td class="${saida ? 'valor-negativo' : 'valor-positivo'}">
                    ${saida ? '- ' : '+ '}${formatarMoeda(t.valor)}
                </td>
                <td><span class="badge ${t.status === 'CONCLUIDA' ? 'badge-concluida' : 'badge-analise'}">
                    ${rotuloStatus(t.status)}
                </span></td>
                <td></td>
            `;

            const tdAcao = tr.lastElementChild;
            if (t.status === 'CONCLUIDA') {
                const botao = document.createElement('button');
                botao.className = 'btn btn-contestar';
                botao.textContent = 'Contestar';
                botao.addEventListener('click', () => contestarTransacao(t.id));
                tdAcao.appendChild(botao);
            } else {
                tdAcao.textContent = t.motivoContestacao ? `Motivo: ${t.motivoContestacao}` : '—';
            }

            tbody.appendChild(tr);
        }
    } catch (e) {
        console.error('Erro ao carregar extrato', e);
    }
}

async function contestarTransacao(id) {
    const motivo = prompt('Descreva o motivo da contestação:');
    if (!motivo) return;

    const resposta = await fetch(`/api/transacoes/${id}/contestar`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ motivo })
    });

    if (resposta.ok) {
        mostrarMensagem('Transação contestada. Nossa equipe irá analisar. ✅', true);
        carregarExtrato();
    } else {
        const erro = await resposta.json();
        mostrarMensagem(erro.erro || 'Não foi possível contestar a transação.', false);
    }
}
