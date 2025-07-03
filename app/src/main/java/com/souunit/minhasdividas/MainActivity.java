package com.souunit.minhasdividas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.souunit.minhasdividas.entities.DebtManager;
import com.souunit.minhasdividas.entities.FixedDebt;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private int dayDate, monthDate, yearDate;
    private DebtManager manager;
    private TextView edit_debt, add_debt, del_debt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.main_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        manager = new DebtManager(new HashMap<Long, FixedDebt>());

        initListeners();

        //TODO: popup de adicionar nova dívida
        edit_debt.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

             }
         });

        add_debt.setOnClickListener(v -> showAddItemPopup());

        del_debt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void initListeners() {
        edit_debt = findViewById(R.id.edit_debt);
        add_debt= findViewById(R.id.add_debt);
        del_debt = findViewById(R.id.del_debt);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //TODO: CARREGAR DADOS DA LISTA
        showItems();

    }

    private void showAddItemPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_add_item, null);

        EditText etNome = popupView.findViewById(R.id.etNome);
        EditText etValue = popupView.findViewById(R.id.etValue);
        CalendarView etDate = popupView.findViewById(R.id.etDate);
        Button btnAdd = popupView.findViewById(R.id.btnAdd);

        // pondo a data atual no calendário
        Calendar calendar = Calendar.getInstance();
        long currentDate = calendar.getTimeInMillis();
        etDate.setDate(currentDate, true, true);

        builder.setView(popupView);
        Dialog dialog = builder.create();

        etDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                dayDate = dayOfMonth;
                monthDate = month;
                yearDate = year;
            }
        });

        btnAdd.setOnClickListener(v -> {
            String nome = etNome.getText().toString().trim();
            String value = etValue.getText().toString().trim();
            long fullDate = etDate.getDate();

            if (!value.isEmpty() && !nome.isEmpty() && dayDate != 0) {
                //transforma a data no modelo DIA-MES-ANO
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                String dueDate = formatter.format(LocalDate.of(yearDate, monthDate, dayDate));

                //Cria um novo objeto com os dados da dívida
                manager.putFixedDebt(nome, Double.parseDouble(value), LocalDate.parse(dueDate, formatter));
                showItems();
                dialog.dismiss();
            } else {
                Toast.makeText(MainActivity.this, "Houve um erro ao criar a dívida", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private void showItems() {
        LinearLayout debtList = findViewById(R.id.debt_list);

        debtList.removeAllViews();

        // Map para manipular           Map preenchido
        Map<Long, FixedDebt> allDebts = manager.getFixedDebts();

        // Para cada objeto dentro do Map...
        for (Map.Entry<Long, FixedDebt> entry : allDebts.entrySet()) {
            FixedDebt debt = entry.getValue();


            // ... faça a criação do card  classe em questão
            //                              [layout do card],   [linearlayout dos cards] , [false para não adicionar ainda]
            View card = LayoutInflater.from(this).inflate(R.layout.card_frutiger_aero, debtList, false);

            // preencha pega a referência dos textViews
            TextView nameTextView = card.findViewById(R.id.name);
            TextView dayTextView = card.findViewById(R.id.dayOfMonth);
            TextView valueTextView = card.findViewById(R.id.value);

            // preencha os textViews do card
            nameTextView.setText(debt.getName());
            dayTextView.setText(String.valueOf(debt.getDueDate().getDayOfMonth()));
            valueTextView.setText(String.format(Locale.getDefault(), "R$ %.2f", debt.getAmount()));

            // adicione o card na lista
            debtList.addView(card);

            // adicione margens ao bottom de cada card
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) card.getLayoutParams();
            params.setMargins(0, 0, 0, 16);
            card.setLayoutParams(params);
        }
    }
}