package com.example.applistatarefas.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.applistatarefas.database.TarefaDAO
import com.example.applistatarefas.databinding.ActivityAdicionarTarefaBinding

class AdicionarTarefaActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityAdicionarTarefaBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Recuperar tarefa passada
        var tarefa: com.example.applistatarefas.model.Tarefa? = null
        val bundle = intent.extras
        if( bundle != null ){
            tarefa = bundle.getSerializable("tarefa") as com.example.applistatarefas.model.Tarefa
            binding.editTarefa.setText( tarefa.descricao )
        }

        binding.btnSalvar.setOnClickListener {

            if ( binding.editTarefa.text.isNotEmpty() ){

                if( tarefa != null ){
                    editar( tarefa )
                }else{
                    salvar()
                }

            }else{
                Toast.makeText(
                    this,
                    "Preencha uma tarefa",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    }

    private fun editar(tarefa: com.example.applistatarefas.model.Tarefa) {

        val descricao = binding.editTarefa.text.toString()
        val tarefaAtualizar = com.example.applistatarefas.model.Tarefa(
            tarefa.idTarefa, descricao, "default"
        )
        val tarefaDAO = TarefaDAO(this)
        if( tarefaDAO.atualizar( tarefaAtualizar ) ){
            Toast.makeText(
                this,
                "Tarefa atualizada com sucesso",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }

    }

    private fun salvar() {

        val descricao = binding.editTarefa.text.toString()
        val tarefa = com.example.applistatarefas.model.Tarefa(
            -1, descricao, "default"
        )

        val tarefaDAO = TarefaDAO(this)
        if (tarefaDAO.salvar(tarefa)) {
            Toast.makeText(
                this,
                "Tarefa cadastrada com sucesso",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

}