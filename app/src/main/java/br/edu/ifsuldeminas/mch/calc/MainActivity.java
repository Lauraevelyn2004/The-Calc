package br.edu.ifsuldeminas.mch.calc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.thecalc.R;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ifsuldeminas.mch.calc";
    private TextView textViewResultado;
    private TextView textViewUltimaExpressao;
    private String expressaoAtual = "";

    private boolean ultimoFoiIgual = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewResultado = findViewById(R.id.textViewResultadoID);
        textViewUltimaExpressao = findViewById(R.id.textViewUltimaExpressaoID);

        // Mapeando botões numéricos e a vírgula
        int[] botoesNumericos = {
                R.id.buttonZeroID, R.id.buttonUmID, R.id.buttonDoisID, R.id.buttonTresID,
                R.id.buttonQuatroID, R.id.buttonCincoID, R.id.buttonSeisID, R.id.buttonSeteID,
                R.id.buttonOitoID, R.id.buttonNoveID, R.id.buttonVirgulaID
        };

        // Listener para adicionar números à expressão
        View.OnClickListener listenerNumeros = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button botao = (Button) v;
                String valor = botao.getText().toString().replace(",", ".");

                // SE A ÚLTIMA TECLA FOI O "=", APAGA O RESULTADO ANTIGO E COMEÇA DE NOVO
                if (ultimoFoiIgual) {
                    expressaoAtual = "";
                    ultimoFoiIgual = false;
                }

                expressaoAtual += valor;
                textViewResultado.setText(expressaoAtual);
            }
        };

        for (int id : botoesNumericos) {
            findViewById(id).setOnClickListener(listenerNumeros);
        }

        // Mapeando botões de operação (+, -, *, /, %)
        int[] botoesOperacao = {
                R.id.buttonSomaID, R.id.buttonSubtracaoID, R.id.buttonMultiplicacaoID,
                R.id.buttonDivisaoID, R.id.buttonPorcentoID
        };

        // Listener para adicionar operadores.
        View.OnClickListener listenerOperacoes = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button botao = (Button) v;
                String operador = botao.getText().toString();

                if (operador.equals("÷")) {
                    operador = "/";
                }

                if (ultimoFoiIgual) {
                    ultimoFoiIgual = false;
                }

                if (expressaoAtual.length() > 0) {
                    char ultimoCaractere = expressaoAtual.charAt(expressaoAtual.length() - 1);

                    boolean isUltimoOperador = (ultimoCaractere == '+' || ultimoCaractere == '-' ||
                            ultimoCaractere == '*' || ultimoCaractere == '/');

                    if (isUltimoOperador && !operador.equals("%")) {
                        expressaoAtual = expressaoAtual.substring(0, expressaoAtual.length() - 1);
                    }

                    expressaoAtual += operador;
                    textViewResultado.setText(expressaoAtual);

                } else if (operador.equals("-")) {
                    expressaoAtual += operador;
                    textViewResultado.setText(expressaoAtual);
                }
            }
        };

        for (int id : botoesOperacao) {
            findViewById(id).setOnClickListener(listenerOperacoes);
        }

        //  Botão C (Reset)
        findViewById(R.id.buttonResetID).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expressaoAtual = "";
                ultimoFoiIgual = false;
                textViewResultado.setText("0");
                textViewUltimaExpressao.setText("");
            }
        });

        // Botão D (Delete)
        findViewById(R.id.buttonDeleteID).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expressaoAtual.length() > 0) {
                    expressaoAtual = expressaoAtual.substring(0, expressaoAtual.length() - 1);
                    ultimoFoiIgual = false; // Se a pessoa apagar um caractere, ela está editando, desliga a trava.

                    if (expressaoAtual.isEmpty()) {
                        textViewResultado.setText("0");
                    } else {
                        textViewResultado.setText(expressaoAtual);
                    }
                }
            }
        });

        // Botão Igual (=)
        findViewById(com.example.thecalc.R.id.buttonIgualID).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (expressaoAtual.isEmpty()) return;

                    String expressaoParaAvaliar = expressaoAtual;

                    // porcentagem
                    expressaoParaAvaliar = expressaoParaAvaliar.replaceAll("%(?=\\d)", "/100*");
                    expressaoParaAvaliar = expressaoParaAvaliar.replace("%", "/100");

                    Calculable avaliadorExpressao = new ExpressionBuilder(expressaoParaAvaliar).build();
                    Double resultado = avaliadorExpressao.calculate();

                    textViewUltimaExpressao.setText(expressaoAtual);

                    if (resultado % 1 == 0) {
                        expressaoAtual = String.valueOf(resultado.intValue());
                    } else {
                        expressaoAtual = String.valueOf(resultado);
                    }

                    textViewResultado.setText(expressaoAtual);

                    ultimoFoiIgual = true;

                } catch (Exception e) {
                    Log.d(TAG, "Erro ao avaliar expressão: " + e.getMessage());
                    textViewResultado.setText("Erro");
                }
            }
        });
    }
}