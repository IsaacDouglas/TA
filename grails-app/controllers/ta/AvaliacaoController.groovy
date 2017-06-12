package ta

import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import org.springframework.web.multipart.MultipartRequest

@Transactional(readOnly = true)
class AvaliacaoController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Avaliacao.list(params), model:[avaliacaoInstanceCount: Avaliacao.count()]
    }

    def show(Avaliacao avaliacaoInstance) {
        respond avaliacaoInstance
    }

    def create() {
        respond new Avaliacao(params)
    }
    //-----------------------------------------

    def importarAvaliacao() {
        respond new Avaliacao(params)
    }

    String getPath(){
        def path = null

        String content = request.getContentType()
        if (content.contains("multipart/form-data") || (request instanceof MultipartHttpServletRequest)) {
            MultipartFile uploadedFile = request.getFile('sheet')
            if (!uploadedFile) {
                flash.message = "No attachment found for upload!"
            }else{
                flash.message = "File uploaded successfully."

                String nomeOriginal = uploadedFile.getOriginalFilename()

                if(nomeOriginal.contains(".xls")){
                    def completePath = servletContext.getRealPath('/')
                    File spreadsheet = new File(completePath, 'spreadsheets.xls')
                    uploadedFile.transferTo(spreadsheet)

                    path = spreadsheet.getAbsolutePath()
                    println("PATH - " + path)
                }else{
                    flash.message = "Arquivo não tem o formato .xls"
                }
            }
        } else {
            flash.message = "Unable to upload file, Bad Request!"
        }

        return path
    }

    def salvarAvaliacoes(){

        def path = getPath()

        //path para teste
        //def defaultPathBase = new File( "." ).getCanonicalPath()
        //println ("Current dir: " + defaultPathBase + "/test/resources/arq.xls")
        //path = defaultPathBase + "/test/resources/arq.xls"

        if (path != null){
            PlanilhaAvaliacao avaliacoes = PlanilhaFactory.getPlanilha(path, "avaliacao")

            String idTurma = (String)params.turma.id
            String nomeAvaliacao = params.nome
            def titulosPlanilha = avaliacoes.getTitulosPlanilha()

            def logins = avaliacoes.logins
            def metas = avaliacoes.metas

            def turma = Turma.findById(params.turma.id)
            def metasTurma = turma.metas



            for(int i=1; i<avaliacoes.sizeLinha; i++){

                String loginCin = avaliacoes.getLinha(i).get(0)
                Aluno aluno = Aluno.findByLoginCin(loginCin)
                Matricula matricula = Matricula.findByAluno(aluno)

                if(TurmaController.alunoEstaNaTurma(loginCin , turma)){
                    for (int j=1; j<avaliacoes.sizeColuna; j++){

                        def linhaPlanilha = avaliacoes.getLinha(i)
                        def meta = titulosPlanilha.get(j)
                        def conceito = linhaPlanilha.get(j)

                        if (TurmaController.metaEstaNaTurma(meta, turma)){
                            Avaliacao novaAvaliacao = new Avaliacao(nomeAvaliacao, meta, conceito, idTurma)
                            novaAvaliacao.save flush: true

                            //verificar se a avaliacao e a matricula existe no sistema
                            if(novaAvaliacao!=null && matricula!=null){
                                matricula.avaliacoes.add(Avaliacao.findById(novaAvaliacao.id))
                            }
                        }
                    }
                }
            }
        }

        redirect action:"index", method:"GET"
    }

    //-----------------------------------------

    @Transactional
    def save(Avaliacao avaliacaoInstance) {
        if (avaliacaoInstance == null) {
            notFound()
            return
        }

        if (avaliacaoInstance.hasErrors()) {
            respond avaliacaoInstance.errors, view:'create'
            return
        }

        avaliacaoInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'avaliacao.label', default: 'Avaliacao'), avaliacaoInstance.id])
                redirect avaliacaoInstance
            }
            '*' { respond avaliacaoInstance, [status: CREATED] }
        }
    }

    def edit(Avaliacao avaliacaoInstance) {
        respond avaliacaoInstance
    }

    @Transactional
    def update(Avaliacao avaliacaoInstance) {
        if (avaliacaoInstance == null) {
            notFound()
            return
        }

        if (avaliacaoInstance.hasErrors()) {
            respond avaliacaoInstance.errors, view:'edit'
            return
        }

        avaliacaoInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Avaliacao.label', default: 'Avaliacao'), avaliacaoInstance.id])
                redirect avaliacaoInstance
            }
            '*'{ respond avaliacaoInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Avaliacao avaliacaoInstance) {

        if (avaliacaoInstance == null) {
            notFound()
            return
        }

        avaliacaoInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Avaliacao.label', default: 'Avaliacao'), avaliacaoInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'avaliacao.label', default: 'Avaliacao'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
