package com.example.speech2text;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Debug;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;
 
/**
 * A very simple application to handle Voice Recognition intents
 * and display the results
 */
public class MainActivity extends Activity implements OnItemSelectedListener
{
 
    private static final int REQUEST_CODE = 1234;
    private static List<String> itemList = new ArrayList<String>(
            Arrays.asList("milk", "cabbage", "rice", "oil", "curd", "tomato","potato"));
    private EditText wordsList;
    private EditText kgsList;
    private EditText expList;
    private Spinner spinner1;
    private EditText txtView;
    private String initialDate;
    private String initialMonth;
    private String initialYear;
    private DatePickerDialog dialog = null;

    private final static String STORETEXT="storetext.txt";


 
    /**
     * Called with the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_recog);
 
        ImageButton speakButton = (ImageButton) findViewById(R.id.speakButton);
 
        wordsList = (EditText) findViewById(R.id.list);
        kgsList = (EditText) findViewById(R.id.kgs);

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        Button btn = (Button) findViewById(R.id.button1);
        txtView = (EditText) findViewById(R.id.textView1);
        txtView.setText("01/01/2014");
        

        addListenerOnSpinnerItemSelection();
        // Disable button if no recognition service is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
            speakButton.setEnabled(false);
            
        }
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar dtTxt = null;

              String preExistingDate = (String) txtView.getText().toString();
              
              if(preExistingDate != null && !preExistingDate.equals("")){
                  StringTokenizer st = new StringTokenizer(preExistingDate,"/");
                      initialMonth = st.nextToken();
                      initialDate = st.nextToken();
                      initialYear = st.nextToken();
                      if(dialog == null)
                      dialog = new DatePickerDialog(v.getContext(),
                                       new PickDate(),Integer.parseInt(initialYear),
                                       Integer.parseInt(initialMonth)-1,
                                       Integer.parseInt(initialDate));
                      dialog.updateDate(Integer.parseInt(initialYear),
                                       Integer.parseInt(initialMonth)-1,
                                       Integer.parseInt(initialDate));
                      
              } else {
                  dtTxt = Calendar.getInstance();
                  if(dialog == null)
                  dialog = new DatePickerDialog(v.getContext(),new PickDate(),dtTxt.getTime().getYear(),dtTxt.getTime().getMonth(),
                                                      dtTxt.getTime().getDay());
                  dialog.updateDate(dtTxt.getTime().getYear(),dtTxt.getTime().getMonth(),
                                                      dtTxt.getTime().getDay());
              }
                
                dialog.show();
            }
            
        });
        
    }
 
    /**
     * Handle the action of the button being clicked
     */
    private class PickDate implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                int dayOfMonth) {
            view.updateDate(year, monthOfYear, dayOfMonth);
            txtView.setText(monthOfYear+1+"/"+dayOfMonth+"/"+year);
            dialog.hide();
        }
        
    }
    public void saveButtonClicked(View v)
    {
    	try {
    		 
    		OutputStreamWriter out = 
    		new OutputStreamWriter(openFileOutput(STORETEXT, 0));
    		out.write(wordsList.getText().toString());
    		out.close();
    		Toast
    		.makeText(this, "The contents are saved in the file.", Toast.LENGTH_LONG)
    		.show();
    		wordsList.setText("");
    		}
    		 
    		catch (Throwable t) {
    		Toast
    		.makeText(this, "Exception: "+t.toString(), Toast.LENGTH_LONG)
    		.show();
    		}	
    }
    
    public void emailButtonClicked(View v)
    {
    	 Intent i = new Intent(Intent.ACTION_SEND);
         i.setType("message/rfc822");
         i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"srinath1984@gmail.com"});
         i.putExtra(Intent.EXTRA_SUBJECT, "VoiceCart");
         i.putExtra(Intent.EXTRA_TEXT   , wordsList.getText().toString());
         try {
             startActivity(Intent.createChooser(i, "Send mail..."));
         } catch (android.content.ActivityNotFoundException ex) {
             Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
         }
    }
    public void restoreButtonClicked(View v)
    
    {
    	try {
    		 
    		InputStream in = openFileInput(STORETEXT);
    		if (in != null) {
    			InputStreamReader tmp=new InputStreamReader(in);
    			BufferedReader reader=new BufferedReader(tmp);
    			String str;
    			StringBuilder buf=new StringBuilder();
    			while ((str = reader.readLine()) != null) {
    				buf.append(str+"\n");
    			}
    		 
    			in.close();
    			wordsList.setText(buf.toString());
    		}
    	}
    	catch (java.io.FileNotFoundException e) {
    		 
    		// that's OK, we probably haven't created it yet
    		 
    		}
    		 
    		catch (Throwable t) {
    		 
    		Toast
    		 
    		.makeText(this, "Exception: "+t.toString(), Toast.LENGTH_LONG)
    		 
    		.show();
    		 
    		}
    		 
    		}
 
    public void speakButtonClicked(View v)
    {
        startVoiceRecognitionActivity();
    }
    
    /**
     * Fire an intent to start the voice recognition activity.
     */
    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);

        startActivityForResult(intent, REQUEST_CODE);
    }
    public void addListenerOnSpinnerItemSelection(){
    	spinner1.setOnItemSelectedListener(this);
    }
    /**
     * Handle the results from the voice recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            // Populate the wordsList with the String values the recognition engine thought it heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String[] mStringArray = new String[matches.size()];
            mStringArray = matches.toArray(mStringArray);
            ArrayList<String> resList = new ArrayList<String>();
            String res = "";


            for(int i = 0; i < mStringArray.length ; i++){
  			  res+=mStringArray[i];
              break;
            	//for (String curVal : itemList){
      			  //Log.d("relevant item ",(mStringArray[i]));

            		//  if (curVal.contains(mStringArray[i])){
            			//  resList.add(curVal);
            			 // res+=curVal;
            		 // }
            	//	}
                
            }
            if (res != "" ) {
            	Log.d("relevant",wordsList.getText().toString());
            	if (wordsList.getText().toString().trim().length()==0) {
            		Log.d("relevant","here");
            		wordsList.setText(res);
            	} else {
                   wordsList.setText(wordsList.getText().toString() + '\n'+ res);
            	}
               kgsList.requestFocus();
            }
            
           
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void onItemSelected(AdapterView<?> parent, View view, int pos,
            long id) {
		  Log.d(" item ",parent.getItemAtPosition(pos).toString() );
		  String selected = parent.getItemAtPosition(pos).toString();
		  if (selected.equals("Email")) {
			  Log.d("relevant","email");
			  emailButtonClicked(view);
		  } else if(selected.equals("Save")) {
			  saveButtonClicked(view);
		  } else if(selected.equals("Restore")) {
			  restoreButtonClicked(view);
		  }

         
    		Toast.makeText(parent.getContext(), 
                "On Item Select : \n" + parent.getItemAtPosition(pos).toString(),
                Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
 
    }
   
}

