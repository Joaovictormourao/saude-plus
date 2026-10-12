// agendamento.js

document.addEventListener("DOMContentLoaded", () => {

    const selectEspecialidade = document.getElementById("especialidade");
    const selectMedico = document.createElement("select");
    selectMedico.id = "medico";
    selectMedico.classList.add("form-control");
    selectMedico.style.marginTop = "10px";
    selectMedico.innerHTML = '<option value="">Selecione o Médico</option>';
    selectEspecialidade.parentNode.appendChild(selectMedico);

    // Preenche especialidades vindas do backend (id, nome)
    async function carregarEspecialidades() {
        try {
            console.log(`Carregando especialidades de: ${API_ENDPOINTS.ESPECIALIDADES}`);
            const res = await fetch(API_ENDPOINTS.ESPECIALIDADES);
            
            if (!res.ok) {
                console.error(`Erro ao carregar especialidades: ${res.status} ${res.statusText}`);
                const texto = await res.text();
                console.error('Resposta:', texto);
                throw new Error(`Falha ao carregar especialidades (${res.status})`);
            }
            
            const especialidades = await res.json();
            console.log('Especialidades carregadas:', especialidades);

            selectEspecialidade.innerHTML = '<option value="">Selecione a Especialidade</option>';
            if (Array.isArray(especialidades)) {
                especialidades.forEach(e => {
                    selectEspecialidade.innerHTML += `<option value="${e.id}">${e.nome}</option>`;
                });
            } else {
                console.error('Resposta não é um array:', especialidades);
            }
        } catch (err) {
            console.error('Erro ao carregar especialidades:', err);
            selectEspecialidade.innerHTML = '<option value="">Erro ao carregar especialidades</option>';
                window.alerts?.showError('Erro ao carregar especialidades', 'Verifique o console.');
        }
    }

    // Ao selecionar especialidade, carrega médicos filtrados (id, nome)
    selectEspecialidade.addEventListener("change", async () => {
        const especialidadeId = selectEspecialidade.value;
        if (!especialidadeId) return;

        try {
            const url = `${API_ENDPOINTS.MEDICOS}?especialidade=${encodeURIComponent(especialidadeId)}`;
            console.log(`Carregando médicos de: ${url}`);
            const res = await fetch(url);
            
            if (!res.ok) {
                throw new Error(`Falha ao carregar médicos (${res.status})`);
            }

            const medicos = await res.json();
            console.log('Médicos carregados:', medicos);
            selectMedico.innerHTML = '<option value="">Selecione o Médico</option>';

            if (Array.isArray(medicos)) {
                medicos.forEach(m => {
                    selectMedico.innerHTML += `<option value="${m.id}">${m.nome}</option>`;
                });
            }
        } catch (err) {
            console.error('Erro ao carregar médicos:', err);
            selectMedico.innerHTML = '<option value="">Erro ao carregar médicos</option>';
        }
    });

    // carrega especialidades ao iniciar
    carregarEspecialidades().catch(console.error);



    const diasContainer =
        document.getElementById("dias");

    const mesAno =
        document.getElementById("mesAno");

    const dataSelecionadaInput =
        document.getElementById("dataSelecionada");

    const horarioSelecionadoInput =
        document.getElementById("horarioSelecionado");

    let dataAtual = new Date();

    // MESES
    const meses = [

        "Janeiro",
        "Fevereiro",
        "Março",
        "Abril",
        "Maio",
        "Junho",
        "Julho",
        "Agosto",
        "Setembro",
        "Outubro",
        "Novembro",
        "Dezembro"

    ];

    // GERAR CALENDÁRIO
    function gerarCalendario(){

        diasContainer.innerHTML = "";

        const ano =
            dataAtual.getFullYear();

        const mes =
            dataAtual.getMonth();

        mesAno.innerText =
            `${meses[mes]} ${ano}`;

        // PRIMEIRO DIA DO MÊS
        let primeiroDia =
            new Date(ano, mes, 1).getDay();

        // ÚLTIMO DIA DO MÊS
        const ultimoDia =
            new Date(ano, mes + 1, 0).getDate();

        // ESPAÇOS VAZIOS
        for(let i = 0; i < primeiroDia; i++){

            const vazio =
                document.createElement("div");

            vazio.classList.add("vazio");

            diasContainer.appendChild(vazio);

        }

        // DIAS
        for(let dia = 1; dia <= ultimoDia; dia++){

            const diaElemento =
                document.createElement("div");

            diaElemento.classList.add("dia");

            diaElemento.innerText = dia;

            // CLIQUE NO DIA
            diaElemento.addEventListener("click", () => {

                document
                    .querySelectorAll(".dia")
                    .forEach((d) => {

                        d.classList.remove("ativo");

                    });

                diaElemento.classList.add("ativo");

                const dataFormatada =
                    `${String(dia).padStart(2, "0")}/${
                        String(mes + 1).padStart(2, "0")
                    }/${ano}`;

                dataSelecionadaInput.value =
                    dataFormatada;

            });

            diasContainer.appendChild(diaElemento);

        }

    }

    gerarCalendario();

    // MÊS ANTERIOR
    document
        .getElementById("prevMes")
        .addEventListener("click", () => {

            dataAtual.setMonth(
                dataAtual.getMonth() - 1
            );

            gerarCalendario();

        });

    // PRÓXIMO MÊS
    document
        .getElementById("nextMes")
        .addEventListener("click", () => {

            dataAtual.setMonth(
                dataAtual.getMonth() + 1
            );

            gerarCalendario();

        });

    // HORÁRIOS
    const horarios =
        document.querySelectorAll(".horario");

    horarios.forEach((horario) => {

        horario.addEventListener("click", () => {

            horarios.forEach((h) => {

                h.classList.remove("ativo");

            });

            horario.classList.add("ativo");

            horarioSelecionadoInput.value =
                horario.innerText;

        });

    });

    // BOTÃO AGENDAR
    document
        .getElementById("btnAgendar")
        .addEventListener("click", async () => {

            const especialidade = document.getElementById("especialidade").value;
            const dataInput = dataSelecionadaInput.value; // dd/mm/yyyy
            const horario = horarioSelecionadoInput.value; // hh:mm

            // VALIDAÇÃO
            if (especialidade === "" || dataInput === "" || horario === "") {
                window.alerts?.showWarning('Campos obrigatórios', 'Preencha todos os campos!');
                return;
            }

            // Pegar paciente logado
            const paciente = JSON.parse(localStorage.getItem("pacienteLogado"));
            if (!paciente) {
                window.alerts?.showWarning('Acesso necessário', 'Você precisa estar logado para agendar.');
                window.location.href = "login.html";
                return;
            }

            // Formatar data para yyyy-mm-dd (esperado pelo backend)
            const partesData = dataInput.split("/");
            const dataISO = `${partesData[2]}-${partesData[1]}-${partesData[0]}`;

            const medico = document.getElementById("medico").value;
            if (!medico) {
                window.alerts?.showWarning('Seleção obrigatória', 'Selecione um médico!');
                return;
            }

            const agendamento = {
                pacienteId: paciente.id,
                medicoId: parseInt(medico, 10),
                especialidadeId: parseInt(especialidade, 10),
                data: dataISO,
                hora: horario,
                descricao: "Agendamento via portal"
            };

            try {
                const resposta = await fetch(API_ENDPOINTS.AGENDAMENTOS, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(agendamento)
                });

                if (resposta.ok) {
                    await window.alerts?.showSuccess('Agendamento realizado', 'Sua consulta foi agendada com sucesso!');
                    window.location.href = "painel_paciente.html";
                } else {
                    const erro = await resposta.json();
                    window.alerts?.showError('Falha no agendamento', (erro.erro || "Falha ao agendar."));
                }
            } catch (err) {
                console.error(err);
                window.alerts?.showError('Erro de conexão', 'Não foi possível contatar o servidor.');
            }
        });

});