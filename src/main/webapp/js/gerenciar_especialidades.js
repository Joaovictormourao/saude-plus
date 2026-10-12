document.addEventListener("DOMContentLoaded", async () => {

    const tabelaBody = document.querySelector("tbody");
    const botaoAdicionar = document.querySelector(".btn-success");

    if (!tabelaBody || !botaoAdicionar) {
        console.error("Estrutura da página inválida: tbody ou botão adicionar não encontrado.");
        return;
    }

    const alerts = window['al'+'erts'];

    // Monta o formulário (HTML) usado tanto para criar quanto para editar especialidade
    function montarFormularioHtml(especialidade) {
        const nome = especialidade?.nome ?? '';
        const descricao = especialidade?.descricao ?? '';

        return `
            <div style="text-align:left">
                <label for="swalNomeEsp" style="display:block;margin-bottom:4px;font-weight:600;">Nome da especialidade</label>
                <input id="swalNomeEsp" class="swal2-input" style="margin:0 0 12px;width:100%;" value="${nome}" placeholder="Ex: Cardiologia">

                <label for="swalDescricaoEsp" style="display:block;margin-bottom:4px;font-weight:600;">Descrição</label>
                <textarea id="swalDescricaoEsp" class="swal2-textarea" style="margin:0;width:100%;" placeholder="Descreva a especialidade...">${descricao}</textarea>
            </div>
        `;
    }

    function validarFormulario() {
        const nome = document.getElementById('swalNomeEsp')?.value.trim() ?? '';
        const descricao = document.getElementById('swalDescricaoEsp')?.value.trim() ?? '';

        if (!nome) {
            window.Swal.showValidationMessage('Informe o nome da especialidade.');
            return false;
        }

        return { nome, descricao };
    }

    function aplicarHoverLinhas() {
        const linhas = document.querySelectorAll("tbody tr");
        linhas.forEach((linha) => {
            linha.addEventListener("mouseenter", () => {
                linha.style.backgroundColor = "#f0f8ff";
                linha.style.transition = "0.3s";
            });
            linha.addEventListener("mouseleave", () => {
                linha.style.backgroundColor = "";
            });
        });
    }

    function renderizarEspecialidades(especialidades) {
        tabelaBody.innerHTML = "";

        if (!Array.isArray(especialidades) || especialidades.length === 0) {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td colspan="4" style="text-align:center; color:#666;">Nenhuma especialidade encontrada.</td>
            `;
            tabelaBody.appendChild(tr);
            return;
        }

        especialidades.forEach((e) => {
            const tr = document.createElement("tr");
            tr.dataset.id = e.id;
            tr.innerHTML = `
                <td>${e.id ?? ""}</td>
                <td>${e.nome ?? ""}</td>
                <td>${e.descricao ?? ""}</td>
                <td>
                    <button class="btn btn-warning btn-sm">Editar</button>
                    <button class="btn btn-danger btn-sm">Excluir</button>
                </td>
            `;
            tabelaBody.appendChild(tr);
        });

        aplicarHoverLinhas();
    }

    async function carregarEspecialidades() {
        try {
            const res = await fetch(`${API_ENDPOINTS.ESPECIALIDADES}`);
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            const data = await res.json();
            renderizarEspecialidades(data);
            return data;
        } catch (err) {
            console.error("Erro ao carregar especialidades:", err);
            alerts?.showError('Erro', 'Erro ao carregar especialidades.');
            return [];
        }
    }

    let especialidadesAtuais = await carregarEspecialidades();

    tabelaBody.addEventListener("click", async (ev) => {
        const alvo = ev.target;
        if (!(alvo instanceof HTMLElement)) return;

        const botao = alvo.closest("button");
        if (!botao) return;

        const tr = botao.closest("tr");
        if (!tr) return;

        const id = tr.dataset.id;
        if (!id) return;

        // EDITAR
        if (botao.classList.contains("btn-warning")) {
            const especialidade = especialidadesAtuais.find(e => String(e.id) === String(id));
            if (!especialidade) return;

            const valores = await alerts?.showForm({
                title: 'Editar especialidade',
                confirmButtonText: 'Salvar alterações',
                cancelButtonText: 'Cancelar',
                html: montarFormularioHtml(especialidade),
                didOpen: () => document.getElementById('swalNomeEsp')?.focus(),
                preConfirm: validarFormulario
            });

            if (!valores) return;

            try {
                const res = await fetch(API_ENDPOINTS.ESPECIALIDADES, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        id: especialidade.id,
                        nome: valores.nome,
                        descricao: valores.descricao,
                        icone: especialidade.icone || ""
                    })
                });

                if (!res.ok) {
                    const errBody = await res.json().catch(() => ({}));
                    throw new Error(errBody.erro || `HTTP ${res.status}`);
                }

                especialidadesAtuais = await carregarEspecialidades();
                alerts?.showSuccess('Sucesso', 'Especialidade atualizada com sucesso!');
            } catch (err) {
                console.error("Erro ao atualizar especialidade:", err);
                alerts?.showError('Erro', err.message || 'Erro ao atualizar especialidade.');
            }
        }

        // EXCLUIR
        if (botao.classList.contains("btn-danger")) {
            const nome = tr.children[1]?.innerText ?? "";

            try {
                const confirmar = await alerts?.showConfirm({
                    title: 'Confirmar exclusão',
                    text: `Deseja realmente excluir ${nome}?`,
                    confirmButtonText: 'Excluir',
                    cancelButtonText: 'Cancelar',
                    icon: 'warning'
                });

                if (confirmar !== true) return;

                const res = await fetch(`${API_ENDPOINTS.ESPECIALIDADES}?id=${encodeURIComponent(id)}`, {
                    method: "DELETE"
                });

                if (!res.ok) {
                    const errBody = await res.text().catch(() => "");
                    throw new Error(`HTTP ${res.status} ${errBody}`);
                }

                especialidadesAtuais = await carregarEspecialidades();
                alerts?.showSuccess('Sucesso', 'Especialidade removida com sucesso!');
            } catch (err) {
                console.error("Erro ao excluir especialidade:", err);
                alerts?.showError('Erro', 'Erro ao excluir especialidade.');
            }
        }

    });

    botaoAdicionar.addEventListener("click", async () => {
        const valores = await alerts?.showForm({
            title: 'Nova especialidade',
            confirmButtonText: 'Cadastrar',
            cancelButtonText: 'Cancelar',
            html: montarFormularioHtml(null),
            didOpen: () => document.getElementById('swalNomeEsp')?.focus(),
            preConfirm: validarFormulario
        });

        if (!valores) return;

        try {
            const res = await fetch(API_ENDPOINTS.ESPECIALIDADES, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    nome: valores.nome,
                    descricao: valores.descricao
                })
            });

            if (!res.ok) {
                const errBody = await res.json().catch(() => ({}));
                throw new Error(errBody.erro || `HTTP ${res.status}`);
            }

            especialidadesAtuais = await carregarEspecialidades();
            alerts?.showSuccess('Sucesso', 'Especialidade cadastrada com sucesso!');
        } catch (err) {
            console.error("Erro ao adicionar especialidade:", err);
            alerts?.showError('Erro', err.message || 'Erro ao cadastrar especialidade.');
        }
    });

});
