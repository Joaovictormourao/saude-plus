sobre.js

document.addEventListener("DOMContentLoaded", () => {

    console.log("Página Sobre carregada com sucesso!");

    // Seleciona todos os cards
    const cards = document.querySelectorAll(".card");

    // Efeito hover nos cards
    cards.forEach((card) => {

        card.addEventListener("mouseenter", () => {

            card.style.transform = "scale(1.05)";
            card.style.transition = "0.3s";
            card.style.boxShadow = "0 5px 15px rgba(0,0,0,0.2)";
            card.style.cursor = "pointer";

        });

        card.addEventListener("mouseleave", () => {

            card.style.transform = "scale(1)";
            card.style.boxShadow = "none";

        });

    });

    // Efeito nas informações de contato
    const infoItems = document.querySelectorAll(".info-item");

    infoItems.forEach((item) => {

        item.addEventListener("mouseenter", () => {

            item.style.backgroundColor = "#f0f8ff";
            item.style.transition = "0.3s";
            item.style.padding = "10px";
            item.style.borderRadius = "10px";

        });

        item.addEventListener("mouseleave", () => {

            item.style.backgroundColor = "transparent";

        });

    });

    // Saudação automática
    const hora = new Date().getHours();

    let mensagem = "";

    if (hora < 12) {

        mensagem = "Bom dia";

    } else if (hora < 18) {

        mensagem = "Boa tarde";

    } else {

        mensagem = "Boa noite";

    }

    console.log(`${mensagem}, seja bem-vindo à Clínica Saúde+!`);

});