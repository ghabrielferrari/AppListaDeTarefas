package com.example.applistatarefas.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.applistatarefas.R
import com.example.applistatarefas.adapter.TarefaAdapter
import com.example.applistatarefas.database.TarefaDAO
import com.example.applistatarefas.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private var listaTarefas = emptyList<com.example.applistatarefas.model.Tarefa>()
    private var tarefaAdapter: TarefaAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //inicializarToolbar()

        binding.fabAdicionar.setOnClickListener {
            val intent = Intent(this, AdicionarTarefaActivity::class.java)
            startActivity( intent )
        }

        //Recyclerview
        tarefaAdapter = TarefaAdapter(
            { id -> confirmarExclusao(id) },
            { tarefa -> editar(tarefa) }
        )
        binding.rvTarefas.adapter = tarefaAdapter

        binding.rvTarefas.layoutManager = LinearLayoutManager(this)


    }

    private fun inicializarToolbar() {
        //val toolbar = binding.includeMainToolbar.tbPrincipal
        //setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Lista de Tarefas"
        }

        addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                    when (menuItem.itemId){
                        R.id.menu_sair -> {
                            deslogarUsuario()
                        }
                    }
                    return true
                }

            }
        )

    }

    private fun deslogarUsuario() {

        AlertDialog.Builder(this)
            .setTitle("Deslogar")
            .setMessage("Deseja realmente sair?")
            .setNegativeButton("Cancelar") { dialog, posicao -> }
            .setPositiveButton("Sim") { dialog, posicao ->
                firebaseAuth.signOut()
                startActivity(
                    Intent(applicationContext, LoginActivity::class.java)
                )
            }
            .create()
            .show()

    }

    private fun editar(tarefa: com.example.applistatarefas.model.Tarefa) {

        val intent = Intent(this, AdicionarTarefaActivity::class.java)
        intent.putExtra("tarefa", tarefa)
        startActivity( intent )

    }

    private fun confirmarExclusao(id: Int) {

        val alertBuilder = AlertDialog.Builder(this)

        alertBuilder.setTitle("Confirmar exclusão")
        alertBuilder.setMessage("Deseja realmente excluir a tarefas?")

        alertBuilder.setPositiveButton("Sim"){ _, _ ->

            val tarefaDAO = TarefaDAO(this)
            if ( tarefaDAO.remover( id ) ){
                atualizarListaTarefas()
                Toast.makeText(
                    this,
                    "Sucesso ao remover tarefa", Toast.LENGTH_SHORT
                ).show()
            }else{
                Toast.makeText(
                    this,
                    "Erro ao remover tarefa", Toast.LENGTH_SHORT
                ).show()
            }

        }

        alertBuilder.setNegativeButton("Não"){ _, _ -> }

        alertBuilder.create().show()

    }

    private fun atualizarListaTarefas(){

        val tarefaDAO = TarefaDAO(this)
        listaTarefas = tarefaDAO.listar()
        tarefaAdapter?.adicionarLista( listaTarefas )

    }

    override fun onStart() {
        super.onStart()
        atualizarListaTarefas()
    }

}