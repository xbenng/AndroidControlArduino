package me.benthepro.androidcontrolarduino;

import android.content.Context;
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
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

//TODO add saving setup
public class MainControl extends ActionBarActivity {

    public TextView text;
    public Button upButtonLeft;
    public Button downButtonLeft;
    public Button upButtonRight;
    public Button downButtonRight;
    public Button updateAddress;

    public EditText addressField;
    public EditText portField;

    public EditText upPinLeft;
    public EditText downPinLeft;
    public EditText upPinRight;
    public EditText downPinRight;

    public Button addButton;
    public ArrayList pinControls = new ArrayList();

    public int PORT;
    public String ADDRESS;

    Socket socket = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_control);

        final Context context = this;

        text = (TextView) findViewById(R.id.textView);

        addressField = (EditText) findViewById(R.id.address);
        portField = (EditText) findViewById(R.id.port);

        upPinLeft = (EditText) findViewById(R.id.upPinLeft);
        downPinLeft = (EditText) findViewById(R.id.downPinLeft);
        upPinRight= (EditText) findViewById(R.id.upPinRight);
        downPinRight = (EditText) findViewById(R.id.downPinRight);

        upButtonLeft = (Button) findViewById(R.id.upButtonLeft);
        downButtonLeft = (Button) findViewById(R.id.downButtonLeft);
        upButtonRight = (Button) findViewById(R.id.upButtonRight);
        downButtonRight = (Button) findViewById(R.id.downButtonRight);

        updateAddress = (Button) findViewById(R.id.updateAddress);
        addButton = (Button) findViewById(R.id.button);

        setAddress();
        //TODO add ability to choose button background, and draggable layout

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinControls.add(new PinControl(pinControls.size()+1,context));
            }
        });
        updateAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAddress();
            }
        });

        upButtonLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    new ClientTask(ADDRESS, PORT, upPinLeft.getText().toString() + ",1").execute(); //pin, state
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    new ClientTask(ADDRESS,PORT, upPinLeft.getText().toString() + ",0").execute(); //pin, state
                }
                return false;
            }
        });
        downButtonLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    new ClientTask(ADDRESS,PORT, downPinLeft.getText().toString() + ",1").execute(); //pin, state
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    new ClientTask(ADDRESS,PORT, downPinLeft.getText().toString() + ",0").execute(); //pin, state
                }
                return false;
            }
        });
        upButtonRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    new ClientTask(ADDRESS,PORT, upPinRight.getText().toString() + ",1").execute(); //pin, state
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    new ClientTask(ADDRESS,PORT, upPinRight.getText().toString() + ",0").execute(); //pin, state
                }
                return false;
            }
        });
        downButtonRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    new ClientTask(ADDRESS,PORT, downPinRight.getText().toString() + ",1").execute(); //pin, state
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    new ClientTask(ADDRESS,PORT, downPinRight.getText().toString() + ",0").execute(); //pin, state
                }
                return false;
            }
        });


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
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public class ClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";
        String message;

        ClientTask(String addr, int port, String message){
            dstAddress = addr;
            dstPort = port;
            this.message = message;
        }

        @Override
        protected Void doInBackground(Void... arg0) {


            try {
                socket = new Socket(dstAddress, dstPort);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(message);
                //ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                //byte[] buffer = new byte[1024];

                //int bytesRead;
                //InputStream inputStream = socket.getInputStream();

    /*
     * notice:
     * inputStream.read() will block if no data return
     */
                //thisshitreads forever
                /*while ((bytesRead = inputStream.read(buffer)) != -1){
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                    System.out.println("Here." + response);
                }*/

                //read once and stop task
                //ledState = inputStream.read();

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
        EditText pinField;
        ToggleButton toggle;
        //TextView label;

        PinControl(int ID, Context context){

            TableRow tr = new TableRow(context);
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
            //pinField.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            pinField.setText(String.valueOf(ID));
            pinField.setInputType(InputType.TYPE_CLASS_NUMBER);

            toggle = new ToggleButton(context);
            toggle.setText("Toggle Pin");

            //add to row
            tr.addView(pinButton);
            tr.addView(pinField);
            tr.addView(toggle);

            //add row to table
            TableLayout table = (TableLayout) findViewById(R.id.table);
            table.addView(tr);



            pinButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (toggle.isChecked()) {
                        //TODO get pin state and switch it
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            ClientTask request = new ClientTask(ADDRESS,PORT, pinButton.getText().toString() + ",?");
                            request.execute();
                            //request.response;
                        }
                    } else {
                       //send switch on, on down
                       //send switch off, on up
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            new ClientTask(ADDRESS,PORT, pinButton.getText().toString() + ",1").execute(); //pin, state
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            new ClientTask(ADDRESS,PORT, pinButton.getText().toString() + ",0").execute(); //pin, state
                        }
                        System.out.println("here");
                    }
                    return false;
                }
            });
        }
    }
}
