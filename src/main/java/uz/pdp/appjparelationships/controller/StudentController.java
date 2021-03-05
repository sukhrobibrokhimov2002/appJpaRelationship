package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    SubjectRepository subjectRepository;

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
        return studentPage;
    }

    //3. FACULTY DEKANAT
    @GetMapping("/forFaculty/{facultyId}")
    public Page<Student> getStudentForFaculty(@PathVariable Integer facultyId, @RequestParam Integer page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> allByGroup_faculty_id = studentRepository.findAllByGroup_Faculty_Id(facultyId, pageable);
        return allByGroup_faculty_id;
    }

    //4. GROUP OWNER
    @GetMapping("/forGroup/{groupId}")
    public List<Student> getAll(@PathVariable Integer groupId) {
        List<Student> allByGroup_id = studentRepository.findAllByGroup_Id(groupId);

        return allByGroup_id;
    }

    @PostMapping
    public String add(@RequestBody StudentDto studentDto) {
        List<Subject> subjectList = new ArrayList<>();

        Optional<Address> addressOptional = addressRepository.findById(studentDto.getAddressId());
        if (!addressOptional.isPresent()) return "Address not found";

        Optional<Group> groupOptional = groupRepository.findById(studentDto.getGroupId());
        if (!groupOptional.isPresent()) return "Group not found";

        List<Integer> subjectIds = studentDto.getSubjectIds();
        for (Integer subjectId : subjectIds) {
            Optional<Subject> subjectOptional = subjectRepository.findById(subjectId);
            if (!subjectOptional.isPresent()) return "Subject not found";
            subjectList.add(subjectOptional.get());
        }
        Student student = new Student();
        student.setAddress(addressOptional.get());
        student.setGroup(groupOptional.get());
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());
        student.setSubjects(subjectList);
        studentRepository.save(student);
        return "Student successfully added";
    }

    @DeleteMapping("/deleteStudentById/{id}")
    public String delete(@PathVariable Integer id) {
        try {
            studentRepository.deleteById(id);
            return "Student successfully deleted";
        } catch (Exception e) {
            return "Error deleting student";
        }
    }

    @PutMapping("/editStudent/{id}")
    public String edit(@PathVariable Integer id, @RequestBody StudentDto studentDto) {
        try {

            List<Subject> editedSubjectList = new ArrayList<>();

            Optional<Address> addressOptional = addressRepository.findById(studentDto.getAddressId());
            if (!addressOptional.isPresent()) return "Address not found";

            Optional<Group> groupOptional = groupRepository.findById(studentDto.getGroupId());
            if (!groupOptional.isPresent()) return "Group not found";

            List<Integer> subjectIds = studentDto.getSubjectIds();
            for (Integer subjectId : subjectIds) {
                Optional<Subject> subjectOptional = subjectRepository.findById(subjectId);
                if (!subjectOptional.isPresent()) return "Subject not found";
                editedSubjectList.add(subjectOptional.get());
            }
            Optional<Student> studentOptional = studentRepository.findById(id);
            if (!studentOptional.isPresent()) return "Student not found";
            Student student = studentOptional.get();
            student.setSubjects(editedSubjectList);
            student.setGroup(groupOptional.get());
            student.setAddress(addressOptional.get());
            student.setLastName(studentDto.getLastName());
            student.setFirstName(studentDto.getFirstName());
            studentRepository.save(student);
            return "Student successfully added";

        } catch (Exception e) {
            return "Error editing student";
        }
    }


}
