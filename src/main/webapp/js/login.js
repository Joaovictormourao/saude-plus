// login.js — conectado à API backend

const API_URL = API_ENDPOINTS.PACIENTES;

document.addEventListener("DOMContentLoaded", () => {

    const formulario = document.getElementById("formLogin");

    formulario.addEventListener("submit", async (event) => {

        event.preventDefault();

        const email = document.getElementById("email").value.trim();
        const senha = document.getElementById("senha").value;

        if (!email || !senha) {
            window.alerts?.showWarning('Campos obrigatórios', 'Preencha todos os campos!');
            return;
        }

        try {

            const resposta = await fetch(API_URL + "/login", {
                method:  "POST",
                headers: { "Content-Type": "application/json" },
                body:    JSON.stringify({ email, senha })
            });

            const dados = await resposta.json();

            if (resposta.ok) {
                // Salva os dados do paciente no armazenamento local
                localStorage.setItem("pacienteLogado", JSON.stringify(dados));
                localStorage.setItem("paciente", JSON.stringify(dados));
                await window.alerts?.showSuccess('Login realizado', 'Bem-vindo!');
                window.location.href = "painel_paciente.html";
            } else {
                window.alerts?.showError('Falha no login', (dados.erro || "E-mail ou senha inválidos."));
            }

        } catch (erro) {
            console.error("Falha na requisição:", erro);
            window.alerts?.showError('Erro de conexão', 'Não foi possível contatar o servidor.');
        }

    });

});
