// gerenciamento_Paciente.js

const API_PACIENTES = API_ENDPOINTS.PACIENTES;

document.addEventListener("DOMContentLoaded", () => {

    const tabela = document.getElementById("tabelaPacientes");

    let pacientes = [];

    async function carregarPacientes() {

        try {

            const resposta = await fetch(API_PACIENTES);

            pacientes = await resposta.json();

            tabela.innerHTML = "";

            if (!pacientes || pacientes.length === 0) {

                tabela.innerHTML =
                    `<tr>
                        <td colspan="6">
                            Nenhum paciente cadastrado.
                        </td>
                    </tr>`;

                return;
            }

            pacientes.forEach((p) => {

                tabela.innerHTML += `
                    <tr>
                        <td>${p.id}</td>
                        <td>${p.nome}</td>
                        <td>${p.cpf}</td>
                        <td>${p.email}</td>
                        <td>${p.telefone}</td>

                        <td>

                            <button
                                class="btn-editar"
                                onclick="editarPaciente(${p.id})">

                                Editar

                            </button>

                            <button
                                class="btn-excluir"
                                onclick="excluirPaciente(${p.id})">

                                Excluir

                            </button>

                        </td>
                    </tr>
                `;

            });

        } catch (erro) {

            console.error(erro);

            window['al'+'erts']?.showError('Erro', 'Erro ao carregar pacientes.');

        }

    }

    carregarPacientes();

    window.editarPaciente = async function(id) {

        const paciente =
            pacientes.find(p => p.id === id);

        if (!paciente) return;

        const alerts = window['al'+'erts'];

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

                headers: {

                    "Content-Type": "application/json"

                },

                body: JSON.stringify({

                    id: paciente.id,
                    nome: valores.nome,
                    telefone: valores.telefone,
                    sexo: paciente.sexo || "",
                    dataNascimento:
                        paciente.dataNascimento || null

                })

            });

            if (resposta.ok) {

                alerts?.showSuccess('Sucesso', 'Paciente atualizado com sucesso!');

                carregarPacientes();

            } else {

                alerts?.showError('Erro', 'Erro ao atualizar paciente.');

            }

        } catch (erro) {

            console.error(erro);

            alerts?.showError('Erro de conexão', 'Erro ao atualizar paciente.');

        }

    };

    window.excluirPaciente = async function(id) {

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

            const resposta = await fetch(

                API_PACIENTES + "?id=" + id,

                {

                    method: "DELETE"

                }

            );

            if (resposta.ok) {

                alerts?.showSuccess('Sucesso', 'Paciente excluído com sucesso!');

                carregarPacientes();

            } else {

                alerts?.showError('Erro', 'Erro ao excluir paciente.');

            }

        } catch (erro) {

            console.error(erro);

            alerts?.showError('Erro de conexão', 'Não foi possível excluir o paciente.');

        }

    };

});

