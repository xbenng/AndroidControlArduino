package me.benthepro.androidcontrolarduino;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

//TODO future: add saving setup
public class MainControl extends ActionBarActivity {

    public TextView text;
    public Button updateAddress;

    public EditText addressField;
    public EditText portField;

    public ArrayList pinControls = new ArrayList();

    public int PORT;
    public String ADDRESS;

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_control);




        text = (TextView) findViewById(R.id.textView);

        addressField = (EditText) findViewById(R.id.address);
        portField = (EditText) findViewById(R.id.port);

        updateAddress = (Button) findViewById(R.id.updateAddress);

       /*TODO make into a class:
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
// Add the buttons
        builder.setPositiveButton("hi", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        builder.setNegativeButton("bye", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });


// Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
       /////////*/

        setAddress();
        //TODO future: add ability to choose button background, and draggable layout

        updateAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAddress();
            }
        });

        pinControls.add(new PinControl(pinControls.size()+1,context));



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_control, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.addButtonAction){
            pinControls.add(new PinControl(pinControls.size()+1,context));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setAddress() {
        PORT = Integer.parseInt(portField.getText().toString());
        ADDRESS = addressField.getText().toString();
        text.setText("Port: " + PORT + " Address: " + ADDRESS);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public class arduinoRequest extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";
        String message;
        Socket socket = null;
        Boolean toggle = false;
        int pinToSwitch;


        arduinoRequest(String addr, int port, int pinToSwitch, Boolean toggle){
            dstAddress = addr;
            dstPort = port;
            this.toggle = toggle;
            this.pinToSwitch = pinToSwitch;
        }
        arduinoRequest(String addr, int port, String message){
            dstAddress = addr;
            dstPort = port;
            this.message = message;
            //todo fix this method
        }
        @Override
        protected Void doInBackground(Void... arg0) {


            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(dstAddress,dstPort),1000);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                //ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                //byte[] buffer = new byte[1024];

                //int bytesRead;
                if (toggle) {
                    InputStream inputStream = socket.getInputStream();
                    out.println(pinToSwitch + ",2");
                    if (inputStream.read() == '0') {
                        out.println(pinToSwitch + ",1");
                    } else {
                        out.println(pinToSwitch + ",0");
                    }
                } else {
                    out.println(message);
                    //handle other method
                }

                //byteArrayOutputStream.write(buffer, 0, bytesRead);
                //response += byteArrayOutputStream.toString("UTF-8");


            } catch (UnknownHostException e) {
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }finally{
                if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            text.setText(response);
            super.onPostExecute(result);
        }

    }

    public class PinControl {
        Button pinButton;
        Button deleteButton;
        EditText pinField;
        ToggleButton toggle;
        TableLayout table = (TableLayout) findViewById(R.id.table);
        PinControl pinControl = this;

        //TextView label;

        PinControl(final int ID, Context context){

            final TableRow tr = new TableRow(context);
            tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

            /*//initialize label
            label = new TextView(context);
            label.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            label.setText("pin control");*/

            //initialize button
            pinButton = new Button(context);
            //pinButton.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            pinButton.setText(Integer.toString(ID));

            //initialize field
            pinField = new EditText(context);
            pinField.setText(String.valueOf(ID));
            pinField.setInputType(InputType.TYPE_CLASS_NUMBER);

            toggle = new ToggleButton(context);
            toggle.setText("Toggle Pin");
            toggle.setTextOn("Toggle Pin");
            toggle.setTextOff("Toggle Pin");

            deleteButton = new Button(context);
            deleteButton.setBackgroundResource(R.drawable.ic_action_cancel);

            //add to row
            tr.addView(pinButton);
            tr.addView(pinField);
            tr.addView(toggle);
            tr.addView(deleteButton);

            //add row to table
            table.addView(tr);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    table.removeView(tr);
                    pinControls.remove(pinControl);
                }
            });

            pinButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (toggle.isChecked()) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            new arduinoRequest(ADDRESS,PORT,Integer.parseInt(pinField.getText().toString()),true).execute();
                        }
                    } else {
                       //send switch on, on down
                       //send switch off, on up
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            new arduinoRequest(ADDRESS,PORT, pinField.getText().toString() + ",1").execute(); //pin, state
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            new arduinoRequest(ADDRESS,PORT, pinField.getText().toString() + ",0").execute(); //pin, state
                        }
                    }
                    return false;
                }
            });


        }
    }
//todo fill this

}
