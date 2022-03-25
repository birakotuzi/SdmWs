package br.edu.ifsp.scl.sdm.sdmws.model

import com.google.gson.annotations.SerializedName

data class Curso(
    val horas: Int,
    val nome: String,
    val semestres: Int,
    val sigla: String
)