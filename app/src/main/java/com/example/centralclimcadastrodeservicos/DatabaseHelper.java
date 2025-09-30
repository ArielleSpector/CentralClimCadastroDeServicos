package com.example.centralclimcadastrodeservicos;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // --- Constantes do Banco de Dados ---
    public static final String DATABASE_NAME = "centralclim.db";
    public static final int DATABASE_VERSION = 1;

    // --- Constantes da Tabela de SERVIÇOS ---
    public static final String SERVICOS_TABLE = "SERVICOS";
    public static final String COLUMN_SERVICO_ID = "ID";
    public static final String COLUMN_SERVICO_TITULO = "TITULO";
    public static final String COLUMN_SERVICO_DATA = "DATA";
    public static final String COLUMN_SERVICO_HORA = "HORA";
    public static final String COLUMN_SERVICO_TECNICO = "TECNICO";
    public static final String COLUMN_SERVICO_STATUS = "STATUS";

    // --- Constantes da Tabela de TÉCNICOS ---
    public static final String TECNICOS_TABLE = "TECNICOS";
    public static final String COLUMN_TECNICO_ID = "ID";
    public static final String COLUMN_TECNICO_NOME = "NOME";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Chamado na primeira vez que o banco de dados é criado.
     * Aqui criamos a estrutura inicial das tabelas.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Script para criar a tabela de técnicos
        String createTableTecnicos = "CREATE TABLE " + TECNICOS_TABLE + " (" +
                COLUMN_TECNICO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TECNICO_NOME + " TEXT)";
        db.execSQL(createTableTecnicos);

        // Script para criar a tabela de serviços
        String createTableServicos = "CREATE TABLE " + SERVICOS_TABLE + " (" +
                COLUMN_SERVICO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SERVICO_TITULO + " TEXT, " +
                COLUMN_SERVICO_DATA + " TEXT, " +
                COLUMN_SERVICO_HORA + " TEXT, " +
                COLUMN_SERVICO_TECNICO + " TEXT, " +
                COLUMN_SERVICO_STATUS + " TEXT)";
        db.execSQL(createTableServicos);

        // ** OPCIONAL: Adiciona alguns técnicos de exemplo ao criar o banco **
        adicionarTecnicosIniciais(db);
    }

    /**
     * Chamado quando a versão do banco de dados (DATABASE_VERSION) é incrementada.
     * Útil para migrações e atualizações de estrutura.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SERVICOS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TECNICOS_TABLE);
        onCreate(db);
    }

    // --- Métodos para interagir com a Tabela de TÉCNICOS ---

    /**
     * Busca todos os nomes dos técnicos salvos no banco.
     * @return Uma lista de Strings com os nomes.
     */
    public List<String> getAllTecnicos() {
        List<String> listaDeTecnicos = new ArrayList<>();
        String queryString = "SELECT " + COLUMN_TECNICO_NOME + " FROM " + TECNICOS_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();

        try (Cursor cursor = db.rawQuery(queryString, null)) {
            if (cursor.moveToFirst()) {
                do {
                    String nomeTecnico = cursor.getString(0);
                    listaDeTecnicos.add(nomeTecnico);
                } while (cursor.moveToNext());
            }
        }
        return listaDeTecnicos;
    }

    // --- Métodos para interagir com a Tabela de SERVIÇOS ---

    /**
     * Adiciona um novo serviço agendado na tabela de serviços.
     * @param titulo O título do serviço.
     * @param data A data do serviço.
     * @param hora A hora do serviço.
     * @param tecnico O nome do técnico atribuído.
     * @return true se a inserção foi bem-sucedida, false caso contrário.
     */
    public boolean addServico(String titulo, String data, String hora, String tecnico) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_SERVICO_TITULO, titulo);
        cv.put(COLUMN_SERVICO_DATA, data);
        cv.put(COLUMN_SERVICO_HORA, hora);
        cv.put(COLUMN_SERVICO_TECNICO, tecnico);
        cv.put(COLUMN_SERVICO_STATUS, "Agendado"); // Status inicial padrão

        long insert = db.insert(SERVICOS_TABLE, null, cv);
        return insert != -1;
    }

    /**
     * Adiciona técnicos iniciais para teste.
     */
    private void adicionarTecnicosIniciais(SQLiteDatabase db) {
        String[] nomes = {"João da Silva", "Maria Oliveira", "Carlos Pereira"};
        for (String nome : nomes) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_TECNICO_NOME, nome);
            db.insert(TECNICOS_TABLE, null, cv);
        }
    }
}