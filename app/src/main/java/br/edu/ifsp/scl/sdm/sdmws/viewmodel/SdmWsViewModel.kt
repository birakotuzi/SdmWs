package br.edu.ifsp.scl.sdm.sdmws.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.edu.ifsp.scl.sdm.sdmws.model.Curso
import br.edu.ifsp.scl.sdm.sdmws.model.Disciplina
import br.edu.ifsp.scl.sdm.sdmws.model.Semestre
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.reflect.Method

class SdmWsViewModel(application: Application): AndroidViewModel(application) {
    val cursoMld: MutableLiveData<Curso> = MutableLiveData()
    val semestreMld: MutableLiveData<Semestre> = MutableLiveData()
    val disciplinaMld: MutableLiveData<Disciplina> = MutableLiveData()

    private val escopoCorrotinas = CoroutineScope(Dispatchers.IO + Job())
    private val filaRequisicoesVolley: RequestQueue = Volley.newRequestQueue(application.baseContext)

    companion object {
        val URL_BASE = "http://nobile.pro.br/sdm_ws"
        val ENDPOINT_CURSO = "/curso"
        val ENDPOINT_SEMESTRE =  "/semestre"
        val ENDPOINT_DISCIPLINA =  "/disciplina"
    }

    fun getCurso() {
        escopoCorrotinas.launch {
            val urlCurso = "${URL_BASE}${ENDPOINT_CURSO}"
            val requisicaoCursoJor = JsonObjectRequest(Request.Method.GET,
                                            urlCurso,
                                            null,
                                            { response ->
                                                if (response != null) {
                                                    val curso: Curso = jsonToCurso(response)
                                                    cursoMld.postValue(curso)
                                                    /*val curso: Curso = Curso (
                                                        response.getInt("horas"),
                                                        response.getString("nome"),
                                                        response.getInt("semestres"),
                                                        response.getString("sigla")
                                                    )*/
                                                    cursoMld.postValue(curso)
                                                }

                                                /* OU
                                                response?.also { resposta ->
                                                    resposta.getInt("horas"),
                                                    resposta.getString("nome"),
                                                    resposta.getInt("semestres"),
                                                    resposta.getString("sigla")
                                                }
                                                */

                                            },
                                            {error -> Log.e(urlCurso, error.toString()) }
                                        )
            filaRequisicoesVolley.add(requisicaoCursoJor)
        }
    }

    fun getSemestre(sid: Int) {
        escopoCorrotinas.launch {
            val urlSemestre = "${URL_BASE}${ENDPOINT_SEMESTRE}/$sid"
            val requisicaoSemestreJar = JsonArrayRequest(Request.Method.GET,
                urlSemestre,
                null,
                { response ->
                    response?.also { disciplinaJar ->
                        val semestre = Semestre()
                        for (indice in 0 until disciplinaJar.length()) {
                            val disciplinaJson = disciplinaJar.getJSONObject(indice)
                            val disciplina = jsonToDisciplina(disciplinaJson)
                            semestre.add(disciplina)
                        }
                        semestreMld.postValue(semestre)
                    }

                },
                {error -> Log.e(urlSemestre, error.toString()) }
            )
            filaRequisicoesVolley.add(requisicaoSemestreJar)
        }
    }

    fun getDisciplina(sigla: String) {

    }

    private fun jsonToCurso(json: JSONObject): Curso {
        val curso: Curso = Curso(
            json.getInt("horas"),
            json.getString("nome"),
            json.getInt("semestres"),
            json.getString("sigla")
        )
        return curso
    }

    private fun jsonToDisciplina(json: JSONObject): Disciplina {
        val disciplina: Disciplina = Disciplina(
            json.getInt("aulas"),
            json.getInt("horas"),
            json.getString("nome"),
            json.getString("sigla")
        )
        return disciplina
    }
}