const express = require('express');
const { v4: uuidv4 } = require('uuid');

const app = express();
app.use(express.json());

class Student {
    constructor(HoTen, Lop) {
        this.ID = uuidv4().replace(/-/g, '').substring(0, 16);
        this.MSSV = Math.floor(10000000000 + Math.random() * 90000000000).toString();
        this.HoTen = HoTen;
        this.Lop = Lop;
        this.deleted = false;
    }
}

let students = [];

app.post('/students', (req, res) => {
    const { HoTen, Lop } = req.body;
    if (!HoTen || !Lop) {
        return res.status(400).json({ message: "HoTen và Lop là bắt buộc" });
    }
    const student = new Student(HoTen, Lop);
    students.push(student);
    res.status(201).json(student);
});

app.get('/students', (req, res) => {
    res.json(students.filter(s => !s.deleted));
});

app.get('/students/:id', (req, res) => {
    const student = students.find(s => s.ID === req.params.id && !s.deleted);
    if (!student) return res.status(404).json({ message: "Không tìm thấy sinh viên" });
    res.json(student);
});

app.put('/students/:id', (req, res) => {
    const { HoTen, Lop } = req.body;
    const student = students.find(s => s.ID === req.params.id && !s.deleted);
    if (!student) return res.status(404).json({ message: "Không tìm thấy sinh viên" });
    if (HoTen) student.HoTen = HoTen;
    if (Lop) student.Lop = Lop;
    res.json(student);
});

app.delete('/students/:id', (req, res) => {
    const student = students.find(s => s.ID === req.params.id && !s.deleted);
    if (!student) return res.status(404).json({ message: "Không tìm thấy sinh viên" });
    student.deleted = true;
    res.json({ message: "Đã xóa sinh viên" });
});

const PORT = 3000;
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});
