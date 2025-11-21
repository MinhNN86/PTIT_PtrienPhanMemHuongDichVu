// Helper to decode JWT
function parseJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(jsonPayload);
    } catch (e) {
        return null;
    }
}

const auth = {
    login: async (username, password) => {
        try {
            const data = await api.login(username, password);
            localStorage.setItem('token', data.token);
            
            // Decode token to get role
            const decoded = parseJwt(data.token);
            console.log('Decoded Token:', decoded); // Debug log
            if (decoded && decoded.role) {
                console.log('Role found:', decoded.role); // Debug log
                localStorage.setItem('role', decoded.role);
                localStorage.setItem('username', decoded.sub);
            } else {
                console.error('Role not found in token');
            }
            
            window.location.href = 'dashboard.html';
        } catch (error) {
            document.getElementById('error-msg').style.display = 'block';
        }
    },

    logout: () => {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        localStorage.removeItem('username');
        window.location.href = 'index.html';
    },

    checkAuth: () => {
        const token = localStorage.getItem('token');
        if (!token) {
            window.location.href = 'index.html';
        }
        return {
            token: token,
            role: localStorage.getItem('role'),
            username: localStorage.getItem('username')
        };
    },

    isAdmin: () => {
        return localStorage.getItem('role') === 'ADMIN';
    }
};
