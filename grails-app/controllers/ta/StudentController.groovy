package ta

import org.apache.ivy.core.settings.Validatable

import java.text.SimpleDateFormat
import java.lang.*

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class StudentController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    public static Date formattedDate(String dateInString) {
        def formatter = new SimpleDateFormat("dd/mm/yyyy");
        Date date = formatter.parse(dateInString);
        return date;
    }

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Student.list(params), model: [studentInstanceCount: Student.count()]
    }


    public void checkConditionPercentage(String loginA, Report reportInstance) {
        double aux = checkPercentageEvaluationStudent(reportInstance.avaliacao, loginA)
        def controllerRepo = new ReportController()
        if (aux >= reportInstance.valor) {
            Student student = Student.findByLogin(loginA)
            controllerRepo.addStudentToReport(student, reportInstance)
        }
    }

    public void checkConditionAverage(Student student, Report reportInstance) {
        def controllerRepo = new ReportController()
        if (checkTotalAverage(student.average)) {
            controllerRepo.addStudentToReport(student, reportInstance)
        }
    }

    public double checkPercentageEvaluationStudent(String evalValue, String loginA) {
        def student = Student.findByLogin(loginA)
        int contE = 0
        def evaluationLists = student.criteriaAndEvaluations;
        int tamanho = 0;
        for (int i = 0; i < evaluationLists.size(); i++) {
            def evaluat = evaluationLists[i].getEvaluations()
            tamanho += evaluat.size()
            for (int j = 0; j < evaluat.size(); j++) {
                if (evaluat.get(i).value.equalsIgnoreCase(evalValue)) {
                    contE++
                }
            }
        }
        return contE / tamanho
    }

    def updateAllAverages() {
        Student.list().each {
            it.calcMedia()
        }
    }

    public boolean checkTotalAverage(double mediaAluno) {
        double media = 0
        for (Student student : Student.list()) {
            media += student.average
        }
        media = media / Student.list().size()
        if (mediaAluno >= media) {
            return true
        } else {
            return false
        }
    }

    public boolean addEvaluationsToAllStudents(LinkedList<Evaluation> evaluationList) {
        for(Report reports : Report.list()){
            reports.students = []
        }
        for (int i = 0; i < Student.list().size(); i++) {
            Student.list().get(i).addEvaluation(evaluationList.get(i))
            Student.list().get(i).save(
                    flush: true,
                    failOnError: true
            )
            for (Report report : Report.list()) {
                if (report.tipo.equalsIgnoreCase("Porcentagem")) {
                    checkConditionPercentage(Student.list().get(i).login, report)
                } else {
                    checkConditionAverage(Student.list().get(i), report)
                }
            }
        }
        return true
    }
