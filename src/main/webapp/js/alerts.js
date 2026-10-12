// alerts.js - SweetAlert2 helpers
// Requer: SweetAlert2 carregado via CDN antes deste arquivo.

(function () {
  function normalizeText(text) {
    if (text === undefined || text === null) return '';
    return String(text);
  }

  function mixOptions(base, overrides) {
    return Object.assign({}, base || {}, overrides || {});
  }

  const DEFAULT_FIRE = {
    allowOutsideClick: false,
    backdrop: true,
  };

  async function showConfirm(
    {
      title = 'Confirmação',
      text,
      confirmButtonText = 'Confirmar',
      cancelButtonText = 'Cancelar',
      icon = 'warning',
    } = {}
  ) {
    const swal = window.Swal;
    if (!swal) {
      // SweetAlert2 não carregado: NÃO usar confirm nativo (exigência do projeto).
      console.error('[SweetAlert2] showConfirm indisponível. SweetAlert2 não carregado.');
      return false;
    }

    const result = await swal.fire(
      mixOptions(
        {
          title,
          text: normalizeText(text),
          icon,
          showCancelButton: true,
          confirmButtonText,
          cancelButtonText,
          ...DEFAULT_FIRE,
          customClass: {
            confirmButton: 'swal2-confirm',
            cancelButton: 'swal2-cancel',
          },
        },
        {}
      )
    );

    return !!result.isConfirmed;
  }

  async function showForm(
    {
      title = 'Formulário',
      html = '',
      confirmButtonText = 'Salvar',
      cancelButtonText = 'Cancelar',
      icon,
      focusConfirm = false,
      preConfirm,
      didOpen,
    } = {}
  ) {
    const swal = window.Swal;
    if (!swal) {
      console.error('[SweetAlert2] showForm indisponível. SweetAlert2 não carregado.');
      return null;
    }

    const result = await swal.fire(
      mixOptions(
        {
          title,
          html,
          icon,
          showCancelButton: true,
          confirmButtonText,
          cancelButtonText,
          focusConfirm,
          preConfirm,
          didOpen,
          ...DEFAULT_FIRE,
        },
        {}
      )
    );

    if (!result.isConfirmed) return null;
    return result.value !== undefined ? result.value : true;
  }

  function showSuccess(title = 'Sucesso', text) {
    const swal = window.Swal;
    if (!swal) {
      // SweetAlert2 não carregado: NÃO usar alert nativo.
      console.error('[SweetAlert2] showSuccess indisponível. SweetAlert2 não carregado.', { title, text });
      return;
    }

    return swal.fire({
      title,
      text: normalizeText(text),
      icon: 'success',
      ...DEFAULT_FIRE,
      timer: 2400,
      timerProgressBar: true,
      showConfirmButton: false,
    });
  }

  function showError(title = 'Erro', text) {
    const swal = window.Swal;
    if (!swal) {
      // SweetAlert2 não carregado: NÃO usar alert nativo.
      console.error('[SweetAlert2] showError indisponível. SweetAlert2 não carregado.', { title, text });
      return;
    }

    return swal.fire({
      title,
      text: normalizeText(text),
      icon: 'error',
      ...DEFAULT_FIRE,
    });
  }

  function showWarning(title = 'Atenção', text) {
    const swal = window.Swal;
    if (!swal) {
      // SweetAlert2 não carregado: NÃO usar alert nativo.
      console.error('[SweetAlert2] showWarning indisponível. SweetAlert2 não carregado.', { title, text });
      return;
    }

    return swal.fire({
      title,
      text: normalizeText(text),
      icon: 'warning',
      ...DEFAULT_FIRE,
    });
  }

  // Expor no window
  window.alerts = {
    showConfirm,
    showForm,
    showSuccess,
    showError,
    showWarning,
  };
})();

