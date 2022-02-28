package uk.ac.abertay.uiandinput;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    float[] hsv = new float[3];
    Toast last_toast;
    View thisActivity;

    private int moveLastVal = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hsv[0] = 0.0f; // Hue
        hsv[1] = 0.0f; // Saturation
        hsv[2] = 1.0f; // Value

        thisActivity = findViewById(R.id.rootLayout);
        bindEvents();
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
        float newVal = current + 10;
        if(newVal > 100) newVal = 100;
        hsv[2] = newVal;
    }

    private void turnBrightnessDown() {
        float current = hsv[2];
        float newVal = current - 10;
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
        if (cancel && last_toast != null) {
            last_toast.cancel();
        }

        new_toast.show();
        last_toast = new_toast;
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
        int green = Color.parseColor("#00ff00");
        int red = Color.parseColor("#ff0000");
        int yellow = Color.parseColor("#ffff00");
        // Move sens calculation to prevent constant firing
        // https://stackoverflow.com/questions/12328867/motionevent-action-move-keeps-firing-x-and-y-even-if-not-moved

        int moveNewVal = (int) (motionEvent.getX() + motionEvent.getY());

        // if the action is move and the difference in position is more than 50 then cease processing
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE && Math.abs(moveLastVal - moveNewVal) < 50) {
            return;
        }
        moveLastVal = moveNewVal;

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Toaster("Down changes the button colour to GREEN");
                btn.setBackgroundColor(green);
                break;
            case MotionEvent.ACTION_MOVE:
                Toaster("Move changes the button colour to RED");
                btn.setBackgroundColor(red);
                break;
            case MotionEvent.ACTION_UP:
                Toaster("Up changes the button colour to YELLOW");
                btn.setBackgroundColor(yellow);
                break;
            default:
                Toaster("Unsupported MotionEvent!");

        }
    }
}