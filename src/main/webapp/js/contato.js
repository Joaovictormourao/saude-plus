// contato.js — envia a mensagem de contato para o backend (com SweetAlert2)

document.addEventListener("DOMContentLoaded", () => {

    const formulario = document.getElementById("formContato");
    if (!formulario) return;

    const telefoneInput = document.getElementById("telefone");

    // Máscara automática telefone
    if (telefoneInput) {
        telefoneInput.addEventListener("input", (e) => {
            let valor = e.target.value.replace(/\D/g, "");

            if (valor.length > 11) {
                valor = valor.slice(0, 11);
            }

            if (valor.length > 10) {
                valor = valor.replace(/^(\d{2})(\d{5})(\d{4}).*/, "($1) $2-$3");
            } else {
                valor = valor.replace(/^(\d{2})(\d{4})(\d{4}).*/, "($1) $2-$3");
            }

            e.target.value = valor;
        });
    }

    formulario.addEventListener("submit", async (event) => {

        event.preventDefault();

        // Captura dos campos
        const nome = document.getElementById("nome").value.trim();
        const email = document.getElementById("email").value.trim();
        const telefone = document.getElementById("telefone").value.trim();
        const assunto = document.getElementById("assunto").value.trim();
        const mensagem = document.getElementById("mensagem").value.trim();

        // Regex Email
        const emailValido = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        // Regex Telefone
        const telefoneValido = /^\(\d{2}\)\s?\d{4,5}-\d{4}$/;

        // Validação Nome
        if (nome.length < 3) {
            window.alerts?.showWarning('Validação', 'Digite um nome válido!');
            return;
        }

        // Validação Email
        if (!emailValido.test(email)) {
            window.alerts?.showWarning('Validação', 'Digite um e-mail válido!');
            return;
        }

        // Validação Telefone (opcional, mas se preenchido precisa ser válido)
        if (telefone !== "" && !telefoneValido.test(telefone)) {
            window.alerts?.showWarning('Validação', 'Telefone inválido! Exemplo: (61) 99999-9999');
            return;
        }

        // Validação Assunto
        if (assunto.length < 5) {
            window.alerts?.showWarning('Validação', 'O assunto deve ter pelo menos 5 caracteres!');
            return;
        }

        // Validação Mensagem
        if (mensagem.length < 10) {
            window.alerts?.showWarning('Validação', 'A mensagem deve ter pelo menos 10 caracteres!');
            return;
        }

        const botaoEnviar = document.getElementById("btnEnviarContato");
        const textoOriginal = botaoEnviar ? botaoEnviar.textContent : null;

        try {
            if (botaoEnviar) {
                botaoEnviar.disabled = true;
                botaoEnviar.textContent = "Enviando...";
            }

            const resposta = await fetch(`${API_ENDPOINTS.MENSAGENS}/contato`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ nome, email, telefone, assunto, mensagem })
            });

            const dados = await resposta.json();

            if (resposta.ok) {
                await window.alerts?.showSuccess('Mensagem enviada!', 'Em breve entraremos em contato com você.');
                formulario.reset();
            } else {
                window.alerts?.showError('Falha ao enviar', (dados.erro || "Não foi possível enviar sua mensagem."));
            }

        } catch (erro) {
            console.error("Falha na requisição:", erro);
            window.alerts?.showError('Erro de conexão', 'Não foi possível contatar o servidor.');
        } finally {
            if (botaoEnviar) {
                botaoEnviar.disabled = false;
                botaoEnviar.textContent = textoOriginal;
            }
        }

    });

});
