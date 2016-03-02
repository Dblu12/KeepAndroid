package com.izv.dam.keep;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import com.izv.dam.keep.adapter.ClaseAdaptador;
import com.izv.dam.keep.gestion.GestionKeep;
import com.izv.dam.keep.gestion.GestionUsuario;
import com.izv.dam.keep.pojo.Keep;
import com.izv.dam.keep.pojo.Usuario;
import com.izv.dam.keep.sqlitebd.GestorKeep;

import java.util.ArrayList;
import java.util.List;

public class Principal extends AppCompatActivity {

    private Usuario user;
    private List<Keep> listaNotas;
    private GestionKeep gk = new GestionKeep(this);
    private ClaseAdaptador cl;
    private boolean online = false;
    private ListView lv;
    private GestorKeep gkeep= new GestorKeep(this);
    private Toolbar toolbar;
    private List<Keep> listaNotasBD;


    /*
    Dia 19 - Queda por hacer:
        - Editar notas
        - Permitir añadir un dibujo - implementar el paint en el proyecto.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        //toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        //setSupportActionBar(toolbar);
        lv = (ListView) findViewById(R.id.listView);
        user = getIntent().getParcelableExtra("usuario");
        if (user != null) {
            Toast.makeText(this, user.getEmail(), Toast.LENGTH_LONG).show();
            online = true;
        }

        init();
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                          @Override
                                          public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                                              AlertDialog.Builder b = new AlertDialog.Builder(Principal.this);
                                              b.setMessage("¿Borrar?")
                                                      .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                                          @Override
                                                          public void onClick(DialogInterface dialog, int which) {

                                                              final Keep k = listaNotas.get(position);
                                                              Runnable r = new Runnable() {
                                                                  @Override
                                                                  public void run() {
                                                                      gk.deleteKeep(k, user);
                                                                  }
                                                              };
                                                              Thread t = new Thread(r);
                                                              t.start();
                                                              gkeep.delete(k);
                                                              listaNotas.remove(position);

                                                              notifyDataChanged();
                                                          }
                                                      })
                                                      .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                          @Override
                                                          public void onClick(DialogInterface dialog, int which) {

                                                          }
                                                      }).show();
                                              return false;
                                          }
                                      }
        );

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(Principal.this);
                LayoutInflater inflater = Principal.this.getLayoutInflater();
                View view = inflater.inflate(R.layout.add_keep, null);
                final EditText et = (EditText) view.findViewById(R.id.etAdd);
                adb.setView(view)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Keep k = listaNotas.get(position);
                                k.setContenido(et.getText().toString());
                                k.setEstado(false);
                                gkeep.updateContenido(k);
                                notifyDataChanged();
                                if (internetEnabled()) {
                                    AddKeepAsync a = new AddKeepAsync();
                                    a.execute();
                                    Log.v("xxxnotas2", listaNotas.toString());
                                }
                                notifyDataChanged();
                            }
                        })
                        .setNegativeButton("Cancelar", null).show();

                notifyDataChanged();
            }
        });
//        syncronice();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gkeep.close();
    }

    @Override
    protected void onResume() {
        gkeep.open();

        listaNotas= gkeep.select(null);
        cl = new ClaseAdaptador(Principal.this, R.layout.item, listaNotas);
        lv.setAdapter(cl);
        super.onResume();
    }

    public void init() {
        /*listaNotas = new ArrayList<>();

        for (int i = 0; i < 10 ; i++) {
            if( i % 2 == 0) {
                listaNotas.add(new Keep(i, "Mensaje " + i, false));
            }else {
                listaNotas.add(new Keep(i, "Mensaje " + i, true));
            }
        }



        Runnable a = new Runnable() {
            @Override
            public void run() {
                listaNotas = gk.getUserKeeps(user);

                Runnable x = new Runnable() {
                    @Override
                    public void run() {

                        Log.v("xxxnotas", listaNotas.toString());

                    }
                };
                Thread t = new Thread(x);
                t.start();
                cl = new ClaseAdaptador(Principal.this, R.layout.item, listaNotas);
                lv.setAdapter(cl);
            }
        };
        Thread b = new Thread(a);
        b.start();

*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_addy) {
            addKeep();
            return true;
        }else if (id== R.id.action_search){
            syncronice();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addKeep() {


        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.add_keep, null);
        final EditText et = (EditText) view.findViewById(R.id.etAdd);
        adb.setView(view)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Keep k= new Keep(gk.getNextAndroidId(listaNotas), et.getText().toString(), false);
                        listaNotas.add(new Keep(gk.getNextAndroidId(listaNotas), et.getText().toString(), false));
                        notifyDataChanged();
                        if (internetEnabled()) {
                            AddKeepAsync a = new AddKeepAsync();
                            a.execute();
                            Log.v("xxxnotas2", listaNotas.toString());
                        }
                        gkeep.insert(k);
                        notifyDataChanged();
                    }
                })
                .setNegativeButton("Cancelar", null).show();

        notifyDataChanged();
    }


    public boolean internetEnabled() {
        ConnectivityManager m = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        boolean is3g = m.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        boolean isWiFi = m.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

        if (is3g || isWiFi) {
            return true;
        }
        return false;
    }

    public void notifyDataChanged() {
        cl.notifyDataSetChanged();
    }


    private class AddKeepAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            listaNotas = gk.uploadKeeps(listaNotas, user);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            notifyDataChanged();
            cl = new ClaseAdaptador(Principal.this, R.layout.item, listaNotas);
            lv.setAdapter(cl);
        }
    }

    private class SyncAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            listaNotasBD = gk.getUserKeeps(user);
            List<Keep> ambas= new ArrayList<>();
            ambas.addAll(listaNotasBD);

            for(Keep k: listaNotas){
                if(!k.isEstado()){
                    ambas.add(k);
                }
            }
            listaNotas= ambas;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            notifyDataChanged();
            AddKeepAsync ak= new AddKeepAsync();
            ak.execute();
        }
    }

    private void syncronice(){
        SyncAsync syncAsync= new SyncAsync();
        syncAsync.execute();
    }

    public void adkeep(View v){
        addKeep();
    }

}
