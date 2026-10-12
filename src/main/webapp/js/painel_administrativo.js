// painel_administrativo.js

const API_PACIENTES = API_ENDPOINTS.PACIENTES;
const API_AGENDAMENTOS = API_ENDPOINTS.AGENDAMENTOS;

let listaConsultasAtual = [];

document.addEventListener("DOMContentLoaded", () => {

    const tabelaPacientes = document.getElementById("tabelaPacientes");
    const tabelaConsultas = document.getElementById("tabelaConsultas");

    async function carregarPacientes() {

        if (!tabelaPacientes) return;

        try {

            const resposta = await fetch(API_PACIENTES);
            const pacientes = await resposta.json();

            tabelaPacientes.innerHTML = "";

            if (!pacientes || pacientes.length === 0) {
                tabelaPacientes.innerHTML = `<tr><td colspan="3">Nenhum paciente cadastrado.</td></tr>`;
                window.listaPacientes = [];
                return;
            }

            pacientes.forEach((p) => {
                tabelaPacientes.innerHTML += `
                    <tr>
                        <td>${p.nome}</td>
                        <td>${p.email}</td>
                        <td>
                            <button class="btn-warning" onclick="editarPaciente(${p.id})">Editar</button>
                            <button class="btn-danger" onclick="excluirPaciente(${p.id})">Excluir</button>
                        </td>
                    </tr>
                `;
            });

            window.listaPacientes = pacientes;

        } catch (erro) {

            console.error(erro);
            window['al'+'erts']?.showError('Erro', 'Erro ao carregar pacientes.');

        }

    }

    async function carregarConsultas() {

        if (!tabelaConsultas) return;

        try {

            const resposta = await fetch(API_AGENDAMENTOS);
            const consultas = await resposta.json();

            listaConsultasAtual = Array.isArray(consultas) ? consultas : [];

            tabelaConsultas.innerHTML = "";

            if (!consultas || consultas.length === 0) {
                tabelaConsultas.innerHTML = `<tr><td colspan="5">Nenhuma consulta agendada.</td></tr>`;
                atualizarRelatorio();
                return;
            }

            consultas.forEach((c) => {
                tabelaConsultas.innerHTML += `
                    <tr>
                        <td>${c.paciente ?? c.pacienteId}</td>
                        <td>${c.especialidade ?? ''}</td>
                        <td>${c.data}</td>
                        <td>${c.hora}</td>
                        <td>
                            ${c.status !== 'Cancelado' ? `<button class="btn-danger" onclick="cancelarConsulta(${c.id})">Cancelar</button>` : 'Cancelado'}
                        </td>
                    </tr>
                `;
            });

            atualizarRelatorio();

        } catch (erro) {
            console.error(erro);
            window['al'+'erts']?.showError('Erro', 'Erro ao carregar consultas.');
        }

    }

    // Atualiza o bloco de Relatórios Gerais com estatísticas reais
    function atualizarRelatorio() {
        const container = document.getElementById("dadosRelatorio");
        if (!container) return;

        const total = listaConsultasAtual.length;
        const porStatus = listaConsultasAtual.reduce((acc, c) => {
            const status = c.status || "Indefinido";
            acc[status] = (acc[status] || 0) + 1;
            return acc;
        }, {});

        if (total === 0) {
            container.innerHTML = `<p>Nenhum relatório disponível.</p>`;
            return;
        }

        const linhasStatus = Object.entries(porStatus)
            .map(([status, qtd]) => `<li><strong>${status}:</strong> ${qtd}</li>`)
            .join("");

        container.innerHTML = `
            <p><strong>Total de consultas:</strong> ${total}</p>
            <ul style="margin-top:10px; list-style:disc; padding-left:20px;">
                ${linhasStatus}
            </ul>
        `;
    }

    // BOTÕES DOS CARDS: rolam até a seção correspondente
    function configurarBotaoNavegacao(idBotao, seletorDestino) {
        const botao = document.getElementById(idBotao);
        const destino = document.querySelector(seletorDestino);
        if (!botao || !destino) return;

        botao.addEventListener("click", () => {
            destino.scrollIntoView({ behavior: "smooth", block: "start" });
        });
    }

    configurarBotaoNavegacao("btnPacientes", ".tabela-container:has(#tabelaPacientes)");
    configurarBotaoNavegacao("btnConsultas", ".tabela-container:has(#tabelaConsultas)");
    configurarBotaoNavegacao("btnRelatorios", ".relatorio-box");

    carregarPacientes();
    carregarConsultas();

    // Disponibiliza para recarregar após ações (editar/excluir/cancelar)
    window.recarregarPainelAdmin = () => {
        carregarPacientes();
        carregarConsultas();
    };

});