<<<<<<< HEAD
    def addEvaToStudents(String criterionName, LinkedList<Evaluation> evaluations){

        Student.listOrderByLogin().each { Student student ->
            student.addEvaluation(evaluations.poll());
        }
    }
    //PARA TESTE- CUCUMBER
    public boolean addEvaluationToAllStudents() {
        def evaluationInstance = new Evaluation(params);
        Student.findAll().each { Student student ->
=======

    public boolean addEvaluationToAllStudents() {
        def evaluationInstance = new Evaluation(params);
        for (Student student : Student.findAll()) {
>>>>>>> 49046cce259c367cf3df2ee6e9e160019f0268ed
            student.addEvaluation(evaluationInstance);
            student.save flush: true
        }
        return true
    }
<<<<<<< HEAD
    //PARA TESTE- CUCUMBER
    public void addEvaluationToStudent(String login){
=======

    public void addEvaluationToStudent(String login) {
>>>>>>> 49046cce259c367cf3df2ee6e9e160019f0268ed
        def student = Student.findByLogin(login);
        def evaluationInstance = new Evaluation(params);
        student.addEvaluation(evaluationInstance);
        student.save flush: true
    }

    public void addEvaluationToStudent2(String login, Date applicationDate) {
        def student = Student.findByLogin(login)
        def eval = Evaluation.findByApplicationDate(applicationDate)
        student.addEvaluation(eval)
        student.save flush: true
    }


<<<<<<< HEAD
    public void addCriterionToAllStudent(String description){
        def students = Student.findAll();
        for(int i =0; i<students.size();i++){
=======
    public void addCriterionToAllStudent(String description) {
        def students = Student.findAll();
        for (int i = 0; i < students.size(); i++) {
>>>>>>> 49046cce259c367cf3df2ee6e9e160019f0268ed
            def evCriterion = new EvaluationsByCriterion(Criterion.findByDescription(description));
            Student student = students.get(i);
            student.addEvaluationsByCriterion(evCriterion)
        }
    }

<<<<<<< HEAD
    public List<Evaluation> countStudentsEvaluated(String criterionName, String origin, String dateInString){
        List<Evaluation> returningValue = new LinkedList<>();
        def evaluation = new Evaluation(origin,null,this.formattedDate(dateInString),criterionName);
=======

    public double checkPorcentageEvaluationStudent(String evalValue, String loginA) {
        def student = Student.findByLogin(loginA)
        def contE = 0
        def evaluationLists = student.criteriaAndEvaluations;
        int tamanho = 0;
        for (int i = 0; i < evaluationLists.size(); i++) {
            def evaluations = evaluationLists.get(i).evaluations;
            for (int j = 0; j < evaluations.size(); j++) {
                tamanho += evaluations.size();
                if (evaluations.get(i).value == evalValue) {
                    contE++;
                }
            }
        }
        return contE / tamanho;
    }


    public List<Evaluation> countStudentsEvaluated(String criterionName, String origin, String dateInString) {
        List<Evaluation> returningValue = new LinkedList<>();
        def evaluation = new Evaluation(origin, null, this.formattedDate(dateInString), criterionName);
>>>>>>> 49046cce259c367cf3df2ee6e9e160019f0268ed
        def students = Student.findAll();
        for (int i = 0; i < students.size(); i++) {
            returningValue.add(students.get(i).findEvaluationByCriterion(criterionName).findSpecificEvaluation(evaluation))
        }
        return returningValue;
    }

<<<<<<< HEAD
    public boolean checkRedundantEvaluationAllStudents(String criterionName,String origin,String dateInString){
        def evaluation = new Evaluation(origin,null,this.formattedDate(dateInString),criterionName);
=======
    public boolean checkRedundantEvaluationAllStudents(String criterionName, String origin, String dateInString) {
        def evaluation = new Evaluation(origin, null, this.formattedDate(dateInString), criterionName)
>>>>>>> 49046cce259c367cf3df2ee6e9e160019f0268ed
        List<Student> students = Student.findAll();
        for (int i = 0; i < students.size(); i++) {
            def evCriterion = students.get(i).findEvaluationByCriterion(criterionName);
            if (evCriterion.findAll { it -> evCriterion.findSpecificEvaluation(evaluation) != null }.size() > 1) {
                return false
            }
        }
        return true
    }

<<<<<<< HEAD
    public boolean checkEvaluationsAllStudents(String criterionName, String origin, String dateInString){
       def evaluation = new Evaluation(origin,null,this.formattedDate(dateInString),criterionName);
       List<Student> students = Student.findAll()
       for(int i =0; i<students.size();i++){
           def evCriterion  = students.get(i).findEvaluationByCriterion(criterionName);
           return evCriterion.findSpecificEvaluation(evaluation) != null
       }
        return false;
=======
    public boolean checkEvaluationsAllStudents(String criterionName, String origin, String dateInString) {
        def evaluation = new Evaluation(origin, null, this.formattedDate(dateInString), criterionName);
        List<Student> students = Student.findAll()
        for (int i = 0; i < students.size(); i++) {
            def evCriterion = students.get(i).findEvaluationByCriterion(criterionName);
            if (evCriterion.findSpecificEvaluation(evaluation) != null) {
                return true;
            } else {
                return false
            }
        }
>>>>>>> 49046cce259c367cf3df2ee6e9e160019f0268ed
    }

    public int countAllStudents() {
        return Student.findAll().size();
    }

    public boolean saveStudent() {
        def studentInstance = new Student(params);
        if (Student.findByLogin(studentInstance.login) == null) {
            studentInstance.save flush: true
            return true
        }
        return false
    }
    public Student createAndSaveStudent(){
        Student student = new Student(params)
        if(Student.findByLogin(student.getLogin()) == null) {
            student.save flush: true
        }
        return student
    }

    public Student createAndSaveStudent() {
        Student student = new Student(params)
        if (Student.findByLogin(student.getLogin()) == null) {
            student.save flush: true
        }
        return student
    }

    def addEvaluation(Student studentInstance, String criterionName, Evaluation evaluationInstance) {
        def student = studentInstance;
        student.addEvaluation(evaluationInstance);
        student.save flush: true
    }

    public Student searchStudent() {
        def studentInstance = Student.findByLogin(params)
        return studentInstance
    }

    public Student createStudent() {
        return new Student(params)
    }

    def show(Student studentInstance) {
        respond studentInstance
    }

    def create() {
        respond new Student(params)
    }


    def search() {
        render view: "search"
    }

    def consult() {
        def auxList = Student.list()
        def studentList = auxList.findAll {
            it.name.toLowerCase().contains(params.consult.toLowerCase()) || it.login.toLowerCase().contains(params.consult.toLowerCase())
        }
        if (studentList == null) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'student.label', default: 'Student'), params.id])
            render view: "search", model: [studentInstanceList: [], studentInstanceCount: 0]
        } else {
            render view: "search", model: [studentInstanceList: studentList, studentInstanceCount: studentList.size()]
        }
    }

    @Transactional
    def save(Student studentInstance) {
        if (studentInstance == null) {
            notFound()
            return
        }
        if (studentInstance.hasErrors()) {
            respond studentInstance.errors, view: 'create'
            return
        }
        studentInstance.save flush: true
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'student.label', default: 'Student'), studentInstance.id])
                redirect studentInstance
            }
            '*' { respond studentInstance, [status: CREATED] }
        }
    }

    def edit(Student studentInstance) {
        respond studentInstance
    }

    @Transactional
    def update(Student studentInstance) {
        if (studentInstance == null) {
            notFound()
            return
        }
        if (studentInstance.hasErrors()) {
            respond studentInstance.errors, view: 'edit'
            return
        }

        studentInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'student.label', default: 'Student'), studentInstance.id])
                redirect studentInstance
            }
            '*' { respond studentInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Student studentInstance) {

        if (studentInstance == null) {
            notFound()
            return
        }

        studentInstance.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'student.label', default: 'Student'), studentInstance.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'student.label', default: 'Student'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }

    public void groupSave(List<Student> group) {
        for (int i = 0; i < group.size(); i++) {
            group.get(i).save flush: true;
        }
    }

    def saveGroup() {
        String group = params.name
        String[] students = group.split(";")
        for (int i = 0; i < students.size(); i++) {
            List<String> token1 = students[i].tokenize(':')
            String info = token1.get(0)
            List<String> token2 = info.tokenize('(')
            String name = token2.get(0)
            String login = token2.get(1)
            Student novo = new Student(name, login)
            novo.calcMedia()

            if (Student.findByLogin(novo.getLogin()) == null) {
                novo.save flush: true
            }
        }

        flash.message = message(code: 'default.created.message', args: [message(code: students.length, 'student.label', default: 'Student')])

        redirect action: "index", method: "GET"
    }

    def createGroup() {
        respond view: 'createGroup'
    }
}




