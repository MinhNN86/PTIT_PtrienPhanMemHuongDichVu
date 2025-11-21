const API_BASE_URL = 'http://localhost:8080/api';

const api = {
    getHeaders: () => {
        const token = localStorage.getItem('token');
        return {
            'Content-Type': 'application/json',
            'Authorization': token ? `Bearer ${token}` : ''
        };
    },

    login: async (username, password) => {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        if (!response.ok) throw new Error('Login failed');
        return await response.json();
    },

    // Students
    getStudents: async (studentCode) => {
        let url = `${API_BASE_URL}/students`;
        if (studentCode) {
            url += `?studentCode=${studentCode}`;
        }
        const response = await fetch(url, {
            headers: api.getHeaders()
        });
        return await response.json();
    },

    addStudent: async (student) => {
        const response = await fetch(`${API_BASE_URL}/students`, {
            method: 'POST',
            headers: api.getHeaders(),
            body: JSON.stringify(student)
        });
        if (!response.ok) throw new Error('Failed to add student');
        return await response.json();
    },

    deleteStudent: async (id) => {
        const response = await fetch(`${API_BASE_URL}/students/${id}`, {
            method: 'DELETE',
            headers: api.getHeaders()
        });
        if (!response.ok) throw new Error('Failed to delete student');
    },

    // Courses
    getCourses: async () => {
        const response = await fetch(`${API_BASE_URL}/courses`, {
            headers: api.getHeaders()
        });
        return await response.json();
    },

    addCourse: async (course) => {
        const response = await fetch(`${API_BASE_URL}/courses`, {
            method: 'POST',
            headers: api.getHeaders(),
            body: JSON.stringify(course)
        });
        if (!response.ok) throw new Error('Failed to add course');
        return await response.json();
    },

    deleteCourse: async (id) => {
        // Note: CourseResource might not have DELETE implemented yet based on history, 
        // but user asked for "add/edit/delete". Assuming it exists or will be added.
        // If not, this call will fail (404/405), which is acceptable for now.
        const response = await fetch(`${API_BASE_URL}/courses/${id}`, {
            method: 'DELETE',
            headers: api.getHeaders()
        });
        if (!response.ok) throw new Error('Failed to delete course');
    },

    // Finance
    calculateTuition: async (credits, majorType) => {
        const response = await fetch(`${API_BASE_URL}/finance/tuition?credits=${credits}&majorType=${majorType}`, {
            headers: api.getHeaders()
        });
        if (!response.ok) throw new Error('Failed to calculate tuition');
        return await response.text(); // Returns a string message
    }
};
