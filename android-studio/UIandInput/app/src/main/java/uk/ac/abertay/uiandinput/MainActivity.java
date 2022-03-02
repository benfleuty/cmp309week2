package uk.ac.abertay.uiandinput;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    float[] hsv = new float[3];
    Toast lastToast;
    View thisActivity;

    private int moveLastVal = -1;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("btnColour_LastColour", findViewById(R.id.btnColourClickMe).getTag().toString());
        outState.putFloatArray("background_LastColour", hsv);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) restoreInstance(savedInstanceState);
        else setNewInstance();

        thisActivity = findViewById(R.id.rootLayout);
        bindEvents();
    }

    private void restoreInstance(Bundle instance) {
        restoreLayoutBackground(instance);
        restoreColourButtonBackground(instance);
    }

    private void restoreColourButtonBackground(Bundle instance) {
        String newBtnColour = instance.getString("btnColour_LastColour");
        if (newBtnColour.charAt(0) == '#') {
            Button btn = findViewById(R.id.btnColourClickMe);
            btn.setBackgroundColor(Color.parseColor(newBtnColour));
            btn.setTag(newBtnColour);
        }
    }

    private void restoreLayoutBackground(Bundle instance) {
        float[] newHsv = instance.getFloatArray("background_LastColour");
        if (newHsv != null) {
            hsv[0] = newHsv[0];
            hsv[1] = newHsv[1];
            hsv[2] = newHsv[2];
        } else {
            hsv[0] = 0.0f;
            hsv[1] = 0.0f;
            hsv[2] = 1.0f;
        }

        findViewById(R.id.rootLayout).setBackgroundColor(Color.HSVToColor(hsv));
    }


    private void setNewInstance() {
        hsv[0] = 0.0f; // Hue
        hsv[1] = 0.0f; // Saturation
        hsv[2] = 1.0f; // Value
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        changeBackgroundColour(event);
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        final int volDownBtn = 25;
        final int volUpBtn = 24;
        // only continue if the pressed buttons are vol up/down
        switch (keyCode) {
            case volDownBtn:
            case volUpBtn:
                break;
            default:
                return false;
        }

        if (keyCode == volDownBtn)
            turnBrightnessDown();
        else turnBrightnessUp();

        Toaster("Brightness is now " + hsv[2]);
        findViewById(R.id.rootLayout).setBackgroundColor(Color.HSVToColor(hsv));
        return true;
    }

    private void turnBrightnessUp() {
        float current = hsv[2];
        float newVal = current + 0.1f;
        if(newVal > 1) newVal = 1;
        hsv[2] = newVal;
    }

    private void turnBrightnessDown() {
        float current = hsv[2];
        float newVal = current - 0.1f;
        if(newVal < 0) newVal = 0;
        hsv[2] = newVal;
    }

    private void bindEvents() {
        // bind onTouchListener to colour button
        Button colourBtn = findViewById(R.id.btnColourClickMe);
        colourBtn.setOnTouchListener(MainActivity.this);
    }

    public void onKeyPadButtonPress(View view) {
        // get the clicked button's id
        int incoming_id = view.getId();
        Button clicked = findViewById(incoming_id);

        // get the value of the button's tag
        String clicked_tag = clicked.getTag().toString();

        MakeOutputToast(clicked_tag);

        // get the phone number input field
        EditText txtPhoneNumber = findViewById(R.id.txtPhoneNumber);

        ApplyActionTo_txtPhoneNumber(clicked_tag, txtPhoneNumber);
    }

    private void MakeOutputToast(String tag) {
        // setup the button press toast's output text
        String output = "You pressed: " + tag;
        // make and show the toast
        Toaster(output);
    }

    private void Toaster(String text) {
        Toaster(text, 0, true);
    }


    private void Toaster(int text) {
        Toaster(String.valueOf(text), 0, true);
    }

    private void Toaster(String text, boolean cancel) {
        Toaster(text, 0, cancel);
    }

    private void Toaster(String text, int duration) {
        Toaster(text, duration, true);
    }

    private void Toaster(String text, int duration, boolean cancel) {
        Toast new_toast = Toast.makeText(this, text, duration);
        // if a toast has been shown before
        // cancel so that the new toast can be shown
        if (cancel && lastToast != null) {
            lastToast.cancel();
        }

        new_toast.show();
        lastToast = new_toast;
    }

    private void ApplyActionTo_txtPhoneNumber(String tag, EditText txtPhoneNumber) {
        switch (tag) {
            case "0":
            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
            case "7":
            case "8":
            case "9":
                txtPhoneNumber.append(tag);
                break;
            case "clear":
                txtPhoneNumber.setText("");
                break;
            case "backspace":
                String og_text = txtPhoneNumber.getText().toString();
                // break if the length is less than one as there is nothing to remove
                if (og_text.length() < 1) break;
                String new_text = og_text.substring(0, og_text.length() - 1);
                txtPhoneNumber.setText(new_text);
                break;
            case "message":
            case "call":
                Toaster("You pressed " + tag);
                break;
            default:
                Toaster("WTF! That is not a button!");
                break;
        }

    }

    public void changeBackgroundColour(MotionEvent event) {
        View layoutRef = findViewById(R.id.rootLayout);
        float eventX = event.getX();
        float eventY = event.getY();
        float height = layoutRef.getHeight(); // make sure the ref is declared and initialised (this is a reference to your root layout)
        float width = layoutRef.getWidth();
        hsv[0] = eventY / height * 360; // (0 to 360)
        hsv[1] = eventX / width + 0.1f; // (0.1 to 1)
        layoutRef.setBackgroundColor(Color.HSVToColor(hsv));
        Toaster(Float.toString(eventX));
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.getId() == R.id.btnColourClickMe)
            handleColourButtonClick(motionEvent);

        return false;
    }

    private void handleColourButtonClick(MotionEvent motionEvent) {
        Button btn = findViewById(R.id.btnColourClickMe);

        String sGreen = "#00ff00";
        String sRed = "#ff0000";
        String sYellow = "#ffff00";

        int green = Color.parseColor(sGreen);
        int red = Color.parseColor(sRed);
        int yellow = Color.parseColor(sYellow);
        // Move sens calculation to prevent constant firing
        // https://stackoverflow.com/questions/12328867/motionevent-action-move-keeps-firing-x-and-y-even-if-not-moved

        int moveNewVal = (int) (motionEvent.getX() + motionEvent.getY());

        // if the action is move and the difference in position is more than 50 then cease processing
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE && Math.abs(moveLastVal - moveNewVal) < 50) {
            return;
        }
        moveLastVal = moveNewVal;

        int iNewColour = -1;
        String sNewColour = null;

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                iNewColour = green;
                sNewColour = sGreen;
                break;
            case MotionEvent.ACTION_MOVE:
                iNewColour = red;
                sNewColour = sRed;
                break;
            case MotionEvent.ACTION_UP:
                iNewColour = yellow;
                sNewColour = sYellow;
                break;
            default:
                Toaster("Unsupported MotionEvent!");

        }

        if (iNewColour == -1) return;

        btn.setBackgroundColor(iNewColour);
        btn.setTag(sNewColour);
    }
}