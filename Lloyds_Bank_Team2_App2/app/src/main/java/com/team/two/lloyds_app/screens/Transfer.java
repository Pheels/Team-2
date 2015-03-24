package com.team.two.lloyds_app.screens;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.team.two.lloyds_app.database.DatabaseAdapter;
import com.team.two.lloyds_app.R;
import com.team.two.lloyds_app.objects.Account;
import com.team.two.lloyds_app.objects.Customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//NOT USED ANYMORE

public class Transfer extends Activity {
    //UI references
    private Spinner from;
    private Spinner to;
    private EditText amountText;

    private static DatabaseAdapter dbadapter;
    private Customer customer;
    ArrayList<Account> accounts;
    private HashMap<String,Account> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        dbadapter = new DatabaseAdapter(this);
        customer = dbadapter.getCustomer(getIntent().getExtras().getInt("customerId"));
        accounts = dbadapter.getAccounts(customer.getId());
        mapAccounts();

       //from = (Spinner)findViewById(R.id.spinner_transfer_from);
       // to = (Spinner)findViewById(R.id.spinner_recipient);
        amountText = (EditText)findViewById(R.id.payment_amount_text);

        ArrayAdapter<String> fromAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, getListExcept(""));
        ArrayAdapter<String> toAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, getListExcept(""));

        from.setAdapter(fromAdapter);
        from.setOnItemSelectedListener(new CustomOnItemSelectedListener(toAdapter));

        to.setAdapter(toAdapter);
        to.setOnItemSelectedListener(new CustomOnItemSelectedListener());

    }

    public void transfer(View view){
        Account destination = map.get((String)to.getSelectedItem());
        Account source = map.get((String)from.getSelectedItem());
        Double balance = Double.parseDouble(amountText.getText().toString());
        CharSequence result = "Hello toast!";

        if (balance > 0){
            if (balance <= source.getAvailableBalance()){
                result = "Successful Transfer";
                dbadapter.transfer(source,destination, balance);
            } else {
                result = "Not enough available balance";
            }

        } else {
            result = "Balance can't be below 0";
        }

        Context context = getApplicationContext();

        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, result, duration);
        toast.show();
    }

    private void mapAccounts(){
        map = new HashMap<>();
        for (Account a: accounts){
            map.put(a.getAccountName(),a);
        }
    }

    private List<String> getListExcept(String except){
        List<String> list = new ArrayList<String>();
        for (Account a: accounts){
            if (!a.getAccountName().equals(except)){
                list.add(a.getAccountName());
            }
        }
        return list;
    }

    class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        private ArrayAdapter<String> dependent;

        public CustomOnItemSelectedListener(ArrayAdapter<String> dependent){
            this.dependent = dependent;
        }

        public CustomOnItemSelectedListener(){
            dependent = null;
        }
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selected = (String)parent.getSelectedItem();
            if (dependent != null) {
                dependent.clear();
                dependent.addAll(getListExcept(selected));
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}

