package com.freddy.proyectoqrasistencia.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.freddy.proyectoqrasistencia.data.model.Alumno
import com.freddy.proyectoqrasistencia.databinding.ItemAlumnoBinding

class AlumnoAdapter(
    private val onGenerarQRClick: (Alumno) -> Unit,
    private val onVerQRClick: (Alumno) -> Unit
) : RecyclerView.Adapter<AlumnoAdapter.AlumnoViewHolder>() {

    private var alumnos: List<Alumno> = emptyList()

    fun setAlumnos(newAlumnos: List<Alumno>) {
        alumnos = newAlumnos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlumnoViewHolder {
        val binding = ItemAlumnoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AlumnoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlumnoViewHolder, position: Int) {
        holder.bind(alumnos[position])
    }

    override fun getItemCount() = alumnos.size

    inner class AlumnoViewHolder(
        private val binding: ItemAlumnoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(alumno: Alumno) {
            binding.apply {
                tvNombre.text = alumno.nombre
                tvDni.text = "DNI: ${alumno.dni}"
                tvPrograma.text = alumno.programaEstudio

                btnGenerarQR.setOnClickListener {
                    onGenerarQRClick(alumno)
                }

                btnVerQR.setOnClickListener {
                    onVerQRClick(alumno)
                }
            }
        }
    }
} 