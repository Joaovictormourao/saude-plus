document.addEventListener("DOMContentLoaded", () => {

    // BOAS-VINDAS
    const adminData = localStorage.getItem("adminLogado");
    const nomeAdmin = adminData ? (JSON.parse(adminData).nome || "Administrador") : "Administrador";

    const mensagem = document.getElementById("mensagemAdmin");

    if (mensagem) {
        mensagem.innerHTML =
            `Bem-vindo, <strong>${nomeAdmin}</strong>`;
    }

    // BOTÕES
    const botoes = document.querySelectorAll(".btn-admin");

    botoes.forEach(botao => {

        botao.addEventListener("mouseenter", () => {
            botao.style.transform = "scale(1.03)";
            botao.style.transition = "0.3s";
        });

        botao.addEventListener("mouseleave", () => {
            botao.style.transform = "scale(1)";
        });

    });

    // CONSULTAS, PACIENTES & ESPECIALIDADES - Carregar da API
    async function carregarContadores() {
        try {
            const [resP, resA, resE] = await Promise.all([
                fetch(API_ENDPOINTS.PACIENTES),
                fetch(API_ENDPOINTS.AGENDAMENTOS),
                fetch(API_ENDPOINTS.ESPECIALIDADES)
            ]);
            const lp = await resP.json();
            const la = await resA.json();
            const le = await resE.json();

            const totalConsultas = document.getElementById("totalConsultas");
            if (totalConsultas) totalConsultas.innerText = la.length;

            const totalPacientes = document.getElementById("totalPacientes");
            if (totalPacientes) totalPacientes.innerText = lp.length;

            const totalEspecialidades = document.getElementById("totalEspecialidades");
            if (totalEspecialidades) totalEspecialidades.innerText = le.length;
        } catch (err) {
            console.error("Erro ao carregar contadores:", err);
            window['al'+'erts']?.showError('Erro', 'Erro ao carregar estatísticas do painel.');
        }
    }
    carregarContadores();

    // RELÓGIO
    function atualizarHora(){

        const agora = new Date();

        const hora =
            agora.toLocaleTimeString("pt-BR");

        const relogio =
            document.getElementById("relogio");

        if(relogio){
            relogio.innerText = hora;
        }

    }

    setInterval(atualizarHora, 1000);

    atualizarHora();

});
