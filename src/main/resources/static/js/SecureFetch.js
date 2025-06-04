class SecureFetch {
    static async request(url, options = {}) {
        try {
            const response = await fetch(url, {
                ...options,
                credentials: 'include', // pour les cookies de session
            });

            // Si non autorisé → Swal + redirection
            if (response.status === 401) {
                await Swal.fire({
                    title: 'Session expirée',
                    text: 'Veuillez vous reconnecter',
                    icon: 'warning',
                    confirmButtonText: 'OK'
                });
                window.location.href = '/login';
                return null;
            }

            return response; // comportement normal sinon

        } catch (error) {
            // Gestion erreur réseau ou autre
            console.error('Erreur réseau :', error);
            await Swal.fire({
                title: 'Erreur',
                text: error.message || 'Une erreur est survenue',
                icon: 'error',
                confirmButtonText: 'OK'
            });
            window.location.href = '/login';
            throw error;
        }
    }
}
