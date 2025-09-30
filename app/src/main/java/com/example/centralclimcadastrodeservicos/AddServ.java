package com.example.centralclimcadastrodeservicos;


import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddServ extends AppCompatActivity {

    // --- Declaração dos Componentes de Layout ---
    private AutoCompleteTextView etServiceTitle;
    private EditText etData;
    private EditText etHora;
    private AutoCompleteTextView etTecnico;

    // --- Variáveis do Banco de Dados ---
    private DatabaseHelper databaseHelper;

    // --- Variável para guardar a data e hora escolhidas ---
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_serv);

        // --- Inicialização dos Componentes de Layout ---
        etServiceTitle = findViewById(R.id.etServiceTitle);
        etData = findViewById(R.id.etData);
        etHora = findViewById(R.id.etHora);
        etTecnico = findViewById(R.id.etTecnico);
        Button btnSalvarServico = findViewById(R.id.btnSalvarServico);

        // --- Inicialização do Banco de Dados ---
        databaseHelper = new DatabaseHelper(this);

        // --- Carregar dados nos combo boxes ---
        popularServicos();
        popularTecnicosDoBanco();

        // --- Configuração dos Listeners de Clique ---
        etData.setOnClickListener(v -> abrirSeletorDeData());
        etHora.setOnClickListener(v -> abrirSeletorDeHora());
        btnSalvarServico.setOnClickListener(v -> salvarServicoNoBanco());
    }

    /**
     * Popula o combo box de serviços com uma lista fixa.
     */
    private void popularServicos() {
        String[] servicos = new String[]{
                "Limpeza Simples (Filtros e Carenagem)",
                "Limpeza Completa (Desmontagem da Evaporadora)",
                "Manutenção Preventiva",
                "Manutenção Corretiva (Diagnóstico de Defeito)",
                "Verificação e Carga de Gás Refrigerante",
                "Troca de Capacitor",
                "Instalação de Ar Condicionado"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, servicos);
        etServiceTitle.setAdapter(adapter);
    }

    /**
     * Busca os técnicos no banco de dados SQLite e os adiciona ao combo box.
     */
    private void popularTecnicosDoBanco() {
        List<String> nomesDosTecnicos = databaseHelper.getAllTecnicos();

        if (nomesDosTecnicos.isEmpty()) {
            Toast.makeText(this, "Nenhum técnico cadastrado.", Toast.LENGTH_SHORT).show();
            // Você pode querer desabilitar o campo ou o botão de salvar aqui
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, nomesDosTecnicos);
        etTecnico.setAdapter(adapter);
    }

    /**
     * Abre um diálogo para o usuário selecionar uma data.
     */
    private void abrirSeletorDeData() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            atualizarCampoData();
        };

        new DatePickerDialog(this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    /**
     * Abre um diálogo para o usuário selecionar uma hora.
     */
    private void abrirSeletorDeHora() {
        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            atualizarCampoHora();
        };

        new TimePickerDialog(this, timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true) // true para formato 24h
                .show();
    }

    /**
     * Formata e exibe a data no EditText.
     */
    private void atualizarCampoData() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        etData.setText(sdf.format(calendar.getTime()));
    }

    /**
     * Formata e exibe a hora no EditText.
     */
    private void atualizarCampoHora() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        etHora.setText(sdf.format(calendar.getTime()));
    }

    /**
     * Valida os campos e salva os dados do novo serviço no banco de dados SQLite.
     */
    private void salvarServicoNoBanco() {
        // --- Coleta dos dados dos campos ---
        String tituloServico = etServiceTitle.getText().toString().trim();
        String dataServico = etData.getText().toString().trim();
        String horaServico = etHora.getText().toString().trim();
        String tecnicoAtribuido = etTecnico.getText().toString().trim();

        // --- Validação dos campos ---
        if (tituloServico.isEmpty()) {
            etServiceTitle.setError("Selecione um título para o serviço.");
            etServiceTitle.requestFocus();
            return;
        }
        if (dataServico.isEmpty()) {
            etData.setError("Selecione uma data.");
            etData.requestFocus();
            return;
        }
        if (horaServico.isEmpty()) {
            etHora.setError("Selecione uma hora.");
            etHora.requestFocus();
            return;
        }
        if (tecnicoAtribuido.isEmpty()) {
            etTecnico.setError("Selecione um técnico.");
            etTecnico.requestFocus();
            return;
        }

        // --- Salvando no Banco de Dados ---
        boolean sucesso = databaseHelper.addServico(tituloServico, dataServico, horaServico, tecnicoAtribuido);

        if (sucesso) {
            Toast.makeText(this, "Serviço agendado com sucesso!", Toast.LENGTH_LONG).show();
            finish(); // Fecha a tela de adicionar serviço
        } else {
            Toast.makeText(this, "Falha ao agendar o serviço.", Toast.LENGTH_SHORT).show();
        }
    }
}