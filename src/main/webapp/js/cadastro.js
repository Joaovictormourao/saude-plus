// cadastro.js — conectado à API backend

const API_URL = API_ENDPOINTS.PACIENTES;

function apenasDigitos(str) {
    return String(str ?? '').replace(/\D+/g, '');
}

function validarCpf(cpf) {
    const num = apenasDigitos(cpf);

    // CPF deve ter 11 dígitos numéricos
    if (num.length !== 11) return false;

    // Rejeita CPFs com todos os dígitos iguais
    if (/^(\d)\1{10}$/.test(num)) return false;

    const calcDigito = (base, fatorInicial) => {
        let soma = 0;
        for (let i = 0; i < base.length; i++) {
            soma += parseInt(base[i], 10) * (fatorInicial - i);
        }
        const resto = (soma * 10) % 11;
        return resto === 10 ? 0 : resto;
    };

    const base9 = num.slice(0, 9);
    const base10 = num.slice(0, 10);

    const digito1 = calcDigito(base9, 10);
    const digito2 = calcDigito(base10, 11);

    return num.endsWith(String(digito1) + String(digito2));
}

function validarTelefoneBR(telefone) {
    // Aceita telefone/celular BR com 11 dígitos (DDD + 9 + 8 dígitos)
    // Ex: 11987654321 (SP) ou 61991234567 (DF)
    const num = apenasDigitos(telefone);
    if (num.length !== 11) return false;
    // Regra de celular: segundo bloco inicia com 9
    return /^(\d{2})9\d{8}$/.test(num);
}

function limitarTelefoneBR(input) {
    // Limita a entrada a 11 dígitos numéricos
    const digitos = apenasDigitos(input.value).slice(0, 11);
    input.value = digitos;
}

function limitarCpfInput(input) {
    // Limita a 11 dígitos numéricos e mantém cursor/valor simples
    const digitos = apenasDigitos(input.value).slice(0, 11);
    input.value = digitos;
}

document.addEventListener("DOMContentLoaded", () => {

    // Visualizador de senha (eye)
    document.querySelectorAll(".btn-toggle-password[data-target]").forEach((btn) => {
        btn.addEventListener("click", () => {
            const targetId = btn.getAttribute("data-target");
            const input = document.getElementById(targetId);
            if (!input) return;

            const isPassword = input.type === "password";
            input.type = isPassword ? "text" : "password";
        });
    });

    const form = document.querySelector("form");

    const cpfInput = document.getElementById("cpf");
    const telefoneInput = document.getElementById("telefone");

    if (cpfInput) {
        // Limita a entrada e remove caracteres não numéricos
        cpfInput.addEventListener("input", () => limitarCpfInput(cpfInput));
        // Corrige no blur também
        cpfInput.addEventListener("blur", () => limitarCpfInput(cpfInput));
    }

    if (telefoneInput) {
        // Limita a entrada a 11 dígitos numéricos
        telefoneInput.addEventListener("input", () => limitarTelefoneBR(telefoneInput));
        telefoneInput.addEventListener("blur", () => limitarTelefoneBR(telefoneInput));
    }

    form.addEventListener("submit", async (e) => {


        e.preventDefault();

        const cpfBruto = document.getElementById("cpf").value;
        const cpfLimpo = apenasDigitos(cpfBruto);

        const telefoneBruto = document.getElementById("telefone")?.value ?? "";
        const telefoneLimpo = apenasDigitos(telefoneBruto);

        // Validação no frontend antes de enviar
        if (!validarCpf(cpfLimpo)) {
            window.alerts?.showError('CPF inválido', 'Informe um CPF válido (11 dígitos e dígitos verificadores corretos).');
            return;
        }

        if (!validarTelefoneBR(telefoneLimpo)) {
            window.alerts?.showError(
                'Telefone inválido',
                'Informe um telefone/celular válido no padrão brasileiro (DDD + 9 + 8 dígitos).'
            );
            return;
        }

        const paciente = {

            nome:           document.getElementById("nome").value.trim(),
            cpf:            cpfLimpo,
            email:          document.getElementById("email").value.trim(),
            telefone:       telefoneLimpo,

            sexo:           document.getElementById("sexo")?.value ?? "",
            senha:          document.getElementById("senha").value,
            dataNascimento: document.getElementById("dataNascimento")?.value ?? ""
        };

        try {

            const resposta = await fetch(API_URL + "/cadastrar", {
                method:  "POST",
                headers: { "Content-Type": "application/json" },
                body:    JSON.stringify(paciente)
            });

            const dados = await resposta.json();

            if (resposta.ok) {
                await window.alerts?.showSuccess('Cadastro realizado', 'Faça o login para continuar.');
                window.location.href = "login.html";
            } else {
                window.alerts?.showError('Falha no cadastro', (dados.erro || "Não foi possível cadastrar."));
            }

        } catch (erro) {
            console.error("Falha na requisição:", erro);
            window.alerts?.showError('Erro de conexão', 'Não foi possível contatar o servidor.');
        }

    });

});

