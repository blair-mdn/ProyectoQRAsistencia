package com.freddy.proyectoqrasistencia.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.freddy.proyectoqrasistencia.data.model.Alumno
import com.freddy.proyectoqrasistencia.data.repository.SupabaseRepository
import com.freddy.proyectoqrasistencia.databinding.ActivityMainBinding
import com.freddy.proyectoqrasistencia.ui.adapter.AlumnoAdapter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var alumnoAdapter: AlumnoAdapter
    private val repository = SupabaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            setupRecyclerView()
            loadAlumnos()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error en onCreate", e)
            Toast.makeText(this, "Error al iniciar la aplicación", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupRecyclerView() {
        try {
            alumnoAdapter = AlumnoAdapter(
                onGenerarQRClick = { alumno ->
                    val intent = Intent(this, QRActivity::class.java).apply {
                        putExtra("alumno_id", alumno.alumnoId.toString())
                        putExtra("nombre", alumno.nombre)
                        putExtra("dni", alumno.dni)
                    }
                    startActivity(intent)
                },
                onVerQRClick = { alumno ->
                    val intent = Intent(this, QRActivity::class.java).apply {
                        putExtra("alumno_id", alumno.alumnoId.toString())
                        putExtra("nombre", alumno.nombre)
                        putExtra("dni", alumno.dni)
                        putExtra("modo_visualizacion", true)
                    }
                    startActivity(intent)
                }
            )

            binding.recyclerViewAlumnos.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = alumnoAdapter
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error al configurar RecyclerView", e)
            Toast.makeText(this, "Error al configurar la lista", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadAlumnos() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                Log.d("MainActivity", "Iniciando carga de alumnos")
                val response = repository.getAlumnos()
                Log.d("MainActivity", "Respuesta recibida: $response")
                
                if (response != null) {
                    alumnoAdapter.setAlumnos(response)
                } else {
                    Toast.makeText(this@MainActivity, "No se encontraron alumnos", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error al cargar alumnos", e)
                Toast.makeText(this@MainActivity, "Error al cargar la lista de alumnos", Toast.LENGTH_LONG).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
} 