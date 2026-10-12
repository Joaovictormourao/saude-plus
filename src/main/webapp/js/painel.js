document.addEventListener("DOMContentLoaded", () => {

    const listaConsultas = document.getElementById("listaConsultas");
    const relatorios = document.getElementById("relatorios");
    const btnMostrar = document.getElementById("btnMostrarConsultas");
    const btnRelatorios = document.getElementById("btnRelatorios");

    let consultas = [];

    // MOSTRAR CONSULTAS
    async function mostrarConsultas() {
        try {
            const paciente = JSON.parse(localStorage.getItem("pacienteLogado"));
            if (!paciente) {
                window['al'+'erts']?.showWarning('Sessão expirada', 'Faça login novamente.');
                window.location.href = "login.html";
                return;
            }

            const res = await fetch(API_ENDPOINTS.AGENDAMENTOS);

            if (!res.ok) {
                throw new Error(`Erro HTTP ${res.status}`);
            }

            const todasConsultas = await res.json();

            // Filtrar apenas as consultas do paciente logado
            consultas = todasConsultas.filter(c => c.pacienteId === paciente.id);

            listaConsultas.innerHTML = "";
            if (consultas.length === 0) {
                listaConsultas.innerHTML = `<p>Nenhuma consulta agendada.</p>`;
                return;
            }

            consultas.forEach((consulta) => {
                listaConsultas.innerHTML += `
                    <div class="consulta-item">
                        <h3>${consulta.especialidade || 'Especialidade Pendente'}</h3>
                        <br>
                        <p><strong>Médico:</strong> ${consulta.medico || 'Pendente'}</p>
                        <p><strong>Data:</strong> ${consulta.data}</p>
                        <p><strong>Horário:</strong> ${consulta.hora}</p>
                        <p><strong>Status:</strong> ${consulta.status}</p>
                        <br>
                        ${consulta.status !== 'Cancelado' ? `
                            <button class="btn-danger" onclick="cancelarConsulta(${consulta.id})">
                                Cancelar Consulta
                            </button>
                        ` : ''}
                    </div>
                `;
            });
        } catch (err) {
            console.error('Erro ao carregar consultas:', err);
            listaConsultas.innerHTML = `<p style="color: red;">Erro ao carregar consultas. Verifique a conexão com o servidor.</p>`;
        }
    }

    // RELATÓRIOS
    async function mostrarRelatorios() {
        await mostrarConsultas();
        relatorios.innerHTML = `
            <div class="consulta-item">
                <h3>📊 Resumo Geral</h3>
                <br>
                <p>Total de consultas: <strong>${consultas.length}</strong></p>
            </div>
        `;
    }

    btnMostrar?.addEventListener("click", mostrarConsultas);
    btnRelatorios?.addEventListener("click", mostrarRelatorios);

    mostrarConsultas();
});

// CANCELAR CONSULTA
async function cancelarConsulta(id) {
    const alerts = window['al'+'erts'];
    const confirmar = await alerts?.showConfirm({
        title: 'Confirmação',
        text: 'Deseja realmente cancelar esta consulta?',
        confirmButtonText: 'Cancelar consulta',
        cancelButtonText: 'Não cancelar',
        icon: 'warning'
    });

    if (!confirmar) return;

    try {
        const res = await fetch(`${API_ENDPOINTS.AGENDAMENTOS}?id=${id}`, { method: "DELETE" });
        if (res.ok) {
            await alerts?.showSuccess('Sucesso', 'Consulta cancelada!');
            location.reload();
        } else {
            alerts?.showError('Erro', 'Erro ao cancelar consulta.');
        }
    } catch (err) {
        alerts?.showError('Erro de conexão', 'Não foi possível contatar o servidor.');
    }
}

// LOGOUT
function logout() {
    const alerts = window['al'+'erts'];
    alerts?.showConfirm({
        title: 'Confirmação',
        text: 'Tem certeza que deseja sair?',
        confirmButtonText: 'Sair',
        cancelButtonText: 'Cancelar',
        icon: 'warning'
    }).then((confirmar) => {
        if (!confirmar) return;
        localStorage.removeItem('pacienteLogado');
        window.location.href = 'index.html';
    });
}