async function excluirPaciente(id) {

    const alerts = window['al'+'erts'];
    const confirmar = await alerts?.showConfirm({
        title: 'Confirmação',
        text: 'Deseja excluir este paciente?',
        confirmButtonText: 'Excluir',
        cancelButtonText: 'Cancelar',
        icon: 'warning'
    });

    if (!confirmar) return;

    try {
        const resposta = await fetch(API_PACIENTES + "?id=" + id, { method: "DELETE" });

        if (resposta.ok) {
            await alerts?.showSuccess('Sucesso', 'Paciente excluído!');
            window.recarregarPainelAdmin ? window.recarregarPainelAdmin() : location.reload();
        } else {
            alerts?.showError('Erro', 'Erro ao excluir paciente.');
        }
    } catch (erro) {
        console.error(erro);
        alerts?.showError('Erro de conexão', 'Não foi possível excluir o paciente.');
    }

}

async function editarPaciente(id) {

    const alerts = window['al'+'erts'];
    const paciente = (window.listaPacientes || []).find(p => p.id === id);
    if (!paciente) return;

    const valores = await alerts?.showForm({
        title: `Editar paciente: ${paciente.nome}`,
        confirmButtonText: 'Salvar alterações',
        cancelButtonText: 'Cancelar',
        html: `
            <div style="text-align:left">
                <label for="swalNome" style="display:block;margin-bottom:4px;font-weight:600;">Nome</label>
                <input id="swalNome" class="swal2-input" style="margin:0 0 12px;width:100%;" value="${paciente.nome ?? ''}">

                <label for="swalTelefone" style="display:block;margin-bottom:4px;font-weight:600;">Telefone</label>
                <input id="swalTelefone" class="swal2-input" style="margin:0;width:100%;" value="${paciente.telefone ?? ''}">
            </div>
        `,
        didOpen: () => {
            document.getElementById('swalNome')?.focus();
        },
        preConfirm: () => {
            const nome = document.getElementById('swalNome')?.value.trim() ?? '';
            const telefone = document.getElementById('swalTelefone')?.value.trim() ?? '';

            if (!nome || !telefone) {
                window.Swal.showValidationMessage('Informe nome e telefone.');
                return false;
            }

            return { nome, telefone };
        }
    });

    if (!valores) return;

    try {
        const resposta = await fetch(API_PACIENTES, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                id: paciente.id,
                nome: valores.nome,
                telefone: valores.telefone,
                sexo: paciente.sexo || "",
                dataNascimento: paciente.dataNascimento || null
            })
        });

        if (resposta.ok) {
            await alerts?.showSuccess('Sucesso', 'Paciente atualizado com sucesso!');
            window.recarregarPainelAdmin ? window.recarregarPainelAdmin() : location.reload();
        } else {
            alerts?.showError('Erro', 'Erro ao atualizar paciente.');
        }
    } catch (erro) {
        console.error(erro);
        alerts?.showError('Erro de conexão', 'Erro ao atualizar paciente.');
    }

}

async function cancelarConsulta(id) {

    const alerts = window['al'+'erts'];
    const confirmar = await alerts?.showConfirm({
        title: 'Confirmação',
        text: 'Cancelar consulta?',
        confirmButtonText: 'Cancelar',
        cancelButtonText: 'Não cancelar',
        icon: 'warning'
    });

    if (!confirmar) return;

    try {
        const resposta = await fetch(API_AGENDAMENTOS + "?id=" + id, { method: "DELETE" });

        if (resposta.ok) {
            await alerts?.showSuccess('Sucesso', 'Consulta cancelada!');
            window.recarregarPainelAdmin ? window.recarregarPainelAdmin() : location.reload();
        } else {
            alerts?.showError('Erro', 'Erro ao cancelar consulta.');
        }
    } catch (erro) {
        console.error(erro);
        alerts?.showError('Erro de conexão', 'Não foi possível cancelar a consulta.');
    }

}
