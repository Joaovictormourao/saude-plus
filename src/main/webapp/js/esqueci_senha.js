// esqueci_senha.js — redefinição de senha conectada à API backend

document.addEventListener("DOMContentLoaded", () => {

    const formulario = document.getElementById("formEsqueciSenha");
    if (!formulario) return;

    // Campos
    const email = document.getElementById("email");
    const novaSenha = document.getElementById("nova_senha");
    const confirmarSenha = document.getElementById("confirmar_senha");

    // Mostrar força da senha
    novaSenha.addEventListener("input", () => {
        const valor = novaSenha.value;

        if (valor.length < 6) {
            novaSenha.style.borderColor = "red";
        } else if (valor.length < 10) {
            novaSenha.style.borderColor = "orange";
        } else {
            novaSenha.style.borderColor = "green";
        }
    });

    formulario.addEventListener("submit", async (event) => {

        event.preventDefault();

        const emailValor = email.value.trim();
        const senhaValor = novaSenha.value;
        const confirmarValor = confirmarSenha.value;

        // Regex Email
        const emailValido = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        // Validação Email
        if (!emailValido.test(emailValor)) {
            window.alerts?.showWarning('Validação', 'Digite um e-mail válido!');
            return;
        }

        // Validação Senha
        if (senhaValor.length < 6) {
            window.alerts?.showWarning('Validação', 'A senha deve ter no mínimo 6 caracteres!');
            return;
        }

        // Senha forte
        const senhaForte = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+$/;

        if (!senhaForte.test(senhaValor)) {
            window.alerts?.showWarning('Validação', 'A senha deve conter letra maiúscula, minúscula e número!');
            return;
        }

        // Confirmação de senha
        if (senhaValor !== confirmarValor) {
            window.alerts?.showWarning('Validação', 'As senhas não coincidem!');
            return;
        }

        const botao = document.getElementById("btnRedefinirSenha");
        const textoOriginal = botao ? botao.textContent : null;

        try {
            if (botao) {
                botao.disabled = true;
                botao.textContent = "Enviando...";
            }

            const resposta = await fetch(`${API_ENDPOINTS.PACIENTES}/redefinir-senha`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email: emailValor, novaSenha: senhaValor })
            });

            const dados = await resposta.json();

            if (resposta.ok) {
                await window.alerts?.showSuccess('Senha redefinida', 'Sua senha foi alterada com sucesso! Faça login para continuar.');
                window.location.href = "login.html";
            } else {
                window.alerts?.showError('Falha ao redefinir senha', (dados.erro || "Não foi possível redefinir a senha."));
            }

        } catch (erro) {
            console.error("Falha na requisição:", erro);
            window.alerts?.showError('Erro de conexão', 'Não foi possível contatar o servidor.');
        } finally {
            if (botao) {
                botao.disabled = false;
                botao.textContent = textoOriginal;
            }
        }

    });

});
