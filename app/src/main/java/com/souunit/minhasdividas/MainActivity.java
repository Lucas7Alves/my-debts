package com.souunit.minhasdividas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Objects;

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

        loadOneItem();

    }

    @Override
    protected void onStart() {
        super.onStart();

        //TODO: CARREGAR DADOS DA LISTA
    }


    // aux methods
    private void showItemPopup(Long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View popupview = LayoutInflater.from(this).inflate(R.layout.popup_show_item, null);

        TextView txtName = popupview.findViewById(R.id.txtName);
        TextView txtAmount = popupview.findViewById(R.id.txtAmount);
        TextView txtId = popupview.findViewById(R.id.txtId);
        Button btnDel = popupview.findViewById(R.id.btnDel);
        Button btnEdit = popupview.findViewById(R.id.btnEdit);
        Button btnPaid = popupview.findViewById(R.id.btnPaid);
        Button btnClose = popupview.findViewById(R.id.btnClose);

        FixedDebt debt = getActuallyDebt(id);

        if (debt != null) {
            txtName.setText(debt.getName());
            txtAmount.setText(String.format(Locale.getDefault(), "R$ %.2f", debt.getAmount()));
            txtId.setText(String.valueOf("ID: " + debt.getId()));
        } else {
            Toast.makeText(this, "Houve um erro ao carregar a dívida", Toast.LENGTH_SHORT).show();
            return;
        }

        builder.setView(popupview);
        Dialog dialog = builder.create();
        btnDel.setOnClickListener(v -> {
            dialog.dismiss();
            deleteDebt(id);
        });
        btnEdit.setOnClickListener(v -> {
            dialog.dismiss();
            editDebt(id);
        });
        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnPaid.setOnClickListener(v -> paidDebt(id));
        dialog.show();
    }

    private void paidDebt(Long id) {
        //TODO: logica "pago"
    }

    private FixedDebt getActuallyDebt(Long id) {
        Map<Long, FixedDebt> allDebts = manager.getFixedDebts();
        for (Map.Entry<Long, FixedDebt> entry : allDebts.entrySet()) {
            if (Objects.equals(entry.getValue().getId(), id)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private void editDebt(Long id) {
        //TODO: logica de edição
        Log.i("chegou", "antes de pegar o debt");
        FixedDebt oldDebt = getActuallyDebt(id);
        Log.i("chegou", "depois de pegar o debt");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_add_item, null);

        EditText etName = popupView.findViewById(R.id.etName);
        EditText etAmount = popupView.findViewById(R.id.etAmount);
        CalendarView etDate = popupView.findViewById(R.id.etDate);
        Button btnAdd = popupView.findViewById(R.id.btnAdd);

        etName.setText(oldDebt.getName());
        etDate.setDate(oldDebt.getDueDate().toEpochDay());
        etAmount.setText(String.valueOf(oldDebt.getAmount()));

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
            String newName = etName.getText().toString().trim();
            String newAmount = etAmount.getText().toString().trim();

            if (!newAmount.isEmpty() && !newName.isEmpty() && dayDate != 0) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                String newDateDue = formatter.format(LocalDate.of(yearDate, monthDate, dayDate));

                manager.updateFixedDebt(id, newName, Double.parseDouble(newAmount), LocalDate.parse(newDateDue, formatter));
                showItems();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private void deleteDebt(Long id) {
        Map<Long, FixedDebt> allDebts = manager.getFixedDebts();

        for (Long entry : allDebts.keySet()) {
            if (entry.equals(id)) {
                manager.deleteFixedDebt(id);
            }
        }
        showItems();
    }
    private void showAddItemPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_add_item, null);

        EditText etName = popupView.findViewById(R.id.etName);
        EditText etAmount = popupView.findViewById(R.id.etAmount);
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
            String name = etName.getText().toString().trim();
            String value = etAmount.getText().toString().trim();
            long fullDate = etDate.getDate();

            if (!value.isEmpty() && !name.isEmpty() && dayDate != 0) {
                //transforma a data no modelo DIA-MES-ANO
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                String dueDate = formatter.format(LocalDate.of(yearDate, monthDate, dayDate));

                //Cria um novo objeto com os dados da dívida
                manager.putFixedDebt(name, Double.parseDouble(value), LocalDate.parse(dueDate, formatter));
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

            card.setOnClickListener(v -> showItemPopup(debt.getId()));
            // adicione o card na lista
            debtList.addView(card);

            // adicione margens ao bottom de cada card
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) card.getLayoutParams();
            params.setMargins(0, 0, 0, 16);
            card.setLayoutParams(params);
        }
    }
    private void loadOneItem() {

        LinearLayout debtList = findViewById(R.id.debt_list);

        debtList.removeAllViews();

        FixedDebt debt = new FixedDebt(1L, "Frutiger Aero", 1000, LocalDate.now());
        View card = LayoutInflater.from(this).inflate(R.layout.card_frutiger_aero, debtList, false);

        TextView nameTextView = card.findViewById(R.id.name);
        TextView dayTextView = card.findViewById(R.id.dayOfMonth);
        TextView valueTextView = card.findViewById(R.id.value);

        nameTextView.setText(debt.getName());
        dayTextView.setText(String.valueOf(debt.getDueDate().getDayOfMonth()));
        valueTextView.setText(String.format(Locale.getDefault(), "R$ %.2f", debt.getAmount()));

        manager.putFixedDebt(debt);
        card.setOnClickListener(v -> showItemPopup(debt.getId()));
        debtList.addView(card);

        // adicione margens ao bottom de cada card
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) card.getLayoutParams();
        params.setMargins(0, 0, 0, 16);
        card.setLayoutParams(params);
    }

    private void initListeners() {
        edit_debt = findViewById(R.id.edit_debt);
        add_debt= findViewById(R.id.add_debt);
        del_debt = findViewById(R.id.del_debt);
    }
}