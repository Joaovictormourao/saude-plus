// login_admin.js — conectado à API backend

const API_URL = API_ENDPOINTS.ADMINISTRADORES;

document.addEventListener("DOMContentLoaded", () => {

    const formulario = document.getElementById("formLoginAdmin");
    const campoSenha = document.getElementById("senha");



    // ── Submit ────────────────────────────────────────────────────────────────
    formulario.addEventListener("submit", async (event) => {

        event.preventDefault();

        const email = document.getElementById("email").value.trim();
        const senha = campoSenha.value;

        const emailValido = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!emailValido.test(email)) {
            window.alerts?.showWarning('E-mail inválido', 'Digite um e-mail válido!');
            return;
        }

        if (senha.length < 6) {
            window.alerts?.showWarning('Senha inválida', 'A senha deve conter no mínimo 6 caracteres!');
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
                // Salva admin no localStorage para persistência entre abas
                localStorage.setItem("adminLogado", JSON.stringify(dados));
                localStorage.setItem("admin", JSON.stringify(dados));
                await window.alerts?.showSuccess('Login administrativo realizado', 'Bem-vindo(a)!');
                window.location.href = "administrativo.html";
            } else {
                window.alerts?.showError('Falha no login', (dados.erro || "E-mail ou senha incorretos."));
            }

        } catch (erro) {
            console.error("Falha na requisição:", erro);
            window.alerts?.showError('Erro de conexão', 'Não foi possível contatar o servidor.');
        }

    });

});
