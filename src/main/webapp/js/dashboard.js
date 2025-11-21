document.addEventListener('DOMContentLoaded', async () => {
    const user = auth.checkAuth();
    document.getElementById('user-display').textContent = `Welcome, ${user.username} (${user.role})`;

    // Show/Hide Admin Controls
    const isAdmin = auth.isAdmin();
    console.log('Is Admin:', isAdmin); // Debug log
    if (!isAdmin) {
        console.log('Hiding admin controls'); // Debug log
        document.querySelectorAll('.admin-only').forEach(el => el.classList.add('hidden'));
    }

    // Load Data
    await loadStudents();
    await loadCourses();

    // Event Listeners
    document.getElementById('logoutBtn').addEventListener('click', auth.logout);
    
    if (auth.isAdmin()) {
        document.getElementById('addStudentBtn').addEventListener('click', handleAddStudent);
        document.getElementById('addCourseBtn').addEventListener('click', handleAddCourse);
        document.getElementById('calcTuitionBtn').addEventListener('click', handleCalcTuition);
    }
});

async function loadStudents() {
    try {
        const user = auth.checkAuth();
        // If Admin, get all. If Student, get only self (by username/studentCode)
        const students = await api.getStudents(auth.isAdmin() ? null : user.username);
        const tbody = document.getElementById('studentTableBody');
        tbody.innerHTML = '';
        
        students.forEach(student => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${student.id}</td>
                <td>${student.studentCode}</td>
                <td>${student.fullName}</td>
                <td>${student.major}</td>
                <td class="admin-only ${auth.isAdmin() ? '' : 'hidden'}">
                    <button class="action-btn delete-btn" onclick="handleDeleteStudent(${student.id})">Delete</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (error) {
        console.error('Error loading students:', error);
    }
}

async function loadCourses() {
    try {
        const courses = await api.getCourses();
        const tbody = document.getElementById('courseTableBody');
        tbody.innerHTML = '';
        
        courses.forEach(course => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${course.id}</td>
                <td>${course.name}</td>
                <td>${course.credits}</td>
                <td>${course.status}</td>
                <td class="admin-only ${auth.isAdmin() ? '' : 'hidden'}">
                    <button class="action-btn delete-btn" onclick="handleDeleteCourse(${course.id})">Delete</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (error) {
        console.error('Error loading courses:', error);
    }
}

// Handlers
async function handleAddStudent() {
    const fullName = document.getElementById('fullName').value;
    const studentCode = document.getElementById('studentCode').value;
    const major = document.getElementById('major').value;
    const password = document.getElementById('password').value;

    if (!fullName || !studentCode || !major || !password) {
        alert('Please fill all fields');
        return;
    }

    try {
        await api.addStudent({ fullName, studentCode, major, password });
        // Clear form
        document.getElementById('fullName').value = '';
        document.getElementById('studentCode').value = '';
        document.getElementById('major').value = '';
        document.getElementById('password').value = '';
        await loadStudents();
    } catch (error) {
        alert('Failed to add student');
    }
}

async function handleDeleteStudent(id) {
    if (confirm('Are you sure?')) {
        try {
            await api.deleteStudent(id);
            await loadStudents();
        } catch (error) {
            alert('Failed to delete student');
        }
    }
}

async function handleAddCourse() {
    const name = document.getElementById('courseName').value;
    const credits = document.getElementById('courseCredits').value;
    const status = document.getElementById('courseStatus').value;

    try {
        await api.addCourse({ name, credits: parseInt(credits), status });
        document.getElementById('courseName').value = '';
        document.getElementById('courseCredits').value = '';
        await loadCourses();
    } catch (error) {
        alert('Failed to add course');
    }
}

async function handleDeleteCourse(id) {
    if (confirm('Are you sure?')) {
        try {
            await api.deleteCourse(id);
            await loadCourses();
        } catch (error) {
            alert('Failed to delete course');
        }
    }
}

async function handleCalcTuition() {
    const credits = document.getElementById('tuitionCredits').value;
    const majorType = document.getElementById('tuitionMajor').value;
    
    try {
        const result = await api.calculateTuition(credits, majorType);
        document.getElementById('tuitionResult').textContent = result;
    } catch (error) {
        document.getElementById('tuitionResult').textContent = 'Error calculating';
    }
}

// Expose delete functions to global scope for onclick
window.handleDeleteStudent = handleDeleteStudent;
window.handleDeleteCourse = handleDeleteCourse;
