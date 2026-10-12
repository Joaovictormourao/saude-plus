index.js

document.addEventListener("DOMContentLoaded", () => {

    // Mensagem de boas-vindas
    console.log("Sistema Clínica Saúde+ carregado com sucesso!");

    // Efeito nos botões
    const botoes = document.querySelectorAll(".btn");

    botoes.forEach(botao => {

        botao.addEventListener("mouseenter", () => {
            botao.style.transform = "scale(1.05)";
            botao.style.transition = "0.3s";
        });

        botao.addEventListener("mouseleave", () => {
            botao.style.transform = "scale(1)";
        });

    });

    // Banner animado
    const banner = document.querySelector(".banner-image");

    if (banner) {
        banner.style.opacity = "0";

        setTimeout(() => {
            banner.style.transition = "1.5s";
            banner.style.opacity = "1";
        }, 300);
    }

    // Saudação automática
    const hora = new Date().getHours();
    let mensagem = "";

    if (hora < 12) {
        mensagem = "Bom dia!";
    } else if (hora < 18) {
        mensagem = "Boa tarde!";
    } else {
        mensagem = "Boa noite!";
    }

    window.alerts?.showSuccess('Boas-vindas', `${mensagem} Bem-vindo à Clínica Saúde+ 🏥`);


});