const KEY='students_v1';
const $ = id => document.getElementById(id);
let students = JSON.parse(localStorage.getItem(KEY) || '[]');
let editingId = null;

const form = $('student-form');
const body = $('students-body');
const search = $('search');

function save(){ localStorage.setItem(KEY, JSON.stringify(students)); }

function render(){
  const q = search.value.trim().toLowerCase();
  const filtered = students.filter(s =>
    !q || [s.name,s.roll,s.email,s.course].some(v => v.toLowerCase().includes(q))
  );
  body.innerHTML = filtered.map(s => `
    <tr>
      <td><strong>${esc(s.roll)}</strong></td>
      <td>${esc(s.name)}</td>
      <td>${esc(s.email)}</td>
      <td>${esc(s.course)}</td>
      <td>${esc(s.year)}</td>
      <td>${esc(s.phone||'-')}</td>
      <td>
        <button class="btn small edit" onclick="editStudent('${s.id}')">Edit</button>
        <button class="btn small delete" onclick="deleteStudent('${s.id}')">Delete</button>
      </td>
    </tr>
  `).join('');
  $('count').textContent = students.length;
  $('empty').classList.toggle('hidden', filtered.length > 0);
}

function esc(s){ return String(s).replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c])); }

form.addEventListener('submit', e => {
  e.preventDefault();
  const data = {
    id: editingId || crypto.randomUUID(),
    name: $('name').value.trim(),
    roll: $('roll').value.trim(),
    email: $('email').value.trim(),
    course: $('course').value,
    year: $('year').value,
    phone: $('phone').value.trim()
  };
  if(editingId){
    students = students.map(s => s.id === editingId ? data : s);
  } else {
    if(students.some(s => s.roll.toLowerCase() === data.roll.toLowerCase())){
      alert('A student with this Roll No already exists.'); return;
    }
    students.push(data);
  }
  save(); render(); resetForm();
});

window.editStudent = id => {
  const s = students.find(x => x.id === id); if(!s) return;
  editingId = id;
  $('student-id').value = id;
  $('name').value = s.name; $('roll').value = s.roll;
  $('email').value = s.email; $('course').value = s.course;
  $('year').value = s.year; $('phone').value = s.phone||'';
  $('form-title').textContent = 'Edit Student';
  $('submit-btn').textContent = 'Update Student';
  $('cancel-btn').classList.remove('hidden');
  window.scrollTo({top:0,behavior:'smooth'});
};

window.deleteStudent = id => {
  if(!confirm('Delete this student?')) return;
  students = students.filter(s => s.id !== id);
  save(); render();
  if(editingId === id) resetForm();
};

$('cancel-btn').addEventListener('click', resetForm);
search.addEventListener('input', render);

function resetForm(){
  editingId = null; form.reset(); $('student-id').value='';
  $('form-title').textContent = 'Add New Student';
  $('submit-btn').textContent = 'Add Student';
  $('cancel-btn').classList.add('hidden');
}

render();
