document.addEventListener("DOMContentLoaded", () => {

    // Seleciona todos os cards
    const cards = document.querySelectorAll(".specialty-card");

    // Animação dos cards
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

        // Clique no card
        card.addEventListener("click", () => {

            const especialidadeEl = card.querySelector(".specialty-name");
            const especialidade = especialidadeEl ? especialidadeEl.innerText : "";

            // Usa SweetAlert2 sempre que disponível
            // Sem fallback nativo: usa SweetAlert2.
            window.alerts?.showSuccess('Você selecionou:', especialidade);


        });

    });

    // Botão Agendar
    const botaoAgendar = document.querySelector(".btn-primary");

    if (botaoAgendar) {
        botaoAgendar.addEventListener("mouseenter", () => {
            botaoAgendar.style.transform = "scale(1.05)";
            botaoAgendar.style.transition = "0.3s";
        });

        botaoAgendar.addEventListener("mouseleave", () => {
            botaoAgendar.style.transform = "scale(1)";
        });
    }

    // Mensagem no console
    console.log("Página de especialidades carregada com sucesso!");

});

